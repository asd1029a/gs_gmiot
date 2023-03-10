package com.danusys.web.platform.service.facility;

import com.danusys.web.commons.app.*;
import com.danusys.web.platform.dto.request.SignageRequestDto;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.facility.FacilitySqlProvider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
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

        String popupType = CommonUtil.validOneNull(paramMap, "popupType").toString();
        switch (popupType) {
            case "station" :
                resultMap.put("data", commonMapper.selectList(fsp.selectListFacilityForStationQry(paramMap)));
                break;
            case "dimming" :
                resultMap.put("data", commonMapper.selectList(fsp.selectListFacilityForDimmingQry(paramMap)));
                break;
            case "signage" :
                resultMap.put("data", commonMapper.selectList(fsp.selectListFacilityForSignageQry(paramMap)));
                break;
            default : resultMap.put("data", commonMapper.selectList(fsp.selectListQry(paramMap)));
                break;
        }

        return resultMap;
    }

    @Override
    public EgovMap getListCctvHead(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data",commonMapper.selectList(fsp.selectListCctvHead(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getListCctv(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(fsp.selectListCctv(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getListPaging(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> pagingMap = new HashMap<>();
        pagingMap.put("data", commonMapper.selectList(fsp.selectListQry(paramMap)));
        EgovMap count = commonMapper.selectOne(fsp.selectCountQry(paramMap));
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
    public EgovMap getSignageLayout(int id) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectOne(fsp.selectOneSignageLayoutQry(id)));
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
    public void modSignageLayout(MultipartFile[] imageFile, MultipartFile[] videoFile,
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
                if("".equals(imageFileName)) {
                    imageFileName = map.get("value").toString();
                }
                map.put("value", imageFileName);
            } else if("videoFile".equals(map.get("kind"))) {
                if("".equals(videoFileName)) {
                    videoFileName = map.get("value").toString();
                }
                map.put("value", videoFileName);
            }
            newTemplateContentList.add(JsonUtil.convertMapToJson(map));
        }

        signageRequestDto.setTemplateContent(newTemplateContentList.toString());
        commonMapper.update(fsp.updateSignageLayoutQry(signageRequestDto));
    }

    @Override
    public String getOneSignageData(String serverName, String serverPort) throws Exception {
        EgovMap dataMap = commonMapper.selectOne(fsp.selectOneSignageLayoutUseQry());

        String templateContentStr = dataMap.get("templateContent").toString().replaceAll("&quot;", "\"");
        List<Map<String, Object>> templateContentList = JsonUtil.jsonToListMap(templateContentStr);

        JSONArray newTemplateContentList = new JSONArray();
        for (Map<String, Object> map : templateContentList) {
            JSONObject newTemplateContent = new JSONObject();
                for (String key : map.keySet()) {
                    JSONArray newFileList = new JSONArray();
                    if(key.indexOf("List") > 0) {
                        List<Map<String, Object>> fileList = (List<Map<String, Object>>) map.get(key);
                        Map<String, Object> newMap = new HashMap<>();
                        if(!fileList.isEmpty()) {
                            for (Map<String, Object> fileMap : fileList) {
                                String fileName = "";
                                String fileKey = "";
                                if(fileMap.get("videoFile") != null) {
                                    fileName = fileMap.get("videoFile").toString();
                                    fileKey = "videoFile";
                                } else {
                                    fileName = fileMap.get("imageFile").toString();
                                    fileKey = "imageFile";
                                }
                                String downloadFilePath =
                                        MessageFormat.format(
                                                "http://{0}:{1}/facility/signage/downloadImage/{2}",
                                                serverName, serverPort, fileName);
                                newMap.put(fileKey, downloadFilePath);
                                newMap.put("startDt", fileMap.get("startDt"));
                                newMap.put("endDt", fileMap.get("endDt"));
                                newMap.put("delayTime", fileMap.get("delayTime"));
                                newFileList.add(JsonUtil.convertMapToJson(newMap));
                            }
                        }
                    }
                    newTemplateContent.put(key, newFileList);
                }
                newTemplateContentList.add(newTemplateContent);
        }
        return newTemplateContentList.toString().replaceAll("\\\\", "");
    };

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modSignageLayoutForGm(Map<String, Object> paramMap) throws Exception {
        int templateSeq = (int) paramMap.get("templateSeq");
        EgovMap deleteMap = commonMapper.selectOne(fsp.selectOneSignageLayoutQry(templateSeq));
        List<Map<String, Object>> beforeTemplateContentList = JsonUtil.jsonToListMap((String) deleteMap.get("templateContent"));
        List<String> notDeleteFileList = (List<String>) paramMap.get("notDeleteFileList");
        Map<String, Object> noUseMap = new HashMap<>();
        noUseMap.put("useYn", "N");

        //useYn : N update
        commonMapper.update(fsp.updateSignageLayoutForGmQry(noUseMap));
        commonMapper.update(fsp.updateSignageLayoutForGmQry(paramMap));

        // beforeMediaFile delete
        for (Map<String, Object> map : beforeTemplateContentList) {
            for (String key : map.keySet()) {
                if(key.indexOf("List") > 0) {
                    List<Map<String, Object>> fileList = (List<Map<String, Object>>) map.get(key);
                    if(!fileList.isEmpty()) {
                        for (Map<String, Object> fileMap : fileList) {
                            String fileName = "";
                            if(fileMap.get("videoFile") != null) {
                                fileName = fileMap.get("videoFile").toString();
                            } else {
                                fileName = fileMap.get("imageFile").toString();
                            }
                            boolean deleteFlag = true;
                            // ?????? ?????? ??? ?????? ?????? ?????? ??????
                            for (String notDelFile : notDeleteFileList) {
                                if (notDelFile.equals(fileName)) {
                                    deleteFlag = false;
                                    break;
                                }
                            }
                            if(deleteFlag) {
                                FileUtil.deleteFile("/pages/config/signage/", fileName);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delSignageTemplate(Map<String, Object> paramMap) throws Exception {
        int templateSeq = (int) paramMap.get("templateSeq");
        String templateContent = paramMap.get("templateContent").toString();
        List<Map<String, Object>> templateContentList = JsonUtil.jsonToListMap(templateContent);

        for (Map<String, Object> map : templateContentList) {
            for (String key : map.keySet()) {
                if(key.indexOf("List") > 0) {
                    List<Map<String, Object>> fileList = (List<Map<String, Object>>) map.get(key);
                    if(!fileList.isEmpty()) {
                        for (Map<String, Object> fileMap : fileList) {
                            String fileName = "";
                            if(fileMap.get("videoFile") != null) {
                                fileName = fileMap.get("videoFile").toString();
                            } else {
                                fileName = fileMap.get("imageFile").toString();
                            }
                            FileUtil.deleteFile("/pages/config/signage/", fileName);
                        }
                    }
                } else if(key.indexOf("kind") > 0) {
                    String fileName = "";
                    if("imageFile".equals(map.get("kind"))) {
                        fileName = map.get("value").toString();
                    } else if("videoFile".equals(map.get("kind"))) {
                        fileName = map.get("value").toString();
                    }
                    FileUtil.deleteFile("/pages/config/signage/", fileName);
                }
            }
        }
        commonMapper.delete(fsp.deleteSignageTemplateQry(templateSeq));
    }
}
