package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.Event;
import com.danusys.web.commons.app.EgovMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/03/17
 * Time : 11:08
 */
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT code_seq FROM v_event_kind WHERE code_id = :codeId", nativeQuery = true)
    Long findEventKind(@Param("codeId") String codeId);
    @Query(value = "SELECT code_seq FROM v_event_grade WHERE code_id = :codeId", nativeQuery = true)
    Long findEventGrade(@Param("codeId") String codeId);
    @Query(value = "SELECT code_seq FROM v_event_proc_stat WHERE code_id = :codeId", nativeQuery = true)
    Long findEventProcStat(@Param("codeId") String codeId);
    @Query(value = "SELECT distinct t2.code_name FROM t_event t1 INNER JOIN t_common_code t2 on " +
            "t1.event_kind = t2.code_seq WHERE t1.event_kind = :kind",nativeQuery = true)
    String msgConv(@Param("kind") Long kind);

    @Query(value = "SELECT a.code_value FROM v_event_kind a JOIN (SELECT parent_code_seq FROM v_event_kind WHERE code_seq = :eventKind) b ON a.code_seq = b.parent_code_seq", nativeQuery = true)
    String findParentKindStr(@Param("eventKind") Long eventKind);
}
