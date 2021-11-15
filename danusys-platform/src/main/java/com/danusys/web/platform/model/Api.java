package com.danusys.web.platform.model;

import com.danusys.web.platform.type.ApiType;
import com.danusys.web.platform.type.BodyType;
import com.danusys.web.platform.type.MethodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/02
 * Time : 3:45 오후
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Api implements Serializable {
    private static final long serialVersionUID = 8858250327261133662L;

    private Long id;
    private String callUrl;
    private MethodType methodType;
    private BodyType requestBodyType;
    private BodyType responseBodyType;
    private ApiType apiType;
    private String targetUrl;
    private String targetPath;
    private String contentType;
    private String serviceNm;
    private String servicePrefix;
    private String authInfo;

    private List<ApiParam> apiRequestParams;
    private List<ApiParam> apiResponseParams;
}
