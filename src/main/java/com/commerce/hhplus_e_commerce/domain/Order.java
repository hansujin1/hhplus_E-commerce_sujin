package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
public class Order {
    private Long order_id;
    private Long user_id;
    private int total_price;
    private int discount_price;
    private int final_price;
    private int cancelled_price;
    private Date created_dt;
    private Date paid_dt;
    private Long userCouponId;


    public Order() {}

    public Order(Long order_id, Long user_id, int total_price, int discount_price, int final_price,
                 int cancelled_amount, Date created_dt, Date paid_dt,Long userCouponId) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.total_price = total_price;
        this.discount_price = discount_price;
        this.final_price = final_price;
        this.cancelled_price = cancelled_amount;
        this.created_dt = created_dt;
        this.paid_dt = paid_dt;
        this.userCouponId = userCouponId;
    }

}
