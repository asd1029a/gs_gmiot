package com.danusys.web.platform.service.facility;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.platform.mapper.facility.FacilitySqlProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacilityServiceImpl implements FacilityService{

    public FacilityServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final FacilitySqlProvider fsp = new FacilitySqlProvider();

    @Override
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        if(paramMap.get("draw") != null) {
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", commonMapper.selectList(fsp.selectListQry(paramMap)));
            pagingMap.put("count", commonMapper.selectOne(fsp.selectCountQry(paramMap)).get("count"));
            return PagingUtil.createPagingMap(paramMap, pagingMap);
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", commonMapper.selectList(fsp.selectListQry(paramMap)));
            return resultMap;
        }
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(fsp.selectOneQry(seq));
    }

    @Override
    public int add(Map<String, Object> paramMap) throws Exception {
        return commonMapper.insert(fsp.insertQry(paramMap));
    }

    @Override
    public int addOpt(Map<String, Object> paramMap) throws Exception {
        if("dimming".equals(paramMap.get("facilityOptType"))) {
            int dimmingGroupSeq;
            List<Object> facilitySeqList = (List<Object>) paramMap.get("facilitySeqList");
            List<Map<String, Object>> facilityOptList = new ArrayList<Map<String, Object>>();

            if(getDimmingGroupSeq().get("dimmingGroupSeq") != null) {
                dimmingGroupSeq = Integer.parseInt(getDimmingGroupSeq().get("dimmingGroupSeq").toString()) + 1;
            } else {
                dimmingGroupSeq = 1;
            }

            for (Object facilitySeq : facilitySeqList) {
                Map<String, Object> dimmingGroupSeqMap = new HashMap<String, Object>();
                dimmingGroupSeqMap.put("facility_seq", facilitySeq.toString());
                dimmingGroupSeqMap.put("facility_opt_name", "dimming_group_seq");
                dimmingGroupSeqMap.put("facility_opt_value", String.valueOf(dimmingGroupSeq));
                dimmingGroupSeqMap.put("facility_opt_type", "1");

                Map<String, Object> dimmingGroupNameMap = new HashMap<String, Object>();
                dimmingGroupNameMap.put("facility_seq", facilitySeq.toString());
                dimmingGroupNameMap.put("facility_opt_name", "dimming_group_name");
                dimmingGroupNameMap.put("facility_opt_value", paramMap.get("dimmingGroupName"));
                dimmingGroupNameMap.put("facility_opt_type", "1");

                facilityOptList.add(dimmingGroupSeqMap);
                facilityOptList.add(dimmingGroupNameMap);
            }
            paramMap.put("facilityOptList", facilityOptList);
        }
        return commonMapper.insert(fsp.insertOptQry(paramMap));
    }

    @Override
    public int mod(Map<String, Object> paramMap) throws Exception {
        return commonMapper.update(fsp.updateQry(paramMap));
    }

    @Override
    @Transactional
    public int modOpt(Map<String, Object> paramMap) throws Exception {
        commonMapper.delete(fsp.deleteOptQry(paramMap));

        if("dimming".equals(paramMap.get("facilityOptType"))) {
            List<Object> facilitySeqList = (List<Object>) paramMap.get("facilitySeqList");
            List<Map<String, Object>> facilityOptList = new ArrayList<Map<String, Object>>();

            for (Object facilitySeq : facilitySeqList) {
                Map<String, Object> dimmingGroupSeqMap = new HashMap<String, Object>();
                dimmingGroupSeqMap.put("facility_seq", facilitySeq.toString());
                dimmingGroupSeqMap.put("facility_opt_name", "dimming_group_seq");
                dimmingGroupSeqMap.put("facility_opt_value", String.valueOf(paramMap.get("dimmingGroupSeq")));
                dimmingGroupSeqMap.put("facility_opt_type", "1");

                Map<String, Object> dimmingGroupNameMap = new HashMap<String, Object>();
                dimmingGroupNameMap.put("facility_seq", facilitySeq.toString());
                dimmingGroupNameMap.put("facility_opt_name", "dimming_group_name");
                dimmingGroupNameMap.put("facility_opt_value", paramMap.get("dimmingGroupName"));
                dimmingGroupNameMap.put("facility_opt_type", "1");

                facilityOptList.add(dimmingGroupSeqMap);
                facilityOptList.add(dimmingGroupNameMap);
            }
            paramMap.put("facilityOptList", facilityOptList);
        }
        return commonMapper.update(fsp.insertOptQry(paramMap));
    }

    @Override
    public void del(int seq) throws Exception {
        commonMapper.delete(fsp.deleteQry(seq));
    }

    @Override
    public void delOpt(Map<String, Object> paramMap) throws Exception {
        commonMapper.delete(fsp.deleteOptQry(paramMap));
    }

    @Override
    public EgovMap getListDimmingGroup(Map<String, Object> paramMap) throws Exception {
        if(paramMap.get("draw") != null) {
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", commonMapper.selectList(fsp.selectListDimmingGroupQry(paramMap)));
            pagingMap.put("count", commonMapper.selectOne(fsp.selectCountDimmingGroupQry(paramMap)).get("count"));
            return PagingUtil.createPagingMap(paramMap, pagingMap);
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", commonMapper.selectList(fsp.selectListDimmingGroupQry(paramMap)));
            return resultMap;
        }
    }

    @Override
    public EgovMap getDimmingGroupSeq() throws Exception {
        return commonMapper.selectOne(fsp.selectOneDimmingGroupSeqQry());
    }

    @Override
    public EgovMap getListLampRoadInDimmingGroup(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(fsp.selectListLampRoadInDimmingGroupQry(paramMap)));
        return resultMap;
    }

}
