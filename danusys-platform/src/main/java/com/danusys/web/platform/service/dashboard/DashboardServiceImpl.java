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

    /*
    광명       : 승객카운트 - 스마트 폴
    영주       : 스마트버스정류장 통신장애
    부산남구    : 스마트폴 통신장애
    */
    @Override
    public EgovMap getStatusCnt1(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        List<EgovMap> resultList = new ArrayList<EgovMap>();

        paramMap.put("codeSig",codeSig);
        switch (codeSig) {
            case "26290":
            case "47210":
                resultList = commonMapper.selectList(dsp.selectStatusCnt1(paramMap));
                break;
            case "41210":
                paramMap.put("stationKind",62); //버스
                paramMap.put("subName","스마트 버스정류장 승객 카운트(1시간내)");
                resultList = commonMapper.selectList(dsp.selectKindStatusCnt(paramMap));
                break;
        }

        //resultMap.put("data", commonMapper.selectList(dsp.selectStatusCnt1(paramMap)));
        resultMap.put("data",resultList);
        return resultMap;
    }

    /*
    광명       : 승객카운트 - 스마트 폴
    영주       : 스마트버스정류장 통신장애
    부산남구    : 스마트폴 통신장애
    */
    @Override
    public EgovMap getStatusCnt2(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        List<EgovMap> resultList = new ArrayList<EgovMap>();

        paramMap.put("codeSig",codeSig);
        switch (codeSig) {
            case "41210":
                paramMap.put("stationKind",104);   //폴
                paramMap.put("subName","스마트 폴 유동인구 카운트(1시간내)");
                resultList = commonMapper.selectList(dsp.selectKindStatusCnt(paramMap));
                break;
            case "47210":
                paramMap.put("stationKind",62);
                resultList = commonMapper.selectList(dsp.selectTroubleFacility(paramMap));
                break;
            case "26290":
                paramMap.put("stationKind",104);
                resultList = commonMapper.selectList(dsp.selectTroubleFacility(paramMap));
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

        paramMap.put("codeSig",codeSig);
        switch (codeSig) {
            case "41210":
                paramMap.put("stationKind",104);
                resultList = commonMapper.selectList(dsp.selectTroubleFacility(paramMap));
                break;
            case "47210":
                paramMap.put("eventKind",65);   //유동인구-쓰러짐감지
                paramMap.put("name","유동인구 이벤트");
                paramMap.put("subName","쓰러짐 감지(1시간내/누적)");
                resultList = commonMapper.selectList(dsp.selectEventCount(paramMap));
                break;
            case "26290":
                paramMap.put("eventKind",55);   //지능형-용의자검출
                paramMap.put("name","지능형 카메라 이벤트");
                paramMap.put("subName","용의자 검출(1시간내/누적)");
                resultList = commonMapper.selectList(dsp.selectEventCount(paramMap));
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

        paramMap.put("codeSig",codeSig);
        switch (codeSig) {
            case "41210":
                paramMap.put("eventKind",187);   //스마트폴-전원이상
                paramMap.put("name","스마트폴 이벤트");
                paramMap.put("subName","전원이상(1시간내/누적)");
                resultList = commonMapper.selectList(dsp.selectEventCount(paramMap));
                break;
            case "47210":
                paramMap.put("eventKind",66);   //유동인구-화재감지
                paramMap.put("name","유동인구 이벤트");
                paramMap.put("subName","화재 감지(1시간내/누적)");
                resultList = commonMapper.selectList(dsp.selectEventCount(paramMap));
                break;
            case "26290":
                paramMap.put("eventKind",188);   //지능형-실종자검출
                paramMap.put("name","지능형 카메라 이벤트");
                paramMap.put("subName","실종자 검출(1시간내/누적)");
                resultList = commonMapper.selectList(dsp.selectEventCount(paramMap));
                break;
        }
        resultMap.put("data",resultList);
        //resultMap.put("data", commonMapper.selectList(dsp.selectStatusCnt4(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getFloatingPopulation(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        ArrayList<Map<String,Object>> resultTempArray = new ArrayList<>();
        List<EgovMap> stationList = new ArrayList<EgovMap>();

        paramMap.put("codeSig",codeSig);

        stationList = commonMapper.selectList(dsp.selectStationByPeopleCntList(paramMap));  //지자체별 개소목록
        for(EgovMap tempMap : stationList) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", tempMap.get("stationName"));   //개소이름

            List<EgovMap> peopleCntByStationList = new ArrayList<EgovMap>();

            ArrayList<Map<String,Object>> dataTempArray = new ArrayList<>();

            paramMap.put("stationSeq", tempMap.get("stationSeq"));
            peopleCntByStationList = commonMapper.selectList(dsp.selectPeopleCntByStationList(paramMap));  //동별 개소목록

            List<Object> tempTimeCntArray = new ArrayList<>();
            for(EgovMap tempMap2 : peopleCntByStationList) {
                tempTimeCntArray.add(tempMap2.get("timeCnt"));
            }
            map.put("data",tempTimeCntArray);    //동별 개수 카운트
            resultTempArray.add(map);
        }
        resultMap.put("data", resultTempArray);
        return resultMap;
    }


    @Override
    public EgovMap getStation(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        ArrayList<Map<String,Object>> resultTempArray = new ArrayList<>();
        List<EgovMap> stationKindList = new ArrayList<EgovMap>();

        paramMap.put("codeSig",codeSig);

        stationKindList = commonMapper.selectList(dsp.selectStationKindList(paramMap));  //지자체별 개소종류목록
        for(EgovMap tempMap : stationKindList) {
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

        paramMap.put("codeSig",codeSig);

        List<EgovMap> airDataList = commonMapper.selectList(dsp.selectAirPollution(paramMap));
        ArrayList<Map<String,Object>> resultTempArray = new ArrayList<>();

        paramMap.put("codeSig",codeSig);

        for(EgovMap tempMap : airDataList) {
            Map<String, Object> tempResultMap = new HashMap<>();
            for (Object o : tempMap.keySet()) {

                String strKey = o.toString();
                String objValue = "";
                if(tempMap.get(strKey) == null)  objValue = "-";
                else objValue = tempMap.get(strKey).toString();

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

    @Override
    public EgovMap getDroneCabinetStatus(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(dsp.getDroneCabinetStatus(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getCabinetRank(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(dsp.selectCabinetRank(paramMap)));
        return resultMap;
    }
}
