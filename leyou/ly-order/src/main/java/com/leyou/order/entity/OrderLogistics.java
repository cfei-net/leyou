package com.leyou.order.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_order_logistics")
public class OrderLogistics {
    @Id
    private Long orderId; // 订单id，与订单表一对一
    private String logisticsNumber; // 物流单号
    private String logisticsCompany;//物流名称
    private String addressee;//收件人
    private String phone;//手机号
    private String province; // 省
    private String city; // 市
    private String district; // 区
    private String street; //街道
    private String postcode; // 邮编
    private Date createTime;
    private Date updateTime;
}