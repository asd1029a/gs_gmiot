package com.danusys.web.drone.controller;

import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.service.DroneDetailsService;
import com.danusys.web.drone.service.DroneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UrlPathHelper;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class DroneFileController {

    private final DroneService droneService;
    private final DroneDetailsService droneDetailsService;


    @PostMapping(value = "/upload/drone", produces = "multipart/form-data;charset=UTF-8")
    public ResponseEntity<?> fileUpload(MultipartFile[] uploadFile, HttpServletRequest request, long droneId) {
        log.info("droneId={}", droneId);


//        for (MultipartFile multipartFile : uploadFile) {
//            BufferedImage image = null;
//            try {
//                image = ImageIO.read(multipartFile.getInputStream());
//                Integer width = image.getWidth();
//                Integer height = image.getHeight();
//                log.info("width={},height={}", width, height);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }


        if (droneId == 0) {
            log.info("여기옴");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
//            String folderPath = request.getRequestURI();
//            log.info(FileUtil.uploadAjaxPost(uploadFile, folderPath));
            String fileName = FileUtil.uploadAjaxPost(uploadFile, request);
            log.info("fileName={}", fileName);
            DroneDetails setDroneDetails = new DroneDetails();
            setDroneDetails.setThumbnailImg(fileName);
            droneDetailsService.updateDroneDetails(setDroneDetails, droneId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(fileName);
        }


    }

    @GetMapping(value = "/image/{imageName:.+}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> userSearch(@PathVariable("imageName") String imageName, HttpServletRequest request) throws IOException {

        byte[] image = FileUtil.getImage(imageName, request);
        return ResponseEntity.status(HttpStatus.OK).body(image);
    }

    @ResponseBody
    @RequestMapping("/{fileName:.+}")
    public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("fileName") String fileName) throws IOException {

        FileUtil.fileDownload(request, response, fileName);

    }
    @ResponseBody
    @PostMapping("/excel/download")
    public void excelDownload(HttpServletRequest request , HttpServletResponse response, @RequestBody ArrayList<Map<String, Object>> paramMap) throws IOException {


        FileUtil.excelDownload(request,response,paramMap);


    }
}


