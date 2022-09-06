package com.danusys.web.commons.sqlconverter.model.postgresql;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Data
@Table(name = "t_facility_seq")
public class TestFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_seq")
    private Long facilitySeq;

    @Column(name = "latitude")
    private int latitude;

    @Column(name = "longitude")
    private int longitude;
}
