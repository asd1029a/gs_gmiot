package com.danusys.web.smartmetering.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    private String X_MEF_TK = "X-MEF-TK";
    private String X_MEF_EKI = "X-MEF_EKI";


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
     * @param request
     * @param serviceId
     * @return
     */
    @PostMapping("/{serviceId}/rd")
    public ResponseEntity<?> postRd(HttpServletRequest request, @PathVariable String serviceId) {
        log.info("### postRd");
        log.info("### pathInfo   : {}", request.getPathInfo());
        log.info("### requestURI : {}", request.getRequestURI());
        log.info("### serviceId  : {}", serviceId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);


        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
    }



    @PostMapping(value = {
            "/{serviceId}/{objectId}/{objectInstance}",
            "/{serviceId}/{objectId}/{objectInstance}/{resourceId}"})
    public ResponseEntity<?> postObserveDataNotice(HttpServletRequest request,
                                                   @PathVariable String serviceId,
                                                   @PathVariable String objectId,
                                                   @PathVariable String objectInstance,
                                                   @PathVariable String resourceId) {
        log.info("### postObserveDataNotice");
        log.info("### pathInfo   : {}", request.getPathInfo());
        log.info("### requestURI : {}", request.getRequestURI());
        log.info("### serviceId  : {}", serviceId);
        log.info("### objectId  : {}", objectId);
        log.info("### objectInstance  : {}", objectInstance);
        log.info("### resourceId  : {}", resourceId);

        String origin = request.getHeader(X_M2M_ORIGIN);
        String ri = request.getHeader(X_M2M_RI);

        log.info("### Device Entity Id : {}", origin);
        log.info("### Request Id       : {}", ri);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_M2M_RI, ri);
        headers.add(X_M2M_RSC, String.valueOf(HttpStatus.OK.value()));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("");
    }

    @PostMapping(value = {
            "/{deviceId}/{objectId}/{objectInstance}",
            "/{deviceId}/{objectId}/{objectInstance}/{resourceId}"})
    public ResponseEntity<?> getDeviceStatusCheck(HttpServletRequest request,
                                                   @PathVariable String deviceId,
                                                   @PathVariable String objectId,
                                                   @PathVariable String objectInstance,
                                                   @PathVariable String resourceId) {
        log.info("### getDeviceStatusCheck");
        log.info("### pathInfo   : {}", request.getPathInfo());
        log.info("### requestURI : {}", request.getRequestURI());
        log.info("### serviceId  : {}", deviceId);
        log.info("### objectId  : {}", objectId);
        log.info("### objectInstance  : {}", objectInstance);
        log.info("### resourceId  : {}", resourceId);

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

}
