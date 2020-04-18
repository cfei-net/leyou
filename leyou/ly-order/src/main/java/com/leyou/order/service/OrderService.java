package com.leyou.order.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadlocal.UserHolder;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;
import com.leyou.order.dto.CartDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.entity.Order;
import com.leyou.order.entity.OrderDetail;
import com.leyou.order.entity.OrderLogistics;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderLogisticsMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.vo.OrderDetailVO;
import com.leyou.order.vo.OrderLogisticsVO;
import com.leyou.order.vo.OrderVO;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.AddressDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderLogisticsMapper logisticsMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private UserClient userClient;

    /**
     * 创建订单接口
     * @param orderDTO      订单DTO对象
     * @return              订单编号
     */
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        // 1、生成订单编号
        long orderId = idWorker.nextId();
        // 2、保存数据到订单表
        order.setOrderId(orderId);
        order.setPostFee(0L); // 全场包邮
        order.setPaymentType(orderDTO.getPaymentType()); // 页面有
        order.setUserId(UserHolder.getUser().getId()); // 获取当前线程上的用户id
        order.setStatus(OrderStatusEnum.INIT.value()); // 初始化未付款
        order.setInvoiceType(0); // 发票类型(0无发票1普通发票，2电子发票，3增值税发票)
        order.setSourceType(2); //订单来源：1:app端，2：pc端，3：微信端
        // =======================================================================================
        // 2.0 计算总金额： （每个sku的价格 * 数量 ） 然后再全部相加起来就是总金额
        List<CartDTO> carts = orderDTO.getCarts();
        // 2.1 为了能够通过skuid获取他购买的数量，我们可以把List集合转成Map
        Map<Long, Integer> cartMap = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        // 2.2 收集skuid
        List<Long> skuIds = carts.stream().map(CartDTO::getSkuId).collect(Collectors.toList());
        // 2.3 远程调用商品微服务查询sku
        List<SkuDTO> skuDTOList = itemClient.querySkuListByIds(skuIds);
        // 2.4 收集每个sku的价格 ,最后求和
        long totalFee = skuDTOList.stream().mapToLong(skuDTO -> skuDTO.getPrice() * cartMap.get(skuDTO.getId())).sum();
        // =======================================================================================
        order.setTotalFee(totalFee); // TODO 总金额
        order.setActualFee(1L);  // TODO 注意：实际支付 = 总金额 - 活动金额；现在为了付款所以写死1分钱
        int count = orderMapper.insertSelective(order);
        if(count != 1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        // 3、保存数据到订单条目表：批量
        List<OrderDetail> detailList = new ArrayList<>();
        for (SkuDTO s : skuDTOList) {
            // 创建订单条目
            OrderDetail od = new OrderDetail();
            od.setId(idWorker.nextId());
            od.setOrderId(orderId);
            od.setSkuId(s.getId());
            od.setTitle(s.getTitle());
            od.setNum(cartMap.get(s.getId())); // 数量
            od.setPrice(s.getPrice());
            od.setOwnSpec(s.getOwnSpec());
            od.setImage(StringUtils.substringBefore(s.getImages(),","));// 指的是截取到逗号之前的字符串
            od.setCreateTime(new Date());
            od.setUpdateTime(new Date());
            detailList.add(od);
        }
        count = orderDetailMapper.insertList(detailList);// 批量插入
        if(count != skuDTOList.size()){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        // 4、保存物流表的信息
        AddressDTO addressDTO = userClient.queryAddressById(UserHolder.getUser().getId(), orderDTO.getAddressId());
        OrderLogistics orderLogistics = BeanHelper.copyProperties(addressDTO, OrderLogistics.class);
        orderLogistics.setOrderId(orderId);
        count = logisticsMapper.insertSelective(orderLogistics);
        if(count != 1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        // 5、减去库存
        itemClient.minusStock(cartMap);
        // 6、返回订单编号
        return orderId;
    }

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    public OrderVO queryOrderById(Long id) {
        // 1、查询订单
        Order order = orderMapper.selectByPrimaryKey(id);
        // 1.1 判空
        if(order == null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        // 1.2 判断订单是谁的？
        if(!UserHolder.getUser().getId().equals(order.getUserId())){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        // 2、查询订单详情
        OrderDetail record = new OrderDetail();
        record.setOrderId(order.getOrderId());
        List<OrderDetail> orderDetailList = orderDetailMapper.select(record);
        if(CollectionUtils.isEmpty(orderDetailList)){
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        // 3、查询物流信息
        OrderLogistics orderLogistics = logisticsMapper.selectByPrimaryKey(order.getOrderId());
        if(orderLogistics==null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        // 4、封装OrderVO返回
        OrderVO orderVO = BeanHelper.copyProperties(order, OrderVO.class);
        orderVO.setLogistics(BeanHelper.copyProperties(orderLogistics, OrderLogisticsVO.class));
        orderVO.setDetailList(BeanHelper.copyWithCollection(orderDetailList, OrderDetailVO.class));

        // 5、返回信息
        return orderVO;
    }
}
