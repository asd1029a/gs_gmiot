package com.danusys.web.commons.api.dto;

import com.danusys.web.commons.api.model.Event;
import com.danusys.web.commons.api.repository.EventRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class EventReqeustDTO {
    private final EventRepository eventRepository;
    private Long facilitySeq;
    private Long stationSeq;
    private String facilityId;
    private String eventKind;
    private String eventGrade;
    private String eventMessage;

    public Event toEntity() {
        return Event.builder()
                .facilitySeq(this.facilitySeq)
                .stationSeq(this.stationSeq)
                .eventKind(eventRepository.findEventKind(this.eventKind))
                .eventGrade(eventRepository.findEventGrade(this.eventGrade))
                .eventMessage(this.eventMessage)
                .build();
    }
}
