package com.danusys.web.commons.app.model.paging;

import com.danusys.web.commons.app.types.DataType;
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
public class Column {

    private String data;
    private String name;
    private Boolean searchable;
    private Boolean orderable;
    private Search search;
    private DataType type;

    public Column(String data) {
        this.data = data;
    }
}
