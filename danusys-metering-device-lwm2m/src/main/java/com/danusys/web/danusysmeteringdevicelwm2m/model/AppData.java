package com.danusys.web.danusysmeteringdevicelwm2m.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/04/11
 * Time : 4:20 PM
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "m2m:cin")
public class AppData {
    @JacksonXmlProperty
    private int ty;
    @JacksonXmlProperty
    private String cnf;
    @JacksonXmlProperty
    private String con;
}