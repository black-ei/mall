package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_order_status")
public class OrderStatusEntity {
    @Id
    private Long orderId;
    private Integer status;
    private Date createTime;
    private Date paymentTime;
}
