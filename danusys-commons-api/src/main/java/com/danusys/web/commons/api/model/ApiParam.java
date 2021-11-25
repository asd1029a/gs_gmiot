package com.danusys.web.commons.api.model;

import com.danusys.web.commons.api.types.DataType;
import com.danusys.web.commons.api.types.ParamType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 1:16 오후
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api_param")
public class ApiParam implements Serializable {

    private static final long serialVersionUID = -7877521422884001681L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int seq;

    @Column(nullable = false)
    private String fieldNm;

    @Column(nullable = false)
    private String fieldMapNm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataType dataType;

    @Column(nullable = false)
    private boolean required;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParamType paramType;

    @Column
    private String value;

    @Column
    private String description;

    @Column(nullable = false)
    private Long apiId;
}