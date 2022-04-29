package com.danusys.web.commons.api.dto;

import com.danusys.web.commons.api.model.Event;
import com.danusys.web.commons.api.model.FacilityOpt;
import lombok.Getter;
import lombok.Setter;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/04/22
 * Time : 10:06
 */
@Getter
@Setter
public class FacilityDataRequestDTO {
    private String facilityId;
    private Long facilitySeq;
    private String facilityOptValue;
    private String facilityOptName;
    private int facilityOptType;

    public FacilityOpt toEntity() {
        return FacilityOpt.builder()
                .facilitySeq(this.facilitySeq)
                .facilityOptName(this.facilityOptName)
                .facilityOptValue(this.facilityOptValue)
                .facilityOptType(this.facilityOptType)
                .build();
    }
}
