package com.danusys.web.commons.app;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.org.apache.bcel.internal.util.ClassPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.servlet.ModelAndView;

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


    /**
     * 파일 업로드
     * @param uploadFile
     * @param request
     * @return 저장된 파일 이름
     *
     * ajax를 요청한 펭지ㅣ url 기준으로 폴더를 만들어 파일 업로드
     * 서버에 home 폴더를 가져와 그안에 파일을 저장
     * ex) /drone에서 요청시
     * c:/user/owner/drone 안에 파일이 저장됨
     *
     */


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

            savedFileName = setFileUUID() + uploadFileName;
            File savefile = new File(uploadPath, savedFileName);

            try {
                multipartFile.transferTo(savefile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return savedFileName;
    }

    /**
     * 파일이름에 uuid 붙이기
     * @return
     */
    private static String setFileUUID() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String str = sdf.format(date);
//        return str.replace("-","_");
        return str + "_";
    }

    /**
     * byte로 이미지 리턴
     * @param imageName
     * @param request
     * @return 이미지 byte[]
     */
    public static byte[] getImage2(String imageName, HttpServletRequest request) {
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

    /**
     * 이미지 바로 출력
     * @param imageName 출력할 이미지이름
     * @param request
     * @param response
     *
     */
    public static void getImage(String imageName, HttpServletRequest request, HttpServletResponse response) {
        String folderPath = "/";
        String folder[] = request.getHeader("REFERER").split("/");

        for (int i = 0; i < folder.length; i++) {
            if (i >= 3)
                folderPath += folder[i] + "/";
        }
        String imagePath = STATIC_EXTERNAL_FILE_PATH + folderPath + imageName;

        File file = new File(imagePath);
        FileInputStream fis = null;

        BufferedInputStream in = null;
        ByteArrayOutputStream bStream = null;

        try {
            fis = new FileInputStream(file);
            in = new BufferedInputStream(fis);
            bStream = new ByteArrayOutputStream();
            int imgByte;
            while ((imgByte = in.read()) != -1) {
                bStream.write(imgByte);
            }

            String type = "";

            int pos = imagePath.lastIndexOf(".");
            String ext = imagePath.substring(pos + 1);

            type = "image/" + ext.toLowerCase();

            System.out.println(type);

            response.setHeader("Content-Type", type);
            response.setContentLength(bStream.size());
            bStream.writeTo(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (bStream != null) {
                try {
                    bStream.close();
                } catch (Exception est) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ei) {

                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception efis) {

                }
            }
        }


    }


    /**
     *  ajax를 요청한 url 기준으로 fileName 다운로드
     *  /drone에서 ajax 요청시
     *  /c:user/owner/drone 에 있는 파일을 가져온다.
     * @param request
     * @param response
     * @param fileName
     */
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

    /**
     * @param paramMap
     *
     *    ex)
     *    paramMap = {
     *    dataMap: resultData,  <- 조회한 결과
     *    fileName: "Log.xlsx",
     *    headerList: ["아이디", "드론이름", "미션이름", "입력날짜"]
     * };
     *
     *             dataMap-> List<Map<String,Object>> dataMap
     *             headerList -> List<String> heartList
     *             dataMap -> 엑셀에 담을 data map 리스트
     *             headerList -> 엑셀 첫줄에 해더 부분을 임의로 지정할 경우
     *
     *             필수 : dataMap ,
     *             선택 : headerList
     *
     * @return Workbook 리턴
     */


    public static Workbook excelDownload(
                                         Map<String, Object> paramMap) {

        List<Map<String, Object>> dataMap = null;
        List<String> headerList = null;
        if (paramMap.get("dataMap") != null) {
            dataMap = (List<Map<String, Object>>) paramMap.get("dataMap");
        }
        if (paramMap.get("headerList") != null) {
            headerList = (List<String>) paramMap.get("headerList");
        }


        int rowNum = 0;
        AtomicInteger cellNum = new AtomicInteger();
        //log.info("paramMap={}", paramMap);

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("sheet 1");

        rowNum = 1;

        for (Map<String, Object> data : dataMap) {
            //row 생성
            Integer finalRowNum = rowNum;
            Row row = sheet.createRow(finalRowNum);
            Row headRow = sheet.createRow(0);

            cellNum.set(0);
            if (headerList == null) {
                data.forEach((k, v) -> {

                    Cell cell = headRow.createCell(cellNum.get());
                    if (k != null)
                        cell.setCellValue(k);

                    cellNum.incrementAndGet();
                });
            } else {
                headerList.forEach((s) -> {
                    Cell cell = headRow.createCell(cellNum.get());
                    if (s != null)
                        cell.setCellValue(s);

                    cellNum.incrementAndGet();
                });
            }

            cellNum.set(0);
            data.forEach((k, v) -> {


                Cell cell = row.createCell(cellNum.get());
                if (v != null)
                    cell.setCellValue(v.toString());

                //cell에 데이터 삽입
                cellNum.incrementAndGet();
            });

            sheet.autoSizeColumn(finalRowNum);
            //  sheet.setColumnWidth(finalRowNum, (sheet.getColumnWidth(finalRowNum))+100 );
            rowNum++;

        }


        // Excel File Output
        //  response.setHeader("Content-Disposition", "attachment;filename=testExcel1.xlsx");
        //    response.setHeader("Content-Disposition",  String.format("attachment; filename=fileName;charset=utf-8"));

        //    response.setHeader("Content-Disposition",  String.format("attachment; filename=fileName;charset=utf-8"));

        return wb;


    }


}
