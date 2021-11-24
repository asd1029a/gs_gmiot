package com.danusys.web.platform.type;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 12:57 오후
 */
@XmlType
@XmlEnum
public enum MethodType {
    GET, POST, PUT, DELETE//, GETXML, POSTLIST, POSTMAP, POSTMAPLIST, PATHVAR
}
