package com.danusys.web.commons.api.model;

import com.danusys.web.commons.api.types.ApiType;
import com.danusys.web.commons.api.types.BodyType;
import com.danusys.web.commons.api.types.MethodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;



/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/02
 * Time : 3:45 오후
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api")
public class Api implements Serializable {
    private static final long serialVersionUID = 8858250327261133662L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String callUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MethodType methodType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyType requestBodyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyType responseBodyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiType apiType;

    @Column(nullable = false)
    private String targetUrl;

    @Column(nullable = false)
    private String targetPath;

    private String contentType;
    private String serviceNm;
    private String servicePrefix;
    private String authInfo;

    @Transient
    private List<ApiParam> apiRequestParams;

    @Transient
    private List<ApiParam> apiResponseParams;
}
