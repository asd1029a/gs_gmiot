package com.danusys.web.platform.service.ststistics;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.statistics.StatisticsSqlProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    public StatisticsServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final StatisticsSqlProvider ssp = new StatisticsSqlProvider();

    @Value("#{'${municipality.name}'.substring(0, 5)}")
    private String emdCode;

    @Override
    public EgovMap getSumChart(Map<String, Object> paramMap) throws Exception {
        return null;
    }

    @Override
    public EgovMap getAvgChart(Map<String, Object> paramMap) throws Exception {
        return null;
    }

    @Override
    public EgovMap getMapChart(Map<String, Object> paramMap) throws Exception {
        return null;
    }

    @Override
    public EgovMap getGeoJson() throws Exception {
        List<EgovMap> list = commonMapper.selectList(ssp.selectGeoJsonQry(emdCode));
        EgovMap result = new EgovMap();
        result.put("data", list);

        return result;
    }
}
