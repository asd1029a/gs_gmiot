package com.danusys.web.platform.service.dashboard;

import com.danusys.web.commons.app.AirPollutionUtil;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.dashboard.DashboardSqlProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {
    public DashboardServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}
    private final CommonMapper commonMapper;

    private final DashboardSqlProvider dsp = new DashboardSqlProvider();

    @Value("${danusys.area.code.sig}")
    private String codeSig;
    //광명: 41210, 김제: 45210, 영주: 47210, 부산남구: 26290

    @Override
    public EgovMap getDroneData(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data","");
        return resultMap;
    }

    @Override
    public EgovMap getStatusCnt1(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(dsp.selectStatusCnt1(paramMap)));
        return resultMap;
    }

    /*
    광명       : 스마트버스정류장 통신장애
    영주       : 스마트버스정류장 통신장애
    부산남구    : 스마트폴 통신장애
    */
    @Override
    public EgovMap getStatusCnt2(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        List<EgovMap> resultList = new ArrayList<EgovMap>();
        switch (codeSig) {
            case "41210":
            case "47210":
                resultList = commonMapper.selectList(dsp.selectTroubleBus(paramMap));
                break;
            case "26290":
                resultList = commonMapper.selectList(dsp.selectTroublePole(paramMap));
                break;
        }
        resultMap.put("data",resultList);
        return resultMap;
    }

    /*
    광명       : 스마트폴 통신장애
    영주       : 유동인구-쓰러짐 카운트
    부산남구    : 용의자검출 카운트
    */
    @Override
    public EgovMap getStatusCnt3(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        List<EgovMap> resultList = new ArrayList<EgovMap>();
        switch (codeSig) {
            case "41210":
                resultList = commonMapper.selectList(dsp.selectTroublePole(paramMap));
                break;
            case "47210":
                resultList = commonMapper.selectList(dsp.selectEventDropAttack(paramMap));
                break;
            case "26290":
                resultList = commonMapper.selectList(dsp.selectEventSuspectDetection(paramMap));
                break;
        }
        resultMap.put("data",resultList);
        //resultMap.put("data", commonMapper.selectList(dsp.selectStatusCnt3(paramMap)));
        return resultMap;
    }

    /*
    광명       : 스마트폴 이벤트(전원이벤트)
    영주       : 유동인구-화재 카운트
    부산남구    : 실종자검출 카운트
    */
    @Override
    public EgovMap getStatusCnt4(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        List<EgovMap> resultList = new ArrayList<EgovMap>();
        switch (codeSig) {
            case "41210":
                resultList = commonMapper.selectList(dsp.selectEventPole(paramMap));
                break;
            case "47210":
                resultList = commonMapper.selectList(dsp.selectEventFire(paramMap));
                break;
            case "26290":
                resultList = commonMapper.selectList(dsp.selectEventMissingPerson(paramMap));
                break;
        }
        resultMap.put("data",resultList);
        //resultMap.put("data", commonMapper.selectList(dsp.selectStatusCnt4(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getStation(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();

        List<EgovMap> stationList = new ArrayList<EgovMap>();
        ArrayList<Map<String,Object>> resultTempArray = new ArrayList<>();

        paramMap.put("codeSig",codeSig);
        stationList = commonMapper.selectList(dsp.selectStationList(paramMap));  //지자체별 개소목록
        for(EgovMap tempMap : stationList) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", tempMap.get("codeName"));   //개소이름

            List<EgovMap> stationCntByDongMap = new ArrayList<EgovMap>();
            ArrayList<Map<String,Object>> dataTempArray = new ArrayList<>();

            paramMap.put("codeSeq", tempMap.get("codeSeq"));
            stationCntByDongMap = commonMapper.selectList(dsp.selectStationCntByDong(paramMap));  //동별 개소목록
            for(EgovMap tempMap2 : stationCntByDongMap) {
                Map<String, Object> cntTempMap =new HashMap<>();
                cntTempMap.put("x",tempMap2.get("stationName"));
                cntTempMap.put("y",tempMap2.get("stationCnt"));
                dataTempArray.add(cntTempMap);
            }
            map.put("data",dataTempArray);    //동별 개수 카운트
            resultTempArray.add(map);
        }
        resultMap.put("data", resultTempArray);
        return resultMap;
    }

    @Override
    public EgovMap getAirPollution(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        List<EgovMap> airDataList = commonMapper.selectList(dsp.selectAirPollution(paramMap));
        ArrayList<Map<String,Object>> resultTempArray = new ArrayList<>();

        for(EgovMap tempMap : airDataList) {
            Map<String, Object> tempResultMap = new HashMap<>();
            for (Object o : tempMap.keySet()) {

                String strKey = o.toString();
                String objValue = tempMap.get(strKey).toString();

                Map<String, Object> result = null;
                Double result500 = null;
                try {
                    if ("pm10Value24".equals(strKey) || "pm25Value24".equals(strKey)) {
                        result500 = AirPollutionUtil.convertAirPollutionDataToScore500(strKey, objValue);
                        result = AirPollutionUtil.convertAirPollutionDataToScoreMap(result500);
                        tempResultMap.put(strKey, result);
                    } else {
                        tempResultMap.put(strKey, objValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            resultTempArray.add(tempResultMap);
        }
        resultMap.put("data", resultTempArray);
        return resultMap;
    }
}
