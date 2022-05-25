package com.danusys.web.commons.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/03/17
 * Time : 11:03
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "t_event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventSeq;

    @Column(nullable = false)
    private Long facilitySeq;

    @Column
    private Long stationSeq;

    @Column
    private Long eventKind;

    @Column
    private Long eventGrade;

    @Column
    private Long eventProcStat;

//    @Column
//    private String eventAddress;

    @Column
    private Timestamp eventStartDt;
//
//    @Column
//    private Timestamp eventEndDt;

//    @Column
//    private String eventManager;
//
//    @Column
//    private Timestamp eventMngDt;

//    @Column
//    private String eventMngContent;

    @Column
    private String eventMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp insertDt;

    @Builder
    public Event(Long facilitySeq, Long stationSeq, Long eventKind, Long eventGrade, String eventMessage, Timestamp eventStartDt) {
        this.facilitySeq = facilitySeq;
        this.stationSeq = stationSeq;
        this.eventKind = eventKind;
        this.eventGrade = eventGrade;
        this.eventMessage = eventMessage;
        this.eventStartDt = eventStartDt;
    }
}
