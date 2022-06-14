package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.dto.EventReqeustDTO;
import com.danusys.web.commons.api.model.Event;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.repository.EventRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/03/17
 * Time : 11:09
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final FacilityRepository facilityRepository;
    private final SseService sseService;

    public Event save(Event event) {
        return this.eventRepository.save(event);
    }

    public List<Event> saveAll(List<Event> list) {
        return this.eventRepository.saveAll(list);
    }

    public List<Event> saveAllByEventRequestDTO(List<EventReqeustDTO> list) throws Exception {
        List<Event> eventList = new ArrayList<>();

        list.forEach(f -> {
            Facility facility = facilityRepository.findByFacilityId(f.getFacilityId());
            f.setFacilitySeq(facility.getFacilitySeq());
            f.setStationSeq(facility.getStationSeq());
            Event e = f.toEntity();
            eventList.add(e);
        });

        eventRepository.saveAll(eventList);
        return eventList;
    }

    public Event saveByEventRequestDTO(EventReqeustDTO eventReqeustDTO) throws Exception {
        Facility facility = facilityRepository.findByFacilityId(eventReqeustDTO.getFacilityId());
        eventReqeustDTO.setFacilitySeq(facility.getFacilitySeq());
        eventReqeustDTO.setStationSeq(facility.getStationSeq());
        Long eventKind = eventRepository.findEventKind(eventReqeustDTO.getEventKind());
        Long eventGrade = eventRepository.findEventGrade(eventReqeustDTO.getEventGrade() == null ? "10" : eventReqeustDTO.getEventGrade());
        String msgConv = eventRepository.msgConv(eventKind);
        Event event = eventReqeustDTO.toEntity(eventKind, eventGrade);
        event.setEventMessage(msgConv);
        eventRepository.save(event);
        return event;
    }

    public Long findEventKind(String codeId) {
        return this.eventRepository.findEventKind(codeId);
    }

    public Long findEventGrade(String codeId) {
        return this.eventRepository.findEventGrade(codeId);
    }

    public Long findEventProcStat(String codeId) {
        return this.eventRepository.findEventProcStat(codeId);
    }

    public String findParentKind(Long eventKind) {
        return eventRepository.findParentKindStr(eventKind);
    }

    public List<String> findByParentCodeValue(String parentCodeValue) {
        return eventRepository.findByParentCodeValue(parentCodeValue);
    }
}
