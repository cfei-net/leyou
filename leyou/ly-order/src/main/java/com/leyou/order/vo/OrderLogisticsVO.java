package com.leyou.order.vo;

import lombok.Data;

@Data
public class OrderLogisticsVO {
    private Long orderId;
    private String logisticsNumber;//物流单号
    private String logisticsCompany;//物流名称
    private String addressee;//收件人
    private String phone;//手机号
    private String province;//省
    private String city;//市
    private String district;//区
    private String street;//街道
    private String postcode;//邮编
}