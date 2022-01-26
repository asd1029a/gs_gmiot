package com.danusys.web.platform.model.paging;

import lombok.*;

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
@AllArgsConstructor
@EqualsAndHashCode
public class Order {

    private Integer column;
    private Direction dir;

}
