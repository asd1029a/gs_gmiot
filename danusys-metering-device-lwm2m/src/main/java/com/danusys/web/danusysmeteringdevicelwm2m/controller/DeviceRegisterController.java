package com.danusys.web.danusysmeteringdevicelwm2m.controller;

import com.danusys.web.danusysmeteringdevicelwm2m.model.AppData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/04/11
 * Time : 11:06 AM
 */
@Slf4j
@RestController
public class DeviceRegisterController {
    private String X_M2M_ORIGIN = "X-M2M-Origin";
    private String X_M2M_RI = "X-M2M-RI";
    private String X_M2M_RSC = "X-M2M-RSC";

    private String CONTENT_TYPE = "Content-type";
    private String CONTENT_TYPE_VALUE = "application/vnd.onem2m-res+xml";

    private String X_MEF_TK = "X-MEF-TK";
    private String X_MEF_EKI = "X-MEF_EKI";
    private String ONEM2M_URL = "https://testbrks.onem2m.uplus.co.kr:8433/";//ASN_CSE-D-33da4b19b5-FSTD/10250/0/0";


    @GetMapping("/")
    public ResponseEntity<?> index() {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }


    /**
     * 1.4 Bootstrap 서비스 서버 interface
     * @param request
     * @param serviceId
     * @return
     */
    @PostMapping("/{serviceId}/bs")
    public ResponseEntity<?> postBs(HttpServletRequest request, @PathVariable String serviceId) {
        log.info("### postBs");
        log.info("### pathInfo   : {}", request.getPathInfo());
        log.info("### requestURI : {}", request.getRequestURI());
        log.info("### serviceId  : {}", serviceId);


        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);

        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.CREATED.value()));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
    }

    /**
     * Register 서비스 서버 interface
     * Register Update 서비스 서버 interface
     * 4.4 De-register 서비스 서버 Interface
     * 13.4 Modem firmware Download 서비스 서버 Interface
     * @param request
     * @param serviceId
     * @return
     */
    @PostMapping("/{serviceId}/rd")
    public ResponseEntity<?> postRd(HttpServletRequest request, @PathVariable String serviceId, @RequestBody Map<String, Object> data) {
        log.info("### postRd");
        log.info("### pathInfo   : {}", request.getPathInfo());
        log.info("### requestURI : {}", request.getRequestURI());
        log.info("### serviceId  : {}", serviceId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);
        String contentType = request.getHeader(CONTENT_TYPE);

        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);
        log.info("### contentType      : {}", contentType);
        log.info("### rdData           : {}", data);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
    }

    /**
     * 6.4 Observe & Data Notification 서비스 서버 Interface
     * 8.4
     * @param request
     * @param serviceId
     * @param objectId
     * @param objectInstanceId
     * @param resourceId
     * @return
     */
    @PostMapping(value = {"/{serviceId}/{objectId}/{objectInstanceId}", "/{serviceId}/{objectId}/{objectInstanceId}/{resourceId}"})
    public ResponseEntity<?> postObserveDataNotice(HttpServletRequest request,
                                                   @PathVariable String serviceId,
                                                   @PathVariable String objectId,
                                                   @PathVariable String objectInstanceId,
                                                   @PathVariable(name = "resourceId", required = false) String resourceId,
                                                   @RequestBody Map<String, Object> data) {
        log.info("### postObserveDataNotice");
        log.info("### pathInfo       : {}", request.getPathInfo());
        log.info("### requestURI     : {}", request.getRequestURI());
        log.info("### @@serviceId    : {}", serviceId);
        log.info("### objectId       : {}", objectId);
        log.info("### objectInstanceId : {}", objectInstanceId);
        log.info("### resourceId     : {}", resourceId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);
        String contentType = request.getHeader(CONTENT_TYPE);

        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);
        log.info("### contentType      : {}", contentType);
        log.info("### data             : {}", data);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));

        /**
         * TODO 6.4 body 값에 xml 데이터 저장???
         */

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
    }


    /**
     * 7.4 Device Status Check 서비스 서버 Interface
     * @param request
     * @param deviceId
     * @param objectId
     * @param objectInstanceId
     * @param resourceId
     * @return
     */
    @GetMapping(value = {"/{deviceId}/{objectId}/{objectInstanceId}",
            "/{deviceId}/{objectId}/{objectInstanceId}/{resourceId}"}, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getDeviceStatusCheck(HttpServletRequest request,
                                                  @PathVariable String deviceId,
                                                  @PathVariable String objectId,
                                                  @PathVariable String objectInstanceId,
                                                  @PathVariable(name = "resourceId", required = false) String resourceId) {
        log.info("### getDeviceStatusCheck");
        log.info("### pathInfo       : {}", request.getPathInfo());
        log.info("### requestURI     : {}", request.getRequestURI());
        log.info("### deviceId       : {}", deviceId);
        log.info("### objectId       : {}", objectId);
        log.info("### objectInstanceId : {}", objectInstanceId);
        log.info("### resourceId     : {}", resourceId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);
        String tk = request.getHeader(X_MEF_TK);
        String eki = request.getHeader(X_MEF_EKI);


        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);
        log.info("### token            : {}", tk);
        log.info("### enrollmentKeyId  : {}", eki);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));

        AppData appData = new AppData();
        appData.setTy(4);
        appData.setCnf("application/octet-stream");
        appData.setCon("MjAyMi0wNC0xMiAxNzoyODo0MC4xODkgTHdNMk0gZGV2aWNl"); //TODO 데이터 넣어야 함.

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(appData);
    }

    /**
     * 8.4 Device Control 서비스 서버 Interface
     * @param request
     * @param deviceId
     * @param objectId
     * @param objectInstanceId
     * @param resourceId
     * @return
     */
//    @PostMapping(value = {"/{deviceId}/{objectId}/{objectInstanceId}/{resourceId}"})
//    public ResponseEntity<?> postDeviceControlCallFlow(HttpServletRequest request,
//                                                   @PathVariable String deviceId,
//                                                   @PathVariable String objectId,
//                                                   @PathVariable String objectInstanceId,
//                                                   @PathVariable String resourceId) {
//        log.info("### postDeviceControlCallFlow");
//        log.info("### pathInfo       : {}", request.getPathInfo());
//        log.info("### requestURI     : {}", request.getRequestURI());
//        log.info("### @@deviceId    : {}", deviceId);
//        log.info("### objectId       : {}", objectId);
//        log.info("### objectInstanceId : {}", objectInstanceId);
//        log.info("### resourceId     : {}", resourceId);
//
//        String origin = request.getHeader(X_M2M_ORIGIN);
//        String ri = request.getHeader(X_M2M_RI);
//        String contentType = request.getHeader(CONTENT_TYPE);
//
//        log.info("### Device Entity Id : {}", origin);
//        log.info("### Request Id       : {}", ri);
//        log.info("### contentType      : {}", contentType);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(X_M2M_RI, ri);
//        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));
//
//        /**
//         * TODO 8.4 body 값에 xml 데이터 저장???
//         */
//
//        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
//    }

    /**
     * 9.4 Device Execute 서비스 서버 Interface
     * @param request
     * @param deviceId
     * @param objectId
     * @param objectInstanceId
     * @param resourceId
     * @return
     */
    @PutMapping(value = {"/{deviceId}/{objectId}/{objectInstanceId}/{resourceId}"})
    public ResponseEntity<?> putDeviceExecute(HttpServletRequest request,
                                                       @PathVariable String deviceId,
                                                       @PathVariable String objectId,
                                                       @PathVariable String objectInstanceId,
                                                       @PathVariable String resourceId) {
        log.info("### putDeviceExecute");
        log.info("### pathInfo       : {}", request.getPathInfo());
        log.info("### requestURI     : {}", request.getRequestURI());
        log.info("### @@deviceId    : {}", deviceId);
        log.info("### objectId       : {}", objectId);
        log.info("### objectInstanceId : {}", objectInstanceId);
        log.info("### resourceId     : {}", resourceId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);
        String contentType = request.getHeader(CONTENT_TYPE);

        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);
        log.info("### contentType      : {}", contentType);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));

        /**
         * TODO 9.4 body 값에 xml 데이터 저장???
         */

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
    }


    /**
     * 10.4 External Observe 서비스 서버 Interface
     * @param request
     * @param deviceId
     * @param objectId
     * @param objectInstanceId
     * @param resourceId
     * @return
     */
    @GetMapping(value = {"/{serviceId}/ob/{objectId}/{objectInstanceId}",
            "/{serviceId}/ob/{objectId}/{objectInstanceId}/{resourceId}"})
    public ResponseEntity<?> getExternalObserve(HttpServletRequest request,
                                                  @PathVariable String deviceId,
                                                  @PathVariable String objectId,
                                                  @PathVariable String objectInstanceId,
                                                  @PathVariable(name = "resourceId", required = false) String resourceId) {
        log.info("### getExternalObserve");
        log.info("### pathInfo       : {}", request.getPathInfo());
        log.info("### requestURI     : {}", request.getRequestURI());
        log.info("### deviceId       : {}", deviceId);
        log.info("### objectId       : {}", objectId);
        log.info("### objectInstanceId : {}", objectInstanceId);
        log.info("### resourceId     : {}", resourceId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);
        String tk = request.getHeader(X_MEF_TK);
        String eki = request.getHeader(X_MEF_EKI);

        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);
        log.info("### token            : {}", tk);
        log.info("### enrollmentKeyId  : {}", eki);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));


        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
    }

    /**
     * 10.4 Cancel Observe 서비스 서버 Interface
     * @param request
     * @param deviceId
     * @param objectId
     * @param objectInstanceId
     * @param resourceId
     * @return
     */
    @GetMapping(value = {"/{serviceId}/obc/{objectId}/{objectInstanceId}",
            "/{serviceId}/obc/{objectId}/{objectInstanceId}/{resourceId}"})
    public ResponseEntity<?> getCancelObserve(HttpServletRequest request,
                                                                @PathVariable String deviceId,
                                                                @PathVariable String objectId,
                                                                @PathVariable String objectInstanceId,
                                                                @PathVariable(name = "resourceId", required = false) String resourceId) {
        log.info("### getCancelObserve");
        log.info("### pathInfo       : {}", request.getPathInfo());
        log.info("### requestURI     : {}", request.getRequestURI());
        log.info("### deviceId       : {}", deviceId);
        log.info("### objectId       : {}", objectId);
        log.info("### objectInstanceId : {}", objectInstanceId);
        log.info("### resourceId     : {}", resourceId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);
        String tk = request.getHeader(X_MEF_TK);
        String eki = request.getHeader(X_MEF_EKI);

        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);
        log.info("### token            : {}", tk);
        log.info("### enrollmentKeyId  : {}", eki);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));


        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
    }

    /**
     * 11.4 Create Object 서비스 서버 Interface
     * @param request
     * @param deviceId
     * @param objectId
     * @return
     */
    @PostMapping(value = {"/{deviceId}/{objectId}"})
    public ResponseEntity<?> postCreateObject(HttpServletRequest request,
                                              @PathVariable String deviceId,
                                              @PathVariable String objectId) {
        log.info("### postCreateObject");
        log.info("### pathInfo       : {}", request.getPathInfo());
        log.info("### requestURI     : {}", request.getRequestURI());
        log.info("### deviceId       : {}", deviceId);
        log.info("### objectId       : {}", objectId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);
        String tk = request.getHeader(X_MEF_TK);
        String eki = request.getHeader(X_MEF_EKI);

        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);
        log.info("### token            : {}", tk);
        log.info("### enrollmentKeyId  : {}", eki);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));


        /**
         * TODO 11.4 body 값에 xml 데이터 저장???
         */

        AppData appData = new AppData();
//        appData.setTy(4);
        appData.setCnf(MediaType.TEXT_PLAIN_VALUE);
        appData.setCon("/{ObjectId}/{Object InstanceId}"); //TODO 데이터 넣어야 함.


        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(appData);
    }

    /**
     * 12.4 Delete Object 서비스 서버 Interface
     * @param request
     * @param deviceId
     * @param objectId
     * @return
     */
    @DeleteMapping(value = {"/{deviceId}/{objectId}/{objectInstanceId}"})
    public ResponseEntity<?> deleteObject(HttpServletRequest request,
                                              @PathVariable String deviceId,
                                              @PathVariable String objectId,
                                              @PathVariable String objectInstanceId) {
        log.info("### deleteObject");
        log.info("### pathInfo          : {}", request.getPathInfo());
        log.info("### requestURI        : {}", request.getRequestURI());
        log.info("### deviceId          : {}", deviceId);
        log.info("### objectId          : {}", objectId);
        log.info("### objectInstanceId  : {}", objectInstanceId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);
        String tk = request.getHeader(X_MEF_TK);
        String eki = request.getHeader(X_MEF_EKI);

        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);
        log.info("### token            : {}", tk);
        log.info("### enrollmentKeyId  : {}", eki);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));

        /**
         * TODO 11.4 body 값에 xml 데이터 저장???
         */

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
    }

}
