package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.PointType;
import lombok.Getter;

import java.util.Date;

@Getter
public class PointHis {
    private Long point_hisId;
    private final Long user_id;
    private final PointType type;
    private final int point;
    private final Date created_dt;

    public PointHis(Long point_hisId, Long user_id, PointType type, int point, Date created_dt) {
        this.point_hisId = point_hisId;
        this.user_id = user_id;
        this.type = type;
        this.point = point;
        this.created_dt = created_dt;
    }

    public void pointHisId(Long pointHisId) {
        this.point_hisId = pointHisId;
    }
}
