package com.danusys.web.commons.api.scheduler.types;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/06/10
 * Time : 10:06
 */
public enum FacilityKindType {

    LIGHT(92L,"전등", Arrays.asList("lamp")),
    AIR_CONDITIONER(91L,"에어컨", Arrays.asList("air_con")),
    AIR_CLEANER(93L,"공기청정기",Arrays.asList("air_cleaner")),
    AIR_SCAN(131L,"대기측정기",Arrays.asList("air_scan")),
    BUS_STOP( 99L,"무정차전광판", Arrays.asList("bus_stop")),
    SMART_CHAIR(96L, "스마트의자",Arrays.asList("bench")),
    SIGN(58L, "사이니지", Arrays.asList("sign")),
    CHARGER(120L,"충전기", Arrays.asList("charger")),
    AUTO_DOOR(98L,"자동문" ,Arrays.asList("door1", "door2", "door3")),
    EMPTY(-1L,"등록된 시설물코드 없음",Collections.EMPTY_LIST);

    //common code seq
    @Getter
    private Long commonCodeSeq;
    @Getter
    private String title;
    private List<String> facilityTypeList;

    FacilityKindType(Long commonCodeSeq, String title, List<String> facilityTypeList) {
        this.commonCodeSeq = commonCodeSeq;
        this.title = title;
        this.facilityTypeList = facilityTypeList;
    }

    public static FacilityKindType findFacilityKind(String code) {
        return Arrays.stream(FacilityKindType.values())
                .filter(facilityKindType -> facilityKindType.hasFacilityKindType(code))
                .findAny()
                .orElse(EMPTY);
    }

    private boolean hasFacilityKindType(String code) {
        return facilityTypeList.stream()
                .anyMatch(kind -> kind.equals(code));
    }
}
