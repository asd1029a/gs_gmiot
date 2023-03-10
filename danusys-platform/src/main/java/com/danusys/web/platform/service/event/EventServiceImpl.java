package com.danusys.web.platform.service.event;

import com.danusys.web.commons.api.service.CommonCodeService;
import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.event.EventSqlProvider;
import lombok.RequiredArgsConstructor;
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
            EgovMap countMap = commonMapper.selectOne(esp.selectCountQry(paramMap));
            pagingMap.put("count", countMap.get("count"));
            pagingMap.put("statusCount", countMap);
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

    @Override
    public int mod(Map<String, Object> paramMap) throws Exception {
        String eventProcStat = CommonUtil.validOneNull(paramMap, "eventProcStat");
        if(eventProcStat != ""){
            paramMap.put("eventProcStat", commonMapper.selectOne(esp.selectProcStatCodeSeqQry(eventProcStat)).get("codeSeq"));
        }
        if(eventProcStat.equals("9")){
            paramMap.put("eventEndManager", LoginInfoUtil.getUserDetails().getUsername());
        }
        return commonMapper.update(esp.updateQry(paramMap));
    }
}
