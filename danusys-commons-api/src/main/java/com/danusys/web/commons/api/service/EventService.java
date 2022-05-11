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

    public List<Event> saveAllByEventRequestDTO(List<EventReqeustDTO> list) throws Exception {
        List<Event> eventList = new ArrayList<>();

        list.forEach(f -> {
            Facility facility = facilityRepository.findByFacilityId(f.getFacilityId());
            f.setFacilitySeq(facility.getFacilitySeq());
            f.setStationSeq(facility.getStationSeq());
            eventList.add(f.toEntity());
        });

        eventRepository.saveAll(eventList);
        return eventList;
    }

    public Event saveByEventRequestDTO(EventReqeustDTO eventReqeustDTO) throws Exception {
        Facility facility = facilityRepository.findByFacilityId(eventReqeustDTO.getFacilityId());
        eventReqeustDTO.setFacilitySeq(facility.getFacilitySeq());
        eventReqeustDTO.setStationSeq(facility.getStationSeq());

        Event event = eventReqeustDTO.toEntity();

        eventRepository.save(event);
        return event;
    }
}
