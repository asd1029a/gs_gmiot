package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.dto.EventReqeustDTO;
import com.danusys.web.commons.api.model.Event;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.repository.EventRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
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
public class EventService {
    private EventRepository eventRepository;
    private FacilityRepository facilityRepository;

    public EventService(EventRepository eventRepository
            , FacilityRepository facilityRepository) {
        this.eventRepository = eventRepository;
        this.facilityRepository = facilityRepository;
    }

    public Event save(Event event) {
        return this.eventRepository.save(event);
    }

    public List<Event> saveAllByEeventRequestDTO(List<EventReqeustDTO> list) {
        List<Event> eventList = new ArrayList<>();

        list.forEach(f -> {
            Facility facility = facilityRepository.findByFacilityId(f.getFacilityId());
            Event event = Event
                    .builder()
                    .facilitySeq(facility.getFacilitySeq())
                    .eventKind(eventRepository.findEventKind(f.getEventKindNm()))
                    .eventGrade(f.getEventGrade()).build();

            eventList.add(event);
        });

        eventRepository.saveAll(eventList);
        return eventList;
    }
}
