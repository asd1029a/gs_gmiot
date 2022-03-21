package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/03/17
 * Time : 11:08
 */
public interface EventRepository extends JpaRepository<Event, Long> {
}
