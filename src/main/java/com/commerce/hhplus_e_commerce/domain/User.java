package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class User {
    private final Long userId;
    private final String userName;
    private int point;
    private final LocalDate createdDt;

    public User(Long userId,String username, int point,LocalDate createdDt) {
        this.userId = userId;
        this.userName = username;
        this.point = point;
        this.createdDt = createdDt;
    }

    public void payPoint(int amount){
        if(this.point<amount){
            throw new IllegalArgumentException("포인트 부족");
        }
        this.point -= amount;
    }
}
