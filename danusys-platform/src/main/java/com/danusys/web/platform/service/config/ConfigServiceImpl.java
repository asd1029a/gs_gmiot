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
    public Map<String, Object> getListCode(Map<String, Object> paramMap) throws Exception {
        return PagingUtil.createPagingMap(paramMap, commonMapper.selectList(csp.selectAllCodeQry(paramMap)));
    }

    public EgovMap getOneCode(int seq) throws Exception {
        return commonMapper.selectOne(csp.selectOneCodeQry(seq));
    }
}
