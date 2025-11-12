package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.PointType;
import lombok.Getter;

import java.util.Date;

@Getter
public class PointHis {
    private Long pointHisId;
    private final Long userId;
    private final PointType type;
    private final int point;
    private final Date createdDt;

    public PointHis(Long pointHisId, Long userId, PointType type, int point, Date createdDt) {
        this.pointHisId = pointHisId;
        this.userId = userId;
        this.type = type;
        this.point = point;
        this.createdDt = createdDt;
    }

    public void pointHisId(Long pointHisId) {
        this.pointHisId = pointHisId;
    }
}
