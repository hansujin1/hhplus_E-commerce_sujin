package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class User {
    private final Long user_id;
    private final String user_name;
    private int point;
    private final LocalDate created_dt;

    public User(Long userId,String username, int point,LocalDate created_dt) {
        this.user_id = userId;
        this.user_name = username;
        this.point = point;
        this.created_dt = created_dt;
    }

    public void payPoint(int amount){
        if(this.point<amount){
            throw new IllegalArgumentException("포인트 부족");
        }
        this.point -= amount;
    }
}
