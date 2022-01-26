package com.danusys.web.platform.util;

import com.danusys.web.commons.api.types.DataType;
import com.danusys.web.platform.model.paging.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Created by IntelliJ IDEA
 * Project   : danusys-webservice-parent
 * Developer : kai
 * Date : 2022/01/26
 * Time : 2:23 PM
 */
@Slf4j
public class Paging {
    private static final Comparator<Map> EMPTY_COMPARATOR = (o1, o2) -> 0;

    public static Page<List<Map<String, Object>>> getPage(List<Map<String, Object>> lists, PagingRequest pagingRequest) {
        List<Map<String, Object>> filtered = lists.stream()
                .sorted(sorts(pagingRequest))
                .filter(filterLists(pagingRequest))
                .skip(pagingRequest.getStart())
                .limit(pagingRequest.getLength())
                .collect(Collectors.toList());

        long count = lists.stream().filter(filterLists(pagingRequest)).count();

        Page<List<Map<String, Object>>> page = new Page(filtered);
        page.setRecordsFiltered((int) count);
        page.setRecordsTotal((int) count);
        page.setDraw(pagingRequest.getDraw());

        return page;
    }

    public static Predicate<Map> filterLists(PagingRequest pagingRequest) {
        if (pagingRequest.getSearch() == null || StringUtils.isEmpty(pagingRequest.getSearch().getValue())) {
            return map -> true;
        }
        String value = pagingRequest.getSearch().getValue();

        return map -> map.keySet().stream()
                .map(m -> map.get(m).toString().toLowerCase().contains(value))
                .map(String::valueOf)
                .collect(Collectors.joining("|")).toString().contains("true");
    }


    public static Comparator<Map> sorts(PagingRequest pagingRequest) {
        if (pagingRequest.getOrder() == null) {
            return EMPTY_COMPARATOR;
        }

        if (pagingRequest.getOrder().stream().count() == 0) {
            return EMPTY_COMPARATOR;
        }

        try {
            Order order = pagingRequest.getOrder().get(0);
            int columnIndex = order.getColumn();
            Column column = pagingRequest.getColumns().get(columnIndex);

//            log.info("order : {}", order.getDir());
//            log.info("order : {}", order.getColumn());
//            log.info("order : {}", column.getData());
//            log.info("order : {}", column.getType());

            Comparator<Map> comparator = null;
            if(order.getDir() == Direction.asc) {
                if(DataType.DOUBLE == column.getType()) {
                    comparator = (o1, o2) ->  Double.compare(Double.parseDouble(o1.get(column.getData()).toString()), Double.parseDouble(o2.get(column.getData()).toString())); //오름차순
                } else {
                    comparator = (o1, o2) -> o1.get(column.getData()).toString().toLowerCase().compareTo(o2.get(column.getData()).toString().toLowerCase()); //오름차순
                }
            } else if(order.getDir() == Direction.desc) {
                if(DataType.DOUBLE == column.getType()) {
                    comparator = (o1, o2) ->  Double.compare(Double.parseDouble(o2.get(column.getData()).toString()), Double.parseDouble(o1.get(column.getData()).toString())); //내림차순
                } else {
                    comparator = (o1, o2) -> o2.get(column.getData()).toString().toLowerCase().compareTo(o1.get(column.getData()).toString().toLowerCase()); //내일차순
                }
            } else {
                comparator = EMPTY_COMPARATOR;
            }

            return comparator;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return EMPTY_COMPARATOR;
    }
}
