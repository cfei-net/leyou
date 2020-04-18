package com.leyou.order.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.service.OrderService;
import com.leyou.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * @param orderDTO          订单DTO接参用的
     * @param bindingResult     接收异常信息：HibernateValidateble中的内容：可以参考UserController
     * @return                  订单的ID
     */
    @PostMapping("/order")
    public ResponseEntity<Long> createOrder(@Valid @RequestBody OrderDTO orderDTO, BindingResult bindingResult){
        // 判断是否有异常
        if(bindingResult.hasErrors()){
            String errMsg = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(" | "));
            throw new LyException(402, errMsg);
        }
        // 正常调用
        Long orderId = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    /**
     * 根据订单编号去查询订单信息
     * @param id    订单编号
     * @return      订单VO
     */
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderVO> queryOrderById(@PathVariable("id") Long id){
        OrderVO orderVO = orderService.queryOrderById(id);
        return ResponseEntity.ok(orderVO);
    }
}
