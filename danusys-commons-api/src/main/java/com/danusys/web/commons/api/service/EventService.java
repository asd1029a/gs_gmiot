package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.Event;
import com.danusys.web.commons.api.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event save(Event event) {
        return this.eventRepository.save(event);
    }
}
