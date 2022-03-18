package com.danusys.web.commons.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/03/18
 * Time : 14:09
 */
@Getter
@Setter
public class EventReqeustDTO {
    private String facilityId;
    private String eventKind;
    private int eventGrade;
    private String eventMessage;
}
