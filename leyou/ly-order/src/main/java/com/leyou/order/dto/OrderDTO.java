package com.leyou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @NotNull(message = "收件人地址不能为空")
    private Long addressId; // 收获人地址id
    @NotNull(message = "付款类型不能为空")
    private Integer paymentType;// 付款类型
    @NotNull(message = "购物车列表不能为空")
    private List<CartDTO> carts;// 订单详情
}