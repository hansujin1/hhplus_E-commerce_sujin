package com.commerce.hhplus_e_commerce.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    private Long user_id;
    private String user_name;
    private int point;
    private Date created_dt;

    public User() {}

    public User(Long userId,String username, int point,Date created_dt) {
        this.user_id = userId;
        this.user_name = username;
        this.point = point;
        this.created_dt = created_dt;
    }

}
