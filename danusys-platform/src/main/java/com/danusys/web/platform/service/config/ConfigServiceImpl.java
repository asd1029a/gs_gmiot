package com.danusys.web.platform.service.config;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.config.ConfigMapper;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConfigServiceImpl implements ConfigService {

    public ConfigServiceImpl(ConfigMapper commonMapper) {this.commonMapper = commonMapper;}
    private final ConfigMapper commonMapper;

    @Override
    public Map<String, Object> getListCode(Map<String, Object> paramMap) throws Exception {
        System.out.println(commonMapper.selectAllCode(paramMap));
        return PagingUtil.createPagingMap(paramMap, commonMapper.selectAllCode(paramMap));
    }

    public EgovMap getOneCode(int seq) throws Exception {
        return commonMapper.selectOneCode(seq);
    }
}
