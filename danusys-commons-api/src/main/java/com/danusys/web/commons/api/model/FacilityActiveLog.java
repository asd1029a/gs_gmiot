package com.danusys.web.commons.api.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "t_facility_active_log")
public class FacilityActiveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityActiveLogSeq;

    private Long facilitySeq;

    private boolean facilityActiveCheck;

    private String facilityActiveIp;

}
