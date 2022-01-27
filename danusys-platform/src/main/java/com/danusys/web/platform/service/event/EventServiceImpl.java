package com.danusys.web.platform.service.event;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.event.EventMapper;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventServiceImpl implements EventService {
    public EventServiceImpl(EventMapper eventMapper) {this.eventMapper = eventMapper;}
    private final EventMapper eventMapper;

    @Override
    public Map<String, Object> getList(Map<String, Object> paramMap) throws Exception {
        return PagingUtil.createPagingMap(paramMap, eventMapper.selectAll(paramMap));
    }

    public EgovMap getOne(int seq) throws Exception {
        return eventMapper.selectOne(seq);
    }
}
