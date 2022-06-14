package com.danusys.web.platform.service.config;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.platform.mapper.config.ConfigSqlProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final CommonMapper commonMapper;
    private final ConfigSqlProvider csp = new ConfigSqlProvider();

    @Override
    public EgovMap getListCode(Map<String, Object> paramMap) throws Exception {

        if (paramMap.get("draw") != null) {
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", commonMapper.selectList(csp.selectListCodeQry(paramMap)));
            pagingMap.put("count", commonMapper.selectOne(csp.selectCountCodeQry(paramMap)).get("count"));
            return PagingUtil.createPagingMap(paramMap, pagingMap);
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", commonMapper.selectList(csp.selectListTypeQry(paramMap)));
            return resultMap;
        }
    }

    public EgovMap getOneCode(int seq) throws Exception {
        return commonMapper.selectOne(csp.selectOneCodeQry(seq));
    }

    @Override
    public EgovMap getOneEventKind(String pKind) {
        return commonMapper.selectOne(csp.selectOneEventKindQry(pKind));
    }

    @Override
    public List<EgovMap> getListMntrInitParam(String pageTypeCodeValue) throws Exception {
        return commonMapper.selectList(csp.selectListInitMntrParam(pageTypeCodeValue));
    }
}
