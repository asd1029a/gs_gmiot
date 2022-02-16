package com.danusys.web.commons.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
@Slf4j
@Component
public class FileUtil {


    private static String[] staticExtensionList ;
    @Value("#{'${danusys.file.extension}'.split(',')}")
    public void setStaticExtensionList(String[] extensionList){
        staticExtensionList=extensionList;
    }
    private static String STATIC_EXTERNAL_FILE_PATH ;
    @Value("${danusys.path.root}")
    public void setExternalFilePath(String EXTERNAL_FILE_PATH){
       STATIC_EXTERNAL_FILE_PATH=EXTERNAL_FILE_PATH;
    }

    public static String uploadAjaxPost(MultipartFile[] uploadFile, String folderPath) {

        String sPath = STATIC_EXTERNAL_FILE_PATH;
        String uploadFileName = null;
        String savedFileName = null;
        log.info("sPath={},folderPath={}", sPath, folderPath);
        File uploadPath = new File(sPath, folderPath);
        String filePath = null;
        log.info("upload path : " + uploadPath);


        if (uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }

        for (MultipartFile multipartFile : uploadFile) {

            log.info("Upload File Name : " + multipartFile.getOriginalFilename());
            log.info("Upload File Size : " + multipartFile.getSize());
            filePath = multipartFile.getOriginalFilename();
            uploadFileName = multipartFile.getOriginalFilename();
//            try {
//                uploadFileName = new String(uploadFileName.getBytes("8859_1"), "UTF-8");
//
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }

            uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);


            log.info("only file name: " + uploadFileName);


            for (String extension : staticExtensionList) {
                if (uploadFileName.contains(extension))
                    return null;
            }

            savedFileName = getFolder() + uploadFileName;
            File savefile = new File(uploadPath, savedFileName);

            try {
                multipartFile.transferTo(savefile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return savedFileName;
    }

    private static String getFolder() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String str = sdf.format(date);
//        return str.replace("-","_");
        return str + "_";
    }
}
