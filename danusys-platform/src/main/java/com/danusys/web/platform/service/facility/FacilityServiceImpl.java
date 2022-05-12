package com.danusys.web.platform.service.facility;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.platform.dto.request.SignageRequestDto;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.facility.FacilitySqlProvider;
import org.json.simple.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
        EgovMap resultMap = new EgovMap();
        switch (paramMap.get("popupType").toString()) {
            case "station" :
                resultMap.put("data", commonMapper.selectList(fsp.selectListFacilityForStationQry(paramMap)));
                break;
            case "dimming" :
                resultMap.put("data", commonMapper.selectList(fsp.selectListFacilityForDimmingQry(paramMap)));
                break;
            default : resultMap.put("data", commonMapper.selectList(fsp.selectListQry(paramMap)));
                break;
        }

        return resultMap;
    }

    @Override
    public EgovMap getListPaging(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> pagingMap = new HashMap<>();
        EgovMap count = commonMapper.selectOne(fsp.selectCountQry(paramMap));
        pagingMap.put("data", commonMapper.selectList(fsp.selectListQry(paramMap)));
        pagingMap.put("count", count.get("count"));
        pagingMap.put("statusCount", count);
        return PagingUtil.createPagingMap(paramMap, pagingMap);
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
        return commonMapper.insert(fsp.insertOptQry(paramMap));
    }

    @Override
    public int mod(Map<String, Object> paramMap) throws Exception {
        return commonMapper.update(fsp.updateQry(paramMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int modOpt(Map<String, Object> paramMap) throws Exception {
        commonMapper.delete(fsp.deleteOptQry(paramMap));
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
    public EgovMap getListFacilityInStation(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(fsp.selectListFacilityInStationQry(paramMap)));
        return resultMap;
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
    public EgovMap getLastDimmingGroupSeq() throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectOne(fsp.selectOneLastDimmingGroupSeqQry()));
        return resultMap;
    }

    @Override
    public EgovMap getListLampRoadInDimmingGroup(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(fsp.selectListLampRoadInDimmingGroupQry(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getListSignageTemplate(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(fsp.selectListSignageTemplateQry(paramMap)));
        return resultMap;
    }

    @Override
    public int addSignageTemplate(Map<String, Object> paramMap) throws Exception {
        return commonMapper.insert(fsp.insertSignageTemplateQry(paramMap));
    }

    @Override
    public int modSignageTemplate(Map<String, Object> paramMap) throws Exception {
        return commonMapper.update(fsp.updateSignageTemplateQry(paramMap));
    }

    @Override
    public int modSignageLayout(MultipartFile[] imageFile, MultipartFile[] videoFile,
            HttpServletRequest request, SignageRequestDto signageRequestDto) throws Exception {
        String imageFileName = "";
        String videoFileName = "";
        if (imageFile.length > 0) {
            imageFileName = FileUtil.uploadAjaxPost(imageFile, request);
        } else if (videoFile.length > 0) {
            videoFileName = FileUtil.uploadAjaxPost(videoFile, request);
        }
        String templateContentStr = signageRequestDto.getTemplateContent().replaceAll("&quot;", "\"");

        List<Map<String, Object>> templateContentList = JsonUtil.jsonToListMap(templateContentStr);

        JSONArray newTemplateContentList = new JSONArray();
        for (Map<String, Object> map : templateContentList) {
            if("imageFile".equals(map.get("kind"))) {
                map.put("value", imageFileName);
            } else if("videoFile".equals(map.get("kind"))) {
                map.put("value", videoFileName);
            }
            newTemplateContentList.add(JsonUtil.MapToJson(map).toString());
        }

        signageRequestDto.setTemplateContent(newTemplateContentList.toJSONString());
        return commonMapper.update(fsp.updateSignageLayoutQry(signageRequestDto));
    }

    @Override
    public void delSignageTemplate(int seq) throws Exception {
        commonMapper.delete(fsp.deleteSignageTemplateQry(seq));
    }
}
