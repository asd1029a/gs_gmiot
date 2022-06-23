package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/config")
@RequiredArgsConstructor
public class ConfigController {
    @Value("${danusys.area.code.sig}")
    private String sigCode;
    private final ConfigService commonService;

    /*
    * 공통코드관리: 공통코드 목록 조회
   */
    @PostMapping(value = "/commonCode")
    public ResponseEntity<EgovMap> getListCommonCode(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(commonService.getListCode(paramMap));
    }

    @GetMapping(value = "/commonCode/{pSeq}")
    public ResponseEntity<EgovMap> getCommonCode(@PathVariable("pSeq") int seq) throws Exception {
        return ResponseEntity.ok().body(commonService.getOneCode(seq));
    }

    @GetMapping(value = "/commonCode/eventKind/{pKind}")
    public ResponseEntity<EgovMap> getCommonCode(@PathVariable("pKind") String pKind) throws Exception {
        return ResponseEntity.ok().body(commonService.getOneEventKind(pKind));
    }

    @GetMapping(value = "/mntrPageTypeData/{pageTypeCodeValue}")
    public ResponseEntity<Map> getListMntrInitParam(@PathVariable("pageTypeCodeValue") String pageTypeCodeValue) throws Exception {

        List<EgovMap> paramList = commonService.getListMntrInitParam(pageTypeCodeValue);
        Map<String, Object> result = new HashMap<>();

        paramList.stream().forEach(f -> {
            result.put(f.get("codeId").toString(),null);
        });

        paramList.stream().forEach(f -> {
            String valueStr = f.get("childValue").toString();
            String keyStr = f.get("codeValue").toString();

            String[] strAry = valueStr.split(",");

            Map<String, Object> each = new HashMap<>();
            each = (Map<String, Object>) result.get(f.get("codeId"));
            if( each == null ){
                each = new HashMap<>();
            }
            each.put(keyStr, strAry);
            each.put("sigCode",sigCode);
            result.put(f.get("codeId").toString(), each);
        });

        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/media")
    public ResponseEntity<Map> getVideoInfo(HttpServletRequest req) throws Exception {
        String clientIp = CommonUtil.getClientIp(req);
        // Inet4Address.getLocalHost().getHostAddress();
        String ipRegExp = "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])";
        //"/((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})/g";

        List<String> ipEachClass = new ArrayList<String>(Arrays.asList(clientIp.split("\\.")));
        String ipClassAB = ipEachClass.get(0) + "." + ipEachClass.get(1);

        EgovMap videoNetInfo = commonService.getVideoNetInfo(ipClassAB);
        List<EgovMap> videoConfig = commonService.getVideoConfig();

        Map<String, Object> result = new HashMap<>();
        videoConfig.stream().forEach(f -> {
            String key = f.get("name").toString();
            String value = f.get("value").toString();

            if(key.equals("turnUrl") || key.equals("mediaServerWsUrl")){
                if(videoNetInfo != null){
                    value = value.replaceAll(ipRegExp, videoNetInfo.get("ip").toString());
                }
            }
            result.put(key, value);
        });
        return ResponseEntity.ok().body(result);
    }
}
