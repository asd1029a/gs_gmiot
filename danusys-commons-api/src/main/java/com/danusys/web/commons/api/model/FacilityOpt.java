package com.danusys.web.commons.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/15
 * Time : 16:24
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "t_facility_opt")
public class FacilityOpt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityOptSeq;

    @Column(nullable = false)
    private Long facilitySeq;

    @Column(nullable = false)
    private String facilityOptName;

    @Column(nullable = false)
    private String facilityOptValue;

    @Column
    private Integer facilityOptType;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facilityOptType", referencedColumnName = "codeSeq", updatable = false, insertable = false)
    @JsonManagedReference
    private CommonCode commonCode;

    @Builder
    public FacilityOpt(Long facilitySeq, String facilityOptName, String facilityOptValue, int facilityOptType) {
        this.facilitySeq = facilitySeq;
        this.facilityOptName = facilityOptName;
        this.facilityOptValue = facilityOptValue;
        this.facilityOptType = facilityOptType;
    }

    public FacilityOpt setFacilityOpt(Long facilitySeq, String facilityOptName, String facilityOptValue, int facilityOptType){
        this.facilitySeq = facilitySeq;
        this.facilityOptName = facilityOptName;
        this.facilityOptValue = facilityOptValue;
        this.facilityOptType = facilityOptType;
        return this;
    }
}
