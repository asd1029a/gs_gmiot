package com.danusys.web.commons.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URLConnection;

@Slf4j
@Component
public class FileUtil {


    private static String[] staticExtensionList;

    @Value("#{'${danusys.file.extension}'.split(',')}")
    public void setStaticExtensionList(String[] extensionList) {
        staticExtensionList = extensionList;
    }

    private static String STATIC_EXTERNAL_FILE_PATH;

    @Value("${danusys.path.root}")
    public void setExternalFilePath(String EXTERNAL_FILE_PATH) {
        STATIC_EXTERNAL_FILE_PATH = EXTERNAL_FILE_PATH;
    }

    public static String uploadAjaxPost(MultipartFile[] uploadFile, HttpServletRequest request) {
        String folderPath = "";
        String folder[] = request.getHeader("REFERER").split("/");
        for (int i = 0; i < folder.length; i++) {
            if (i >= 3)
                folderPath += folder[i] + "/";

        }
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

    public static byte[] getImage(String imageName, HttpServletRequest request) {
        String folderPath = "/";
        String folder[] = request.getHeader("REFERER").split("/");
        for (int i = 0; i < folder.length; i++) {
            if (i >= 3)
                folderPath += folder[i] + "/";

        }
        InputStream imageStream = null;
        byte[] imageByteArray = null;
        try {
            imageStream = new FileInputStream(STATIC_EXTERNAL_FILE_PATH + folderPath + imageName);
            imageByteArray = IOUtils.toByteArray(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                imageStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageByteArray;
    }

    public static void fileDownload(HttpServletRequest request, HttpServletResponse response,
                                    String fileName) {
        String folderPath = "/";
        String folder[] = request.getHeader("REFERER").split("/");
        for (int i = 0; i < folder.length; i++) {
            if (i >= 3)
                folderPath += folder[i] + "/";
        }
        File file = new File(STATIC_EXTERNAL_FILE_PATH + folderPath + fileName);
        if (file.exists()) {

            //get the mimetype
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            //response.setContentType("application/download; UTF-8");
            /**
             * In a regular HTTP response, the Content-Disposition response header is a
             * header indicating if the content is expected to be displayed inline in the
             * browser, that is, as a Web page or as part of a Web page, or as an
             * attachment, that is downloaded and saved locally.
             *
             */

            /**
             * Here we have mentioned it to show inline
             */
//            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            String fileNameOrg = file.getName();
            log.info("file.getName()={}", file.getName());
            try {
                fileNameOrg = new String(fileNameOrg.getBytes("UTF-8"), "ISO-8859-1");
                response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileNameOrg + "\""));

                response.setContentLength((int) file.length());

                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

                FileCopyUtils.copy(inputStream, response.getOutputStream());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
