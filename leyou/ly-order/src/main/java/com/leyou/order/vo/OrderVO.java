package com.leyou.order.vo;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Data
public class OrderVO {
    @Id
    private Long orderId; // 订单编号
    private Long totalFee;//商品金额
    private Long postFee = 0L;//邮费
    private Long actualFee;//实付金额
    private Integer paymentType;//付款方式：1:在线支付, 2:货到付款
    private String promotionIds; // 优惠促销的活动id，
    private Long userId; //用户id
    private Integer status; // 订单状态
    private Date createTime;// 创建时间
    private Date payTime; //付款时间
    private Date consignTime;//发货时间
    private Date endTime;//确认收货时间
    private Date closeTime;//交易关闭时间
    private Date commentTime;//评价时间
    private Integer invoiceType = 0;//发票类型，0无发票，1普通发票，2电子发票，3增值税发票
    private Integer sourceType = 1;//订单来源 1:app端，2：pc端，3：微信端
    private OrderLogisticsVO logistics; //订单物流信息
    private List<OrderDetailVO> detailList;//订单详情信息
}