package com.danusys.web.commons.api.dto;

import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/05/26
 * Time : 4:24 PM
 */
@Getter
@ToString
@XmlRootElement(name = "logicalfolder")
public class LogicalfolderDTO {

    @XmlAttribute(name = "fdid")
    private String fdid;

    @XmlAttribute(name = "fdpth")
    private String fdpth;

    @XmlElement(name = "logicalpoints")
    private Logicalpoints logicalpoints;

    @Getter
    @ToString
    @XmlRootElement(name = "logicalpoints")
    public static class Logicalpoints {

        @XmlElement(name = "lpt")
        private List<Lpt> lpts;

        @Getter
        @ToString
        @XmlRootElement(name = "lpt")
        public static class Lpt {
            @XmlAttribute(name = "id")
            private String id;

            @XmlAttribute(name = "nm")
            private String nm;

            @XmlAttribute(name = "kind")
            private String kind;

            @XmlAttribute(name = "ty")
            private String ty;

            @XmlAttribute(name = "pth")
            private String pth;

            @XmlAttribute(name = "ref")
            private String ref;

            @XmlAttribute(name = "pms")
            private String pms;

            @XmlAttribute(name = "armen")
            private String armen;

            @XmlAttribute(name = "histen")
            private String histen;

            @XmlAttribute(name = "schen")
            private String schen;

            @XmlAttribute(name = "lgen")
            private String lgen;

            @XmlAttribute(name = "acten")
            private String acten;

            @XmlAttribute(name = "histty")
            private String histty;

            @XmlAttribute(name = "histintv")
            private String histintv;

        }
    }

}
