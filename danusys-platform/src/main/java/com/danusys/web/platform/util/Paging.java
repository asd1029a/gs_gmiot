package com.danusys.web.platform.util;

import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class Paging {
    public static Page<List<Map<String, Object>>> getPage(List<Map<String, Object>> lists, PagingRequest pagingRequest) {
        List<Map<String, Object>> filtered = lists.stream()
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
}
