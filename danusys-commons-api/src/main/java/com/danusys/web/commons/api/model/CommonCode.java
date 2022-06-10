package com.danusys.web.commons.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/02
 * Time : 3:45 오후
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "t_common_code")
public class CommonCode implements Serializable {

    private static final long serialVersionUID = -7937474629818980256L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long codeSeq;

    @Column(nullable = false)
    private String codeId;

    @Column(nullable = false)
    private String codeName;

    @Column(nullable = false)
    private String codeValue;

    @Column(nullable = false)
    private Long parentCodeSeq;

    @Column
    private String useKind;

    @Column
    private Timestamp insertDt;

    @Column
    private Integer insertUserSeq;

    @Column
    private Time updateDt;

    @Column
    private Integer updateUserSeq;
}
