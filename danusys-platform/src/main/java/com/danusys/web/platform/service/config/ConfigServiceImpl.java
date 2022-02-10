package com.danusys.web.platform.service.config;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.config.ConfigSqlProvider;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConfigServiceImpl implements ConfigService {

    public ConfigServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final ConfigSqlProvider csp = new ConfigSqlProvider();

    @Override
    public EgovMap getListCode(Map<String, Object> paramMap) throws Exception {

        if (paramMap.get("draw") != null) {
            return PagingUtil.createPagingMap(paramMap, commonMapper.selectList(csp.selectListCodeQry(paramMap)));
        } else {
            EgovMap resultMap = new EgovMap();
            switch (paramMap.get("type").toString()) {
                case "stationKind" : resultMap.put("data", commonMapper.selectList(csp.selectListViewStationQry(paramMap))); break;
                case "district" : resultMap.put("data", commonMapper.selectList(csp.selectListViewDistrictQry(paramMap))); break;
                case "facilityKind" : resultMap.put("data", commonMapper.selectList(csp.selectListViewKindQry(paramMap))); break;
                case "eventKind" : resultMap.put("data", commonMapper.selectList(csp.selectListViewProblemQry(paramMap))); break;
                default : resultMap.put("data", commonMapper.selectList(csp.selectListCodeQry(paramMap))); break;
            }
            return resultMap;
        }
    }

    public EgovMap getOneCode(int seq) throws Exception {
        return commonMapper.selectOne(csp.selectOneCodeQry(seq));
    }
}
