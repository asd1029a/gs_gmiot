package com.danusys.web.commons.app.model.paging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by IntelliJ IDEA
 * Project   : danusys-webservice-parent
 * Developer : kai
 * Date : 2022/01/26
 * Time : 2:23 PM
 */
@Setter
@Getter
@NoArgsConstructor
public class Search {

    private String value;
    private String regexp;
}
