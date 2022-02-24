package com.danusys.web.platform.service.event;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.event.EventSqlProvider;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {
    public EventServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final EventSqlProvider esp = new EventSqlProvider();

    @Override
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        if(paramMap.get("draw") != null) {
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", commonMapper.selectList(esp.selectListQry(paramMap)));
            pagingMap.put("count", commonMapper.selectOne(esp.selectCountQry(paramMap)).get("count"));
            return PagingUtil.createPagingMap(paramMap, pagingMap);
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", commonMapper.selectList(esp.selectListQry(paramMap)));
            return resultMap;
        }
    }

    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(esp.selectOneQry(seq));
    }
}
