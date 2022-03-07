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
@Setter
@Getter
@NoArgsConstructor
public class PagingRequest {

    private int start;
    private int length;
    private int draw;
    private List<Order> order;
    private List<Column> columns;
    private Search search;

}
