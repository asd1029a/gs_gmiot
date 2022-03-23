package com.danusys.web.drone.controller;

import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.drone.dto.response.FileName;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.service.DroneDetailsService;
import com.danusys.web.drone.service.DroneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;


@RestController
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class DroneFileController {

    private final DroneService droneService;
    private final DroneDetailsService droneDetailsService;


    /*
     url:/file/upload/drone,
     parameter: MultipartFile[] uploadFile, HttpServletRequest request, long droneId
     @param uploadFile : 업로드 할 파일
     @param request :요청 request
     @param droneId : 적용할 드론 id
     return : 파일 이름 ,
     do : db에 파일 이름 저장 , 파일 home 폴더에 ajax 요청 경로로 폴더 생성해서 저장

     */
    @PostMapping(value = "/upload/drone")
    public ResponseEntity<?> fileUpload(MultipartFile[] uploadFile, HttpServletRequest request, long droneId) {


        if (droneId == 0) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            //return null;
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

    //  @ResponseBody
    // @GetMapping(value = "/image/{imageName:.+}",produces = {MediaType.IMAGE_JPEG_VALUE,MediaType.IMAGE_GIF_VALUE,MediaType.IMAGE_PNG_VALUE})

//    @GetMapping(value = "/image/{imageName:.+}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
//    public ResponseEntity<byte[]> getImage(@PathVariable("imageName") String imageName, HttpServletRequest request) throws IOException {
//
//
//        if (imageName == null || imageName.isEmpty() || imageName.equals("null")) {
//            return null;
//        }
//        byte[] image = FileUtil.getImage(imageName, request);
//        //    log.info("에러왜나??{}",image);
//        return ResponseEntity.status(HttpStatus.OK).body(image);
//    }

    @GetMapping(value = "/image/{imageName:.+}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public void getImage(@PathVariable("imageName") String imageName, HttpServletRequest request, HttpServletResponse response) throws IOException {


        if (!(imageName == null || imageName.isEmpty() || imageName.equals("null"))) {
            FileUtil.getImage(imageName, request, response);
        }

    }


    @ResponseBody
    @RequestMapping(value = "/{fileName:.+}")
    public void fileDownload(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable("fileName") String fileName) throws IOException {

        FileUtil.fileDownload(request, response, fileName);

    }

//    @ResponseBody
//    @PostMapping("/excel/download")
//    public ResponseEntity<?> excelDownload(HttpServletRequest request, HttpServletResponse response, @RequestBody ArrayList<Map<String, Object>> paramMap) throws IOException {
//
//
//        FileUtil.excelDownload(request, response, paramMap);
//
//        return ResponseEntity.status(HttpStatus.OK).body();
//    }

    /**
     * excel donwload
     * 엑셀 다운로드
     *
     * @param response
     * @param paramMap ex)
     *                 paramMap = {
     *                 dataMap: resultData,  <- 조회한 결과
     *                 fileName: "Log.xlsx",
     *                 headerList: ["아이디", "드론이름", "미션이름", "입력날짜"]
     *                 };
     *                 <p>
     *                 dataMap-> List<Map<String,Object>> dataMap
     *                 headerList -> List<String> heartList
     *                 dataMap -> 엑셀에 담을 data map 리스트
     *                 headerList -> 엑셀 첫줄에 해더 부분을 임의로 지정할 경우
     *                 <p>
     *                 필수 : dataMap ,
     *                 선택 : headerList
     *                 *
     *                 return response에 blob 데이터를 보낸다.
     * @throws IOException
     */
    @ResponseBody
    @PostMapping("/excel/download")
    public void excelDownload(HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws IOException {

        Workbook wb = null;
//        log.info("param={}",paramMap);
        wb = FileUtil.excelDownload2(paramMap);


        wb.write(response.getOutputStream());
        wb.close();


    }
}


