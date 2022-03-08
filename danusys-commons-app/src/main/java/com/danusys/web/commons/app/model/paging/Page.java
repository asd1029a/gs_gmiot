package com.danusys.web.commons.app.model.paging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * Project   : danusys-webservice-parent
 * Developer : kai
 * Date : 2022/01/26
 * Time : 2:23 PM
 */
@Getter
@Setter
@NoArgsConstructor
public class Page<T> {

    public Page(List<T> data) {
        this.data = data;
    }

    private List<T> data;
    private int recordsFiltered;
    private int recordsTotal;
    private int draw;

}
