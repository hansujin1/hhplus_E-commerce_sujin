package com.commerce.hhplus_e_commerce.domain;

import com.commerce.hhplus_e_commerce.domain.enums.PointType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "point_his")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointHis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_his_id")
    private Long pointHisId;
    @Column(name = "user_id" , nullable = false)
    private  Long userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "type" , nullable = false)
    private  PointType type;
    @Column(name = "point")
    private  int point;
    @Column(name = "created_dt" , nullable = false)
    private LocalDate createdDt;

    public PointHis(Long pointHisId, Long userId, PointType type, int point) {
        this.pointHisId = pointHisId;
        this.userId = userId;
        this.type = type;
        this.point = point;
        this.createdDt = LocalDate.now();
    }

    public void pointHisId(Long pointHisId) {
        this.pointHisId = pointHisId;
    }
}
