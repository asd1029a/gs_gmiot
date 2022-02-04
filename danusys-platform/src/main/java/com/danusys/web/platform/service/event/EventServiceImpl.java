package com.danusys.web.platform.service.event;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.event.EventSqlProvider;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventServiceImpl implements EventService {
    public EventServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final EventSqlProvider esp = new EventSqlProvider();

    @Override
    public Map<String, Object> getList(Map<String, Object> paramMap) throws Exception {
        return PagingUtil.createPagingMap(paramMap, commonMapper.selectList(esp.selectAllEventQry(paramMap)));
    }

    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(esp.selectOneEventQry(seq));
    }
}
