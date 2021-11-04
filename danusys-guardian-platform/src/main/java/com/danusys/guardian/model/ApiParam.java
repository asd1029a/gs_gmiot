package com.danusys.guardian.model;

import com.danusys.guardian.type.DataType;
import com.danusys.guardian.type.ParamType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Project : danusys-guardian-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 1:16 오후
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiParam implements Serializable {

    private static final long serialVersionUID = -7877521422884001681L;

    private Long id;
    private int seq;
    private String fieldNm;
    private String fieldMapNm;
    private DataType dataType;
    private boolean required;
    private ParamType paramType;
    private String value;
    private String description;
}