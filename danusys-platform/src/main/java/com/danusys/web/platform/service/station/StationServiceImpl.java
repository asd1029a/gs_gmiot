package com.danusys.web.platform.service.station;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.config.ConfigSqlProvider;
import com.danusys.web.platform.mapper.facility.FacilitySqlProvider;
import com.danusys.web.platform.mapper.station.StationSqlProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StationServiceImpl implements StationService{

    public StationServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final StationSqlProvider ssp = new StationSqlProvider();
    private final FacilitySqlProvider fsp = new FacilitySqlProvider();
    private final ConfigSqlProvider csp = new ConfigSqlProvider();

    @Override
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(ssp.selectListQry(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getListPaging(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> pagingMap = new HashMap<>();
        EgovMap count = commonMapper.selectOne(ssp.selectCountQry(paramMap));
        pagingMap.put("data", commonMapper.selectList(ssp.selectListQry(paramMap)));
        pagingMap.put("count", count.get("count"));
        pagingMap.put("statusCount", count);
        return PagingUtil.createPagingMap(paramMap, pagingMap);
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(ssp.selectOneQry(seq));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> stationInfo = (Map<String, Object>) paramMap.get("stationInfo");
        Map<String, Object> typeMap = new HashMap<>();
        String stationKindSeq = "";

        typeMap.put("type", "stationKind");

        List<EgovMap> stationKindData = (List<EgovMap>) commonMapper.selectList(csp.selectListTypeQry(typeMap));

        // stationKind 영문명 -> stationKind Seq로 변경
        for (EgovMap stationKind : stationKindData) {
            if(stationKind.get("codeValue").equals(stationInfo.get("stationKind"))) {
                stationKindSeq = stationKind.get("codeSeq").toString();
            }
        }

        stationInfo.put("stationKind", stationKindSeq);
        commonMapper.insert(ssp.insertQry(stationInfo));

        paramMap.put("stationSeq", commonMapper.selectOne(ssp.selectOneLastStationSeqQry()).get("lastStationSeq"));
        return commonMapper.update(fsp.updateStationSeqQry(paramMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int mod(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> stationInfo = (Map<String, Object>) paramMap.get("stationInfo");
        Map<String, Object> typeMap = new HashMap<>();
        String stationKindSeq = "";

        typeMap.put("type", "stationKind");

        List<EgovMap> stationKindData = (List<EgovMap>) commonMapper.selectList(csp.selectListTypeQry(typeMap));

        // stationKind 영문명 -> stationKind Seq로 변경
        for (EgovMap stationKind : stationKindData) {
            if(stationKind.get("codeValue").equals(stationInfo.get("stationKind"))) {
                stationKindSeq = stationKind.get("codeSeq").toString();
            }
        }

        stationInfo.put("stationKind", stationKindSeq);
        stationInfo.put("stationSeq", paramMap.get("stationSeq").toString());
        commonMapper.update(ssp.updateQry(stationInfo));

        commonMapper.update(fsp.updateNullStationSeqQry(paramMap));

        return commonMapper.update(fsp.updateStationSeqQry(paramMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(int seq) throws Exception {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("stationSeq", seq);
        commonMapper.update(fsp.updateNullStationSeqQry(paramMap));
        commonMapper.delete(ssp.deleteQry(seq));
    }

    @Override
    public EgovMap getListStationForSignage(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(ssp.selectListStationForSignageQry(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getOneStationForSignage(int seq) throws Exception {
        return commonMapper.selectOne(ssp.selectOneStationForSignageQry(seq));
    }
}
