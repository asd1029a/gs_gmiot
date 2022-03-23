package com.danusys.web.commons.api.dto;

import com.danusys.web.commons.api.model.Event;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.Query;

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
    private Long facilitySeq;
    private String facilityId;
    private int eventKind;
    private String eventKindNm;
    private String eventGrade;
    private String eventMessage;
}
