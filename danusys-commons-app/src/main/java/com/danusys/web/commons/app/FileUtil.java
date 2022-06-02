package com.danusys.web.commons.app;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Component
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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

        File uploadPath = new File(sPath, folderPath);
        log.info("upload path : " + uploadPath);


        if (uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }

        for (MultipartFile multipartFile : uploadFile) {
            if(!multipartFile.isEmpty()){
                log.info("Upload File Name : " + multipartFile.getOriginalFilename());
                log.info("Upload File Size : " + multipartFile.getSize());

                uploadFileName = multipartFile.getOriginalFilename();
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
            }else{
                savedFileName = "";
            }
        }
        return savedFileName;
    }

    public static String uploadMulitAjaxPost(List<MultipartFile> uploadFile, HttpServletRequest request) {
        String folderPath = "";
        String folder[] = request.getHeader("REFERER").split("/");
        for (int i = 0; i < folder.length; i++) {
            if (i >= 3)
                folderPath += folder[i] + "/";

        }
        String sPath = STATIC_EXTERNAL_FILE_PATH;
        String uploadFileName = null;
        StringBuilder savedFileNames = new StringBuilder();
        String savedFileName = null;

        File uploadPath = new File(sPath, folderPath);
        log.info("upload path : " + uploadPath);


        if (uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }

        int idx = 0;
        for (MultipartFile multipartFile : uploadFile) {
            if(!multipartFile.isEmpty()){
                log.info("Upload File Name : " + multipartFile.getOriginalFilename());
                log.info("Upload File Size : " + multipartFile.getSize());

                uploadFileName = multipartFile.getOriginalFilename();
                uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);

                log.info("only file name: " + uploadFileName);

                for (String extension : staticExtensionList) {
                    if (uploadFileName.contains(extension))
                        return null;
                }
                if(uploadFile.size() -1 == idx) {
                    savedFileNames.append(setFileUUID()).append(uploadFileName);
                } else {
                    savedFileNames.append(setFileUUID()).append(uploadFileName).append(", ");
                }
                savedFileName = setFileUUID() + uploadFileName;
                File savefile = new File(uploadPath, savedFileName);

                try {
                    multipartFile.transferTo(savefile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                idx++;
            }else{
                savedFileNames = new StringBuilder();
            }
        }
        return savedFileNames.toString();
    }

    public static void deleteFile(String folderPath, String fileName) throws Exception {
        String filePath = STATIC_EXTERNAL_FILE_PATH + folderPath + fileName;
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }

    private static String setFileUUID() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        String str = sdf.format(date);
//        return str.replace("-","_");
        return str + "_";
    }

    public static void getImage2(String filePath, String imageName, HttpServletResponse response) throws IOException {
        File imgFile = null;
        OutputStream out = response.getOutputStream();
        FileInputStream fis = null;

        imgFile = new File(STATIC_EXTERNAL_FILE_PATH + filePath + imageName);
        try {
            fis = new FileInputStream(imgFile);
            FileCopyUtils.copy(fis, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            out.flush();
        }
    }

    public static File getVideoFile(String filePath, String videoName) throws IOException {
        return new File(STATIC_EXTERNAL_FILE_PATH + filePath + videoName);
    }

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

    public static void fileDownloadWithFilePath(HttpServletResponse response, String fileName, String folderPath) {

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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
     * excel donwload
     * 엑셀 다운로드
     *
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
     *                 필수 : dataMap ,
     *                 선택 : headerList
     *                 *
     * @throws IOException
     */


    public static Workbook excelDownload(Map<String, Object> paramMap) {
        List<Map<String, Object>> resultMap = new ArrayList<>();
        List<String> headerList = CommonUtil.valiArrNull(paramMap, "headerList");

        if (paramMap.get("dataMap") != null) {
            List<String> headerEn = new ArrayList<>();
            List<String> headerKo = new ArrayList<>();

            ObjectMapper objectMapper = new ObjectMapper();
            List<?> paramData = (List<?>) paramMap.get("dataMap");
            Set<String> notContKey = objectMapper.convertValue(paramData.get(0), Map.class).keySet();

            if (!headerList.isEmpty()){
                // 머리행 셋팅
                headerList.forEach(r -> {
                    String header[] = r.split("\\|");
                    headerKo.add(header[0]);
                    headerEn.add(header[1]);
                    notContKey.remove(header[1]);
                });
            }else{
                headerKo.addAll(notContKey);
            }

            paramData.forEach(m -> {
                Map<String, Object> map = objectMapper.convertValue(m, Map.class);
                notContKey.forEach(r -> {
                    map.remove(r);
                });
                resultMap.add(map);
            });


            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("sheet 1");
            AtomicInteger cellNum = new AtomicInteger();
            int rowNum = 1;

            for (Map<String, Object> data : resultMap) {
                //row 생성
                Row row = sheet.createRow(rowNum);
                Row headRow = sheet.createRow(0);
                cellNum.set(0);

                headerKo.forEach((s) -> {
                    Cell cell = headRow.createCell(cellNum.get());
                    if (s != null)
                        cell.setCellValue(s);
                    cellNum.incrementAndGet();
                });

                cellNum.set(0);
                if(headerList.isEmpty()){
                    data.forEach((k, valStr) -> {
                        Cell cell = row.createCell(cellNum.get());
                        cell.setCellValue(CommonUtil.validNull(valStr));
                        cellNum.incrementAndGet();
                    });
                }else{
                    headerEn.forEach(r -> {
                        String valStr = CommonUtil.validOneNull(data, r);
                        Cell cell = row.createCell(cellNum.get());
                        cell.setCellValue(valStr);
                        cellNum.incrementAndGet();
                    });
                }

                sheet.autoSizeColumn(rowNum);
                rowNum++;
            }
            return wb;
        }else {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("sheet 1");
            return wb;
        }
    }
}
