package com.danusys.guardian.controller;

import com.danusys.guardian.common.util.CommonUtil;
import com.danusys.guardian.common.util.EgovFileScrty;
import com.danusys.guardian.common.util.JsonUtil;
import com.danusys.guardian.common.util.PaginationInfo;
import com.danusys.guardian.model.ComDefaultVO;
import com.danusys.guardian.model.LoginVO;
import com.danusys.guardian.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class BaseRestController {

    public static final String EXCEPTION_MESSAGE = "";

    @Value("${danusys.salt.text}")
    private String saltText = "";

    @Value("${danusys.page.unit}")
    private int pageUnit = 10;

    private final BaseService baseService;

    public BaseRestController(BaseService baseService) {
        this.baseService = baseService;
    }

    @PostMapping("/selectNoSession/{sqlid}/action")
    public Map<String, Object> selectNoSession(
            @PathVariable("sqlid") String sqlid,
            @RequestBody Map<String, Object> param) throws IOException, ModelAndViewDefiningException {
//        response.setHeader("Access-Control-Allow-Origin", "*"); // 크로스도메인 허용

        log.trace("### sqlid : {}", sqlid);
        log.trace("### param : {}", param.toString());
        if(StringUtils.isEmpty(sqlid) && StringUtils.isEmpty(param.get("id"))) {
            throw new IllegalArgumentException("Error Default param");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();

        resList = baseService.baseSelectList(sqlid, param);

        map.put("rows", resList);
        return map;
    }


    /**
     * FuncName : select() FuncDesc : 조회 Param : sqlid : SQL ID Return : String
     * @throws IOException
     */
    @PostMapping("/select/{sqlid}/action")
    public Map<String, Object> baseSelect(@PathVariable("sqlid") String sqlid,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestBody Map<String, Object> param
    ) throws IOException
    {
//        PrintWriter out = null;
//			response.setCharacterEncoding("UTF-8");
//			response.setHeader("Access-Control-Allow-Origin", "*"); // 크로스도메인 허용
//			Map<String, Object> param = null;
        HttpSession session = request.getSession(false);
        LoginVO user = (LoginVO) session.getAttribute("admin");

        log.trace("# 세션아이디 :: " + session.getId());

//			if (request.getParameter("param").trim().equals("") == true) {
//				param = new HashMap<String, Object>();
//			} else {
//				param = JsonUtil.JsonToMap(request.getParameter("param"));
//			}
        /*
         * if (param.get("userId") != null) { if
         * (session.getAttribute("SESSION_USER_ID") != param.get("userId")) {
         * param.put("userId", session.getAttribute("SESSION_USER_ID")); } }
         */

        Map<String, Object> result = new HashMap<String, Object>();

        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        try {
            param.put("recordCountPerPage", "-1");
            resList = baseService.baseSelectList(sqlid, param);
            if (user != null) {
                result.put("rows", resList);
            } else {
                result.put("rows", "sessionOut");
            }
//            out = response.getWriter();
//            out.write(JsonUtil.MapToJson(result)); // Ajax Retun Json String
            return result;
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        Map<String, Object> path = new HashMap<>();
        path.put("path", request.getParameter("path"));

        return path;
    }

    /**
     * FuncName : ajaxInsert() FuncDesc : Ajax 등록 Param : sqlid : SQL ID Return :
     * String
     */
    @RequestMapping(value = "/ajax/insert/{sqlid}/action", method = RequestMethod.POST)
    public String ajaxInsert(@PathVariable("sqlid") String sqlid, @RequestBody Map<String, Object> param) throws IOException {

        int iResult = 0;
//        PrintWriter out = null;
//        response.setCharacterEncoding("UTF-8");
//        Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));

        String result = "";
        try {
            // Insert
            iResult = baseService.baseInsert(sqlid, param);

            if (iResult > 0) {
                result = JsonUtil.OneStringToJson("SUCCESS");
            } else {
                result = JsonUtil.OneStringToJson("SESSION");
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return result;
    }

    /**
     * 함수명 : ajaxInsertReturnSelectKey() FuncDesc : ajax 등록 (Insert Key 반환) Param :
     * sqlid : SQL ID Return : String
     */
    @PostMapping("/ajax/insert/idkey/{sqlid}/action")
    public String ajaxInsertReturnSelectKey(@PathVariable("sqlid") String sqlid, Map<String, Object> param) throws IOException {

        String result = "";
//        PrintWriter out = null;
//        response.setCharacterEncoding("UTF-8");
//        Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));
//        log.trace(param);
        try {
            // Insert
            String sResult = baseService.baseInsertReturnSelectKey(sqlid, param);

            if (sResult != "") {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson(sResult);
            } else {
//                out = response.getWriter();
                result =  JsonUtil.OneStringToJson("SESSION");
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return result;
    }


    /**
     * FuncName : multiAjax FuncDesc : 스크립트 세션 상태 체크 Param : param Return : String
     */
    @PostMapping("/multiAjax/action")
    public String multiAjax(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {

        int iResult = 0;
        PrintWriter out = null;

//        Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));
        // List<Map<String, Object>> recordset = JsonUtil.MapToJson();

        log.trace("**************************************************************");
        log.trace("param : {}", param.toString());
        log.trace("**************************************************************");

        String singleInsertSid = (String) param.get("singleInsertSid");
        String singleUpdateSid = (String) param.get("singleUpdateSid");
        String singleDeleteSid = (String) param.get("singleDeleteSid");
//        String multiInsertsqlmapid = (String) param.get("multiInsertsqlmapid");
//        String multiUpdatesqlmapid = (String) param.get("multiUpdatesqlmapid");
//        String multiDeletesqlmapid = (String) param.get("multiDeletesqlmapid");

//		String saltText = saltText;
        String password = (String) param.get("password");
        if(password != null && !password.isEmpty()) {
            String encryptPassword = EgovFileScrty.encryptPassword(password, saltText);
            param.put("password", encryptPassword);
        }

        if (singleInsertSid != null && !singleInsertSid.isEmpty()) {
            log.trace("**************************************************************");
            log.trace("sqlmapid : {}", singleInsertSid);
            log.trace("param : {}", param.toString());
            log.trace("**************************************************************");

            iResult += baseService.baseInsert(singleInsertSid, param);
        }

        if (singleUpdateSid != null && !singleUpdateSid.isEmpty()) {
            log.trace("**************************************************************");
            log.trace("sqlmapid : {}", singleUpdateSid);
            log.trace("pMap : {}", param.toString());
            log.trace("**************************************************************");

            iResult += baseService.baseUpdate(singleUpdateSid, param);
        }

        if (singleDeleteSid != null && !singleDeleteSid.isEmpty()) {
            log.trace("**************************************************************");
            log.trace("sqlmapid : {}", singleDeleteSid);
            log.trace("param : {}", param.toString());
            log.trace("**************************************************************");

            iResult += baseService.baseDelete(singleDeleteSid, param);
        }

        log.trace("iResult = [" + iResult + "]");
        String result = "";
        if (iResult > 0) {
//            out = response.getWriter();
            result = JsonUtil.OneStringToJson("SUCCESS");
        } else {
//            out = response.getWriter();
            result = JsonUtil.OneStringToJson(EXCEPTION_MESSAGE);
        }
        return result;

        /*
         * if(recordset != null && recordset.size() > 0) { log.trace(
         * "**************************************************************");
         * log.trace("recordset : " + recordset.toString());
         * log.trace(
         * "**************************************************************");
         *
         * if(multiInsertsqlmapid != null && !multiInsertsqlmapid.isEmpty()) { for (int
         * i = 0; i < recordset.size(); i++) { HashMap dataList = (HashMap)
         * recordset.get(i); baseService.baseInsert(multiInsertsqlmapid, dataList); } }
         *
         * if(multiUpdatesqlmapid != null && !multiUpdatesqlmapid.isEmpty()) { for (int
         * i = 0; i < recordset.size(); i++) { HashMap dataList = (HashMap)
         * recordset.get(i); baseService.baseUpdate(multiUpdatesqlmapid, dataList); } }
         *
         * if(multiDeletesqlmapid != null && !multiDeletesqlmapid.isEmpty()) { for (int
         * i = 0; i < recordset.size(); i++) { HashMap dataList = (HashMap)
         * recordset.get(i); baseService.baseDelete(multiDeletesqlmapid, dataList); } }
         * }
         */
    }

    /**
     * FuncName : baseSessionCheck FuncDesc : 스크립트 세션 상태 체크 Param : userId : userId
     * Return : String
     */
    @PostMapping("/baseSessionCheck/action")
    public String baseSessionCheck(@RequestBody Map<String, Object> param)
            throws IOException {

        String result = "";
//        PrintWriter out = null;
//        Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));

        try {
            result = "true";
            // model.addAttribute("message", "세션이 만료되었습니다.");
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        return JsonUtil.OneStringToJson(result);
    }


    @PostMapping("/selectList/{sqlid}/action")
    public Map<String, Object> selectUserList(
            @PathVariable("sqlid") String sqlid,
            ComDefaultVO vo,
            HttpServletRequest request,
            @RequestBody Map<String, Object> param) throws IOException, ModelAndViewDefiningException {
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("Access-Control-Allow-Origin", "*"); // 크로스도메인 허용
//        Map<String, Object> param = null;
        HttpSession session = request.getSession(false);
        LoginVO user = (LoginVO) session.getAttribute("admin");

//        if (request.getParameter("param").trim().equals("") == true) {
//            param = new HashMap<String, Object>();
//        } else {
//            param = JsonUtil.JsonToMap(request.getParameter("param"));
//        }
        log.trace("파라미터 " + param);

        /** EgovPropertyService.sample */
        vo.setPageUnit(pageUnit);
        vo.setPageIndex(vo.getPage());

        Map<String, Object> map = new HashMap<String, Object>();
        if (user != null) {
            List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
            String total;
            /** pageing */
            PaginationInfo paginationInfo = new PaginationInfo();
            paginationInfo.setCurrentPageNo(vo.getPageIndex());
            paginationInfo.setRecordCountPerPage(vo.getPageSize());
            paginationInfo.setPageSize(vo.getPageSize());

            vo.setFirstIndex(paginationInfo.getFirstRecordIndex());
            vo.setLastIndex(paginationInfo.getLastRecordIndex());
            vo.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
            param.put("firstIndex", paginationInfo.getFirstRecordIndex());
            param.put("lastIndex", paginationInfo.getLastRecordIndex());
            param.put("recordCountPerPage", paginationInfo.getRecordCountPerPage());

            resList = baseService.baseSelectList(sqlid, param);
            total = baseService.baseSelectOne(sqlid + "Cnt", param);

            paginationInfo.setTotalRecordCount(Integer.parseInt(total));
            map.put("rows", resList);
            map.put("total", total);

        } else {
            map.put("rows", "sessionOut");
        }
        return map;
    }


    /**
     * FuncName : selectPanelList() FuncDesc : 패널리스트 가져오기 Param : sqlid : SQL ID
     * Return : map
     */
    @PostMapping("/selectPanelList/{sqlid}/action")
    public Map<String, Object> selectPanelList(
            @PathVariable("sqlid") String sqlid,
            ComDefaultVO vo,
            HttpServletRequest request,
            @RequestBody Map<String, Object> param) throws IOException, ModelAndViewDefiningException {
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("Access-Control-Allow-Origin", "*"); // 크로스도메인 허용
//        Map<String, Object> param = null;
        Map<String, Object> map = new HashMap<String, Object>();
        HttpSession session = request.getSession(false);
        if(session == null) {
            map.put("rows", "sessionOut");
            return map;
        }
        LoginVO user = (LoginVO) session.getAttribute("admin");

//        if (request.getParameter("param").trim().equals("") == true) {
//            param = new HashMap<String, Object>();
//        } else {
//            param = JsonUtil.JsonToMap(request.getParameter("param"));
//        }

        /** EgovPropertyService.sample */
        vo.setPageUnit(pageUnit);
        vo.setPageIndex(vo.getPage());

        if (user != null) {
            List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
            /** pageing */
            param.get("onPage");
            try {
                int countPage = Integer.parseInt(param.get("onPage").toString());
                int page = 1;
                if (param.get("page") != null) {
                    page = Integer.parseInt(param.get("page").toString());
                }

                int startCount = (page - 1) * countPage;
                int endCount = page * countPage;
                param.put("firstIndex", startCount);
                param.put("lastIndex", endCount);

                resList = baseService.baseSelectList(sqlid, param);
            } catch(NullPointerException e) {
                param.put("recordCountPerPage", -1);
                resList = baseService.baseSelectList(sqlid, param);
            } finally {
                map.put("rows", resList);
            }

        } else {
            map.put("rows", "sessionOut");
        }
        return map;
    }

    /**
     * FuncName : selectPanelListCount() FuncDesc : 패널리스트 count Param : sqlid : SQL
     * ID Return : map
     */
    @PostMapping("/selectPanelListCount/{sqlid}/action")
    public Map<String, Object> selectPanelListCount(
            @PathVariable("sqlid") String sqlid,
            ComDefaultVO vo,
            HttpServletRequest request,
            @RequestBody Map<String, Object> param) throws IOException, ModelAndViewDefiningException {
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("Access-Control-Allow-Origin", "*"); // 크로스도메인 허용
//        Map<String, Object> param = null;
        HttpSession session = request.getSession(false);
        LoginVO user = (LoginVO) session.getAttribute("admin");

//        if (request.getParameter("param").trim().equals("") == true) {
//            param = new HashMap<String, Object>();
//        } else {
//            param = JsonUtil.JsonToMap(request.getParameter("param"));
//        }
        log.trace("## param : {} ", param);

        /** EgovPropertyService.sample */
        vo.setPageUnit(pageUnit);
        vo.setPageIndex(vo.getPage());

        Map<String, Object> map = new HashMap<String, Object>();
        if (user != null) {
            List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();

            resList = baseService.baseSelectList(sqlid, param);
            log.trace("menuNm : {} ", param.get("menuNm"));
            // 화면에 보이는 페이지 설정
            if (param.get("menuNm").equals("facility")) {
                map.put("total", resList.get(0).get("TOTAL"));
                map.put("nor", resList.get(0).get("NOR"));
                map.put("abnor", resList.get(0).get("ABNOR"));
            } else if (param.get("menuNm").equals("event")) {
                map.put("total", resList);
            }

        } else {
            map.put("rows", "sessionOut");
        }
        return map;
    }



    /**
     * FuncName : ajaxUpdate() FuncDesc : Ajax 수정 Param : sqlid : SQL ID Return :
     * String
     */
    @PostMapping(value = "/ajax/update/{sqlid}/action")
    public String ajaxUpdate(@PathVariable("sqlid") String sqlid,
//                           HttpServletRequest request,
                           @RequestBody Map<String, Object> param) throws IOException {


        String result = "";
//        PrintWriter out = null;
//        response.setCharacterEncoding("UTF-8");
//        Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));

        try {
            // Update
            int iResult = baseService.baseUpdate(sqlid, param);

            if (iResult > 0) {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson("SUCCESS");
            } else {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson("SESSION");
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return result;
    }

    /**
     * FuncName : ajaxDelete() FuncDesc : Ajax 삭제 Param : sqlid : SQL ID Return :
     * String
     */
    @PostMapping("/ajax/delete/{sqlid}/action")
    public String ajaxDelete(@PathVariable("sqlid") String sqlid,
                           @RequestBody Map<String, Object> param) throws IOException {

        String result = "";
//        PrintWriter out = null;
//        response.setCharacterEncoding("UTF-8");
//        Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));

        try {
            // Delete
            int iResult = baseService.baseDelete(sqlid, param);

            if (iResult > 0) {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson("SUCCESS");
            } else {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson("SESSION");
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return result;
    }

    /**
     * 함수명 : ajaxSave() FuncDesc : Ajax C/U/D N건 처리 Param : sqlCid : INSERT SQL ID,
     * sqlUid : UPDATE SQL ID, sqlDid : DELETE SQL ID Return : String
     */
    @PostMapping(value = "/ajax/save/{sqlCid}/{sqlUid}/{sqlDid}/action")
    public String ajaxSave(@PathVariable("sqlCid") String sqlCid,
                         @PathVariable("sqlUid") String sqlUid,
                         @PathVariable("sqlDid") String sqlDid,
                         @RequestBody Map<String, Object> param) {
//        int iResult = 0;
//        PrintWriter out = null;
//        response.setCharacterEncoding("UTF-8");
        String result = "";

        try {

            if (param != null) {
                JSONArray jsonArray = new JSONArray(param);

                log.trace("sqlCid = {}", sqlCid);
                log.trace("sqlUid = {}", sqlUid);
                log.trace("sqlDid = {}", sqlDid);
                log.trace("jsonArray = {}", jsonArray.length());

                int iResult = baseService.ajaxSave(sqlCid, sqlUid, sqlDid, jsonArray);

                if (iResult == 1) {
//                    out = response.getWriter();
                    result = JsonUtil.OneStringToJson("SUCCESS");
                } else {
//                    out = response.getWriter();
                    result = JsonUtil.OneStringToJson(EXCEPTION_MESSAGE);
                }
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return result;
    }

    /**
     * 함수명 : ajaxInsertKey() FuncDesc : Ajax C/U/D N건 처리 Param : sqlid : 첫번째 INSERT
     * SQL ID param : 첫번째 INSERT Parameter Data jsonArray : C/U/D Parameter Data
     * Return : String : idKey
     */
    @PostMapping("/ajax/insert/multiTransaction/idkey/{sqlid}/action")
    public String ajaxInsertKey(@PathVariable("sqlid") String sqlid,
                              @RequestBody Map<String, Object> param) {
//        boolean nullChk = true;
//        String sResult = "";
//        PrintWriter out = null;
//        response.setCharacterEncoding("UTF-8");
        String result = "";

        try {
            Map<String, Object> param1 = (Map<String, Object>) param.get("param1");//JsonUtil.JsonToMap(request.getParameter("param1"));

            JSONArray jsonArray = new JSONArray(param.get("param2").toString());

            // Insert
            String sResult = baseService.ajaxInsertKey(sqlid, param1, jsonArray);

            if (sResult != "") {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson(sResult);
            } else {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson("ERROR");
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        return result;
    }

    /**
     * 함수명 : insertKeyMultiTransaction() FuncDesc : Insert Key Transaction C/U/D N건
     * 처리 Param : sqlid : SQL ID Return : String
     */
    @PostMapping("/insertKeyMultiTransaction/{sqlid1}/{sqlid2}/{sqlid3}/{sqlid4}/action")
    public String insertKeyMultiTransaction(@PathVariable("sqlid1") String sqlid1,
                                          @PathVariable("sqlid2") String sqlid2,
                                          @PathVariable("sqlid3") String sqlid3,
                                          @PathVariable("sqlid4") String sqlid4,
                                          @RequestBody Map<String, Object> param) {
//        boolean nullChk = true;
//        int iResult = 0;
//        PrintWriter out = null;
//        response.setCharacterEncoding("UTF-8");
        String result = "";
        log.trace("sqlid1 = {}", sqlid1);
        log.trace("sqlid2 = {}", sqlid2);
        log.trace("sqlid3 = {}", sqlid3);
        log.trace("sqlid4 = {}", sqlid4);

        try {
            JSONArray jsonArray1 = new JSONArray(param.get("param1").toString());
            JSONArray jsonArray2 = new JSONArray(param.get("param2").toString());
            JSONArray jsonArray3 = new JSONArray(param.get("param3").toString());
            JSONArray jsonArray4 = new JSONArray(param.get("param4").toString());

            int iResult = baseService.insertKeyMultiTransaction(sqlid1, sqlid2, sqlid3, sqlid4, jsonArray1, jsonArray2,
                    jsonArray3, jsonArray4);

            if (iResult > 0) {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson("SUCCESS");
            } else {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson(EXCEPTION_MESSAGE);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }

        return result;
    }

    /**
     * FuncName : setMultiTransaction() FuncDesc : Transaction C/U/D N건 처리 Param :
     * sqlid1 : C/U/D 1번 SQL ID sqlid2 : C/U/D 2번 SQL ID sqlid3 : C/U/D 3번 SQL ID
     * sqlid4 : C/U/D 4번 SQL ID jsonArray1 : C/U/D 1번 JSONArray jsonArray2 : C/U/D
     * 2번 JSONArray jsonArray3 : C/U/D 3번 JSONArray jsonArray4 : C/U/D 4번 JSONArray
     * Return : int
     */
    @PostMapping("/multiTransaction/{sqlid1}/{sqlid2}/{sqlid3}/{sqlid4}/action")
    public String setMultiTransaction(@PathVariable("sqlid1") String sqlid1,
                                    @PathVariable("sqlid2") String sqlid2,
                                    @PathVariable("sqlid3") String sqlid3,
                                    @PathVariable("sqlid4") String sqlid4,
                                    @RequestBody Map<String, Object> param) {
//        boolean nullChk = true;
//        int iResult = 0;
//        PrintWriter out = null;
//        response.setCharacterEncoding("UTF-8");

        String result = "";
        log.trace("sqlid1 = {}", sqlid1);
        log.trace("sqlid2 = {}", sqlid2);
        log.trace("sqlid3 = {}", sqlid3);
        log.trace("sqlid4 = {}", sqlid4);

        try {
            JSONArray jsonArray1 = new JSONArray(param.get("param1").toString());
            JSONArray jsonArray2 = new JSONArray(param.get("param2").toString());
            JSONArray jsonArray3 = new JSONArray(param.get("param3").toString());
            JSONArray jsonArray4 = new JSONArray(param.get("param4").toString());

            int iResult = baseService.setMultiTransaction(sqlid1, sqlid2, sqlid3, sqlid4, jsonArray1, jsonArray2,
                    jsonArray3, jsonArray4);

            if (iResult > 0) {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson("SUCCESS");
            } else {
//                out = response.getWriter();
                result = JsonUtil.OneStringToJson(EXCEPTION_MESSAGE);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return result;
    }

    /**
     * FuncName : writeConnectLog FuncDesc : 접속 로그 기록 Return : String
     */
    @PostMapping("/writeConnectLog/action")
    public String writeConnectLog(HttpServletRequest request,
                                  @RequestBody Map<String, Object> param) throws IOException {

        String result = "";
//        int iResult = 0;
//        PrintWriter out = null;
//        Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));

        try {
            param.put("connIP", CommonUtil.getClientIp(request));
            int iResult = baseService.baseInsert("Common.insertConnectLog", param);

            if (iResult > 0) {
                result = "SUCCESS";
            } else {
                result = "FAIL";
            }
//            out = response.getWriter();
//            out.write();
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        return JsonUtil.OneStringToJson(result);
    }

}
