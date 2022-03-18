package com.danusys.web.commons.api.model;

import lombok.Data;

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
@Table(name = "t_evemt")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long eventSeq;

    @Column(nullable = false)
    private int facilitySeq;

    @Column
    private int eventKind;

    @Column
    private String eventGrade;

    @Column
    private String eventProcStat;

    @Column
    private String eventAddress;

    @Column
    private Timestamp eventStartDt;

    @Column
    private Timestamp eventEndDt;

    @Column
    private String eventManager;

    @Column
    private Timestamp eventMngDt;

    @Column
    private String eventMngContent;

    @Column
    private Timestamp insertDt;

    private String facilityId;
}
