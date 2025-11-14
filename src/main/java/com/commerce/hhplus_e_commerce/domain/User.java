package com.commerce.hhplus_e_commerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private  Long userId;
    @Column(name = "user_name", nullable = false)
    private  String userName;
    @Column(name = "point")
    private int point;
    @Column(name = "created_dt", nullable = false)
    private  LocalDate createdDt;

    public User(String username, int point) {
        this.userName = username;
        this.point = point;
        this.createdDt = LocalDate.now();
    }

    public void payPoint(int amount){
        if(this.point<amount){
            throw new IllegalArgumentException("포인트 부족");
        }
        this.point -= amount;
    }
}
