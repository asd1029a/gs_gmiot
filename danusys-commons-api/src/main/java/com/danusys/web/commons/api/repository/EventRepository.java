package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/03/17
 * Time : 11:08
 */
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT code_seq FROM v_event_kind WHERE code_id = :codeId", nativeQuery = true)
    Long findEventKind(String codeId);
    @Query(value = "SELECT code_seq FROM v_event_grade WHERE code_id = :codeId", nativeQuery = true)
    Long findEventGrade(String codeId);
    @Query(value = "SELECT code_seq FROM v_event_proc_stat WHERE code_id = :codeId", nativeQuery = true)
    Long findEventProcStat(String codeId);
}
