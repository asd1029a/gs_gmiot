package com.danusys.web.drone.controller;

import com.danusys.web.commons.util.FileUtil;
import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.service.DroneDetailsService;
import com.danusys.web.drone.service.DroneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;

@RestController
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class DroneFileController {

    private final DroneService droneService;
    private final DroneDetailsService droneDetailsService;


    @ResponseBody
    @PostMapping(value = "/upload/drone", produces = "multipart/form-data;charset=UTF-8")
    public ResponseEntity<?> fileUpload(MultipartFile[] uploadFile, HttpServletRequest request, long droneId) {
        log.info("droneId={}", droneId);
        if (droneId == 0) {
            log.info("여기옴");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            String folderPath = request.getRequestURI();
//            log.info(FileUtil.uploadAjaxPost(uploadFile, folderPath));
            String fileName = FileUtil.uploadAjaxPost(uploadFile, folderPath);
            log.info("fileName={}",fileName);
            DroneDetails setDroneDetails = new DroneDetails();
            setDroneDetails.setThumbnailImg(fileName);
            droneDetailsService.updateDroneDetails(setDroneDetails, droneId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(fileName);
        }


    }
}
