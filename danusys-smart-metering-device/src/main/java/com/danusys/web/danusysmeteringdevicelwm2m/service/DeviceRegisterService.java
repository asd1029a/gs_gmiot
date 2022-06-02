package com.danusys.web.danusysmeteringdevicelwm2m.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/04/11
 * Time : 11:06 AM
 */
@Slf4j
@Service
//@RequiredArgsConstructor
public class DeviceRegisterService {

//    @GetMapping(value = {"/{deviceId}/{objectId}/{objectInstanceId}",
//            "/{deviceId}/{objectId}/{objectInstanceId}/{resourceId}"}, produces = MediaType.APPLICATION_XML_VALUE)



    public void getDeviceStatusCheck() throws Exception {
        log.info("getDeviceStatusCheck");
        //final String url, MediaType mediaType, HttpMethod method, MultiValueMap<String, Object> params, Class clazz
        String om2mUrl = "https://testbrks.onem2m.uplus.co.kr:8433/ASN_CSE-D-33da4b19b5-FSTD/10250/0/0";
//        exchange(final String url, MediaType mediaType, HttpMethod method, MultiValueMap<String, Object> params, Class clazz) throws Exception {
//
//        }
//final String url, MediaType mediaType, HttpMethod method, MultiValueMap<String, Object> params, Class clazz
//        MediaType.APPLICATION_XML_VALUE
//        String result = webClientHelper.exchange(om2mUrl, MediaType.valueOf(MediaType.APPLICATION_XML_VALUE), HttpMethod.GET, String.class);
//        log.info(result);

    }

}
