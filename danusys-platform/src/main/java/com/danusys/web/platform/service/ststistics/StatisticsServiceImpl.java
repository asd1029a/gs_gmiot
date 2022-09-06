package com.danusys.web.platform.service.ststistics;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.event.EventSqlProvider;
import com.danusys.web.platform.mapper.statistics.StatisticsSqlProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    public StatisticsServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final StatisticsSqlProvider ssp = new StatisticsSqlProvider();

    private final EventSqlProvider esp = new EventSqlProvider();

    @Value("#{'${municipality.name}'.substring(0, 5)}")
    private String emdCode;

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

    @Override
    public List<EgovMap> getSumChart(Map<String, Object> paramMap) throws Exception {
        return commonMapper.selectList(ssp.selectSumQry(paramMap));
    }

    @Override
    public List<EgovMap> getAvgChart(Map<String, Object> paramMap) throws Exception {
        return commonMapper.selectList(ssp.selectAvgQry(paramMap));
    }

    @Override
    public List<EgovMap> getMapChart(Map<String, Object> paramMap) throws Exception {
        return commonMapper.selectList(ssp.selectMapQry(paramMap));
    }

    @Override
    public EgovMap getGeoJson() throws Exception {
        List<EgovMap> list = commonMapper.selectList(ssp.selectGeoJsonQry(emdCode));
        EgovMap result = new EgovMap();
        result.put("data", list);

        return result;
    }

    @Override
    public EgovMap getListOpt(Map<String, Object> paramMap) throws Exception {
        if(paramMap.get("draw") != null) {
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", commonMapper.selectList(ssp.selectListOptQry(paramMap)));
            EgovMap countMap = commonMapper.selectOne(ssp.selectCountOptQry(paramMap));
            pagingMap.put("count", countMap.get("count"));
            pagingMap.put("statusCount", countMap);
            return PagingUtil.createPagingMap(paramMap, pagingMap);
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", commonMapper.selectList(ssp.selectListOptQry(paramMap)));
            return resultMap;
        }
    }

    @Override
    public List<EgovMap> getSumChartOpt(Map<String, Object> paramMap) throws Exception {
        return commonMapper.selectList(ssp.selectSumOptQry(paramMap));
    }

    @Override
    public List<EgovMap> getAvgChartOpt(Map<String, Object> paramMap) throws Exception {
        return commonMapper.selectList(ssp.selectAvgOptQry(paramMap));
    }

    @Override
    public List<EgovMap> getMapChartOpt(Map<String, Object> paramMap) throws Exception {
        return commonMapper.selectList(ssp.selectMapOptQry(paramMap));
    }

    @Override
    public EgovMap getGeoJsonOpt() throws Exception {
        List<EgovMap> list = commonMapper.selectList(ssp.selectGeoJsonQry(emdCode));
        EgovMap result = new EgovMap();
        result.put("data", list);

        return result;
    }
}
