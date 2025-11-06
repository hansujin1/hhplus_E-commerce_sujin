package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.PointType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PointHis {
    private Long point_hisId;
    private Long user_id;
    private PointType type;
    private int point;
    private Date created_dt;

    public PointHis() {}

    public PointHis(Long point_hisId, Long user_id, PointType type, int point, Date created_dt) {
        this.point_hisId = point_hisId;
        this.user_id = user_id;
        this.type = type;
        this.point = point;
        this.created_dt = created_dt;
    }
}
