package com.leyou.order.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_order_detail")
public class OrderDetail {
    @Id
    private Long id;
    private Long orderId; // 订单编号
    private Long skuId; // 商品id
    private Integer num;// 商品购买数量
    private String title;//商品标题
    private Long price; // 商品单价
    private String ownSpec; // 商品规格数据
    private String image; // 图片
    private Date createTime;
    private Date updateTime;
}