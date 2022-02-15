package com.danusys.web.drone.controller;

import com.danusys.web.commons.util.CommonUtil;
import com.danusys.web.commons.util.JsonUtil;
import com.danusys.web.drone.service.FileService;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
public class FileController {

    private FileService fileService;

    @Value("${danusys.path.root}")
    private String pathRoot = "";

    @Value("${danusys.file.upload.path}")
    private String uploadPath = "";


    @Value("#{'${danusys.file.extension}'.split(',')}")
    private String[] extensionList;

    private static final String EXTERNAL_FILE_PATH = "D:/test/";

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 함수명   : fileDownLoad()
     * FuncDesc : 파일 다운로드
     */
    @RequestMapping(value = "/file/download", method = RequestMethod.POST)
    public void fileDownLoad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("FileController fileDownLoad()");

        Map<String, Object> param = null;

//		Properties prop = new Properties();
//		InputStream is = getClass().getResourceAsStream("/egovframework/egovProps/file.properties");
//		prop.load(is);
//		final String sPath = prop.get("file.upload.path").toString();


        if (request.getParameter("param").trim().equals("") == true) {
            param = new HashMap<String, Object>();
        } else {
            param = JsonUtil.JsonToMap(request.getParameter("param"));
        }

        String fileName = param.get("fileName").toString();
        fileName = CommonUtil.getReXSSFilter(fileName);
        boolean isDelete = Boolean.valueOf(param.get("isDelete").toString());
        File file = new File(this.uploadPath + fileName);

        FileInputStream fileInputStream = null;
        ServletOutputStream servletOutputStream = null;

        response.setContentType("application/download; utf-8");

        String[] matches = new String[]{"자산현황", "조치집계함"};

        boolean isMatch = false;
        for (String s : matches) {
            if (fileName.toLowerCase().contains(s)) {
                isMatch = true;
                break;
            }
        }

        if (isMatch) {
            fileName = fileName.substring(19, fileName.length());
        }

        if (fileName.length() > 43) {
            fileName = fileName.substring(62, fileName.length());
        }

        if (fileName.contains("/Request/")) {
            fileName = fileName.substring(9, fileName.length());
        }

        fileName = URLEncoder.encode(fileName, "utf-8");
        fileName = fileName.replace("+", " ");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");

        OutputStream out = response.getOutputStream();

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);

            FileCopyUtils.copy(fis, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    if (isDelete) {
                        file.delete();  //파일 삭제 추가됨
                    }
//                	if(!file.getName().toLowerCase().contains("/Request/".toLowerCase()))
//					{
//
//					}
                    //file.delete();  //파일 삭제 추가됨
                    fis.close();
                } catch (Exception e) {
                }
            }

        }
        out.flush();
    }

    @RequestMapping("/file/getImage.do")
    public void getImage(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String imagePath = request.getParameter("imageUrl");

        log.debug("getImage path : {}", imagePath);

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

    @GetMapping("/file/getImage2")
    public void getImage2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("#   pathRoot : {}", pathRoot);
        log.trace("# uploadPath : {}", uploadPath);

        final String imageUrl = request.getParameter("imageUrl");
        final String sPath = request.getParameter("sPath");
        final String path = pathRoot + uploadPath + "/" + sPath + "/" + imageUrl;

        log.debug("getImage path : {}", path);

        File file = new File(path);
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

            int pos = path.lastIndexOf(".");
            String ext = path.substring(pos + 1);

            type = "image/" + ext.toLowerCase();

            log.debug("# image type : {}", type);

            ServletOutputStream out = response.getOutputStream();

            response.setHeader("Content-Type", type);
            response.setContentLength(bStream.size());
            bStream.writeTo(out);
            out.flush();
            out.close();

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

    @RequestMapping(value = {"/displayFile.do"})
    public ResponseEntity<byte[]> displayFile(@RequestParam("name") String fileName) throws Exception {

        InputStream in = null;
        ResponseEntity<byte[]> entity = null;

        log.info("FILE NAME : " + fileName);

        try {
            String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
            MediaType mType = MediaType.IMAGE_JPEG;

            HttpHeaders headers = new HttpHeaders();

            in = new FileInputStream("C://smart_platform_stilcut//" + fileName);

            if (mType != null) {
                headers.setContentType(mType);
            } else {
                fileName = fileName.substring(fileName.indexOf("_") + 1);
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add("Content-Disposition", "attatchment; filename=\"" +
                        new String(fileName.getBytes("UTF-8"), "ISO-8859-1") +
                        "\"");
            }

            entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        } finally {
            in.close();
        }

        return entity;
    }

    /*@RequestMapping(value="/file/getImage3", method=RequestMethod.GET)
    public ResponseEntity<byte[]> displayFile(@RequestParam("name") String fileName) {
    	InputStream in = null;
    	ResponseEntity<byte[]> entity = null;

    	try {
    		String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
    		MediaType mType = MediaType.IMAGE_JPEG;
    		HttpHeaders headers = new HttpHeaders();
    		in = new FileInputStream(fileName);

    		fileName = fileName.substring(fileName.indexOf("_") + 1);
    		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    		headers.add("Content-Disposition", "attachment; filename=\"" + );
    	} catch(Exception e) {
    		e.printStackTrace();
    		entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
    	} finally {
    		in.close();
    	}
    	return null;
    }*/

    @RequestMapping("/getImageLocal.do")
    public void getImageLocal(@RequestParam Map<String, Object> request, HttpServletResponse response) throws Exception {
        String filePath = (String) request.get("filePath");

        File imgFile = new File(filePath);
        //File imgFile = new File("C:\\btn_Register.png");
        FileInputStream ifo = new FileInputStream(imgFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int readlength = 0;

        while ((readlength = ifo.read(buf)) != -1) {
            baos.write(buf, 0, readlength);
        }
        byte[] imgbuf = null;
        imgbuf = baos.toByteArray();
        baos.close();
        ifo.close();

        int length = imgbuf.length;
        OutputStream out = response.getOutputStream();
        out.write(imgbuf, 0, length);
        out.close();
    }


    /**
     * 함수명   : fileUpLoad()
     * FuncDesc : 파일 업로드
     * Param    :
     * Return   :
     * Author   :
     * History  :
     */
    @RequestMapping(value = "/file/upload.do", method = RequestMethod.POST)
    public void fileUpLoad(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) throws IOException {
        log.info("FileController fileUpLoad()");

        PrintWriter out = null;

        response.setCharacterEncoding("UTF-8");

        Map<String, Object> param = null;

        if (request.getParameter("fileParam").trim().equals("") == true) {
            param = new HashMap<String, Object>();
        } else {
            param = JsonUtil.JsonToMap(request.getParameter("fileParam"));
        }

        List<Map<String, Object>> resList = null;

        try {
            // 파일 리스트
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

            List<MultipartFile> fileList = multipartRequest.getFiles("upFile");
            System.out.println("File Count = " + fileList.size());

            // 프로퍼티에서 물리적 파일 저장소 경로 Read
            Properties prop = new Properties();
            InputStream is = getClass().getResourceAsStream("/egovframework/egovProps/file.properties");
            prop.load(is);

            final String sPath = prop.get("file.path").toString();

            // 파일 업로드
            resList = fileService.setFileUploadCreate(fileList, sPath, param);

            JSONArray jsonList = new JSONArray(JsonUtil.ListToJson(resList));

            model.addAttribute("jsonList", jsonList); // Retun Json String
            model.addAttribute("resList", resList);

            out = response.getWriter();

            out.write(JsonUtil.ListToJson(resList)); // Ajax Retun Json String

        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 함수명   : fileUpLoad2()
     * FuncDesc : 파일 업로드
     * Param    :
     * Return   :
     * Author   :
     * History  :
     */
    @RequestMapping(value = "/file/upload2.do", method = RequestMethod.POST)
    public void fileUpLoad2(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) throws IOException {
        System.out.println("====================");

        PrintWriter out = null;

        Map<String, Object> param = new HashMap<String, Object>();

        Map<String, Object> resData = new HashMap<String, Object>();

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        MultipartFile file = multipartRequest.getFile("upFile");
        System.out.println(file.getOriginalFilename());

        // 프로퍼티에서 물리적 파일 저장소 경로 Read
        Properties prop = new Properties();
        InputStream is = getClass().getResourceAsStream("/conf/properties/file.properties");
        prop.load(is);

        final String sPath = prop.get("file.upload.path").toString();

        try {
            resData = fileService.setFileUploadCreate2(file, sPath, param);
        } catch (IllegalStateException e) {
            log.error(e.toString());
            e.printStackTrace();
            resData.put("resultCode", "2");
            resData.put("resultMsg", "IllegalStateException");
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
            resData.put("resultCode", "3");
            resData.put("resultMsg", "IOException");
        }

        out = response.getWriter();

        out.write(JsonUtil.MapToJson(resData)); // Ajax Retun Json String
        System.out.println(JsonUtil.MapToJson(resData));
    }

    /**
     * 함수명   : fileUpLoad3()
     * FuncDesc : 파일 업로드
     * Param    :
     * Return   :
     * Author   :
     * History  :
     */
    @RequestMapping(value = "/file/upload3.do", method = RequestMethod.POST)
    public void fileUpLoad3(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) throws IOException {
        System.out.println("====================");

        PrintWriter out = null;

        Map<String, Object> resData = new HashMap<String, Object>();

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        MultipartFile file = multipartRequest.getFile("upFile");
        System.out.println(file.getOriginalFilename());

        // 프로퍼티에서 물리적 파일 저장소 경로 Read
        Properties prop = new Properties();
        InputStream is = getClass().getResourceAsStream("/egovframework/egovProps/file.properties");
        prop.load(is);

        final String sPath = prop.get("file.upload.path").toString();

        try {
            System.out.println(file);
            System.out.println(sPath);
            resData.put("resultCode", "1");
            resData.put("resultFile", fileService.setFileUploadCreate3(file, sPath));
        } catch (IllegalStateException e) {
            log.error(e.toString());
            e.printStackTrace();
            resData.put("resultCode", "2");
            resData.put("resultMsg", "IllegalStateException");
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
            resData.put("resultCode", "3");
            resData.put("resultMsg", "IOException");
        }

        out = response.getWriter();

        out.write(JsonUtil.MapToJson(resData)); // Ajax Retun Json String
        System.out.println(JsonUtil.MapToJson(resData));

    }

    /**
     * 함수명 : fileUpLoad4() FuncDesc : 파일 업로드 Param : Return : Author : History :
     */
    @RequestMapping(value = "/file/upload4", method = RequestMethod.POST)
    public void fileUpLoad4(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model)
            throws IOException {
        PrintWriter out = null;

        Map<String, Object> resData = new HashMap<String, Object>();

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

//        Properties prop = new Properties();
//        InputStream is = getClass().getResourceAsStream("/egovframework/egovProps/file.properties");
//        prop.load(is);
//        final String uploadPath = prop.get("file.upload.path").toString();

        final String sPath = request.getParameter("sPath");
        // 파일 업드로 경로
        final String root = this.pathRoot + "/" + this.uploadPath + "/" + sPath + "/";

        MultipartFile file = multipartRequest.getFile("upFile");

        log.info("upload path : {}, fileName : {}", root, file.getOriginalFilename());

        try {
            File uploadFile = fileService.setFileUploadCreate4(file, root);
            resData.put("resultCode", "1");
            resData.put("resultFile", uploadFile.getName());
        } catch (IllegalStateException e) {
            log.error(e.toString());
            e.printStackTrace();
            resData.put("resultCode", "2");
            resData.put("resultMsg", "IllegalStateException");
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
            resData.put("resultCode", "3");
            resData.put("resultMsg", "IOException");
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        out = response.getWriter();

        out.write(JsonUtil.MapToJson(resData)); // Ajax Retun Json String
        System.out.println(JsonUtil.MapToJson(resData));

    }


    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/file/download4.do")
    public void download4(String sourceUrl, String targetFilename, HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream is = getClass().getResourceAsStream("/egovframework/egovProps/file.properties");
        ;
        Properties prop = new Properties();
        prop.load(is);

        final String uploadPath = prop.get("file.upload.path").toString();
        final String sPath = request.getParameter("sPath");
        String fileName = request.getParameter("fileName");

        // 파일 업드로 경로
        final String root = System.getProperty("user.home") + "/" + uploadPath + "/" + sPath + "/" + fileName;

        File file = new File(root);

        String encodedFilename = "";
        response.setContentType("application/download; UTF-8");
        response.setContentLength((int) file.length());

        String header = request.getHeader("User-Agent");

        if (header.indexOf("MSIE") > -1) {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (header.indexOf("Trident") > -1) {
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (header.indexOf("Chrome") > -1) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else if (header.indexOf("Opera") > -1) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (header.indexOf("Safari") > -1) {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
            encodedFilename = URLDecoder.decode(encodedFilename);
        } else {
            encodedFilename = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
            encodedFilename = URLDecoder.decode(encodedFilename);
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");

        OutputStream out = response.getOutputStream();

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            FileCopyUtils.copy(fis, out);
        } catch (FileNotFoundException e) {
        } finally {
            IOUtils.closeQuietly(fis);
        }
        out.flush();
    }

    /**
     * 함수명   : canvasImageUpLoad()
     * FuncDesc : 파일 업로드
     * Param    :
     * Return   :
     * Author   :
     * History  :
     */
    @RequestMapping(value = "/file/canvasImageUpLoad.do", method = RequestMethod.POST)
    public void canvasImageUpLoad(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) throws IOException {
        System.out.println("====================");

        PrintWriter out = null;

        Map<String, Object> resData = new HashMap<String, Object>();

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        MultipartFile file = multipartRequest.getFile("upFile");
        System.out.println(file.getOriginalFilename());

        // 프로퍼티에서 물리적 파일 저장소 경로 Read
        Properties prop = new Properties();
        InputStream is = getClass().getResourceAsStream("/egovframework/egovProps/file.properties");
        prop.load(is);

        final String sPath = request.getParameter("path");
        final String fileName = request.getParameter("fileName");

        try {
            System.out.println(file);
            System.out.println(sPath);
            resData.put("resultCode", "1");
            resData.put("resultFile", fileService.setFileUploadCreate4(file, sPath, fileName));
        } catch (IllegalStateException e) {
            log.error(e.toString());
            e.printStackTrace();
            resData.put("resultCode", "2");
            resData.put("resultMsg", "IllegalStateException");
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
            resData.put("resultCode", "3");
            resData.put("resultMsg", "IOException");
        }

        out = response.getWriter();

        out.write(JsonUtil.MapToJson(resData)); // Ajax Retun Json String
        System.out.println(JsonUtil.MapToJson(resData));

    }

    /**
     * 함수명   : sendSftp()
     * FuncDesc : 파일 Sftp 전송
     * Param    :
     * Return   :
     * Author   :
     * History  :
     */
//    @RequestMapping(value="/file/sendSftp.do", method=RequestMethod.POST)
//    public void sendSftp(HttpServletRequest request, HttpServletResponse response) {
//    	String fileName = request.getParameter("fileName");
//    	String path = request.getParameter("path");
//        System.out.println("====================aaaaaaaaaaaaaaaaaaaaa");
//
//        PrintWriter out = null;
//
//    	boolean flag = SftpUtils.sendFile(fileName, path);
//
//    	if (flag) {
//    		System.out.println("전송 성공!");
//    	} else {
//    		System.out.println("전송 실패.....");
//    	}
//
//    	try {
//            out = response.getWriter();
//    	} catch(IOException e) {
//    		e.printStackTrace();
//    	}
//
//        out.write(String.valueOf(flag));
//    }
    @ResponseBody
    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadAjaxPost(MultipartFile[] uploadFile, String sPath, String folderPath) {

        log.info("sPath={},folderPath={}",sPath,folderPath);
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
            String uploadFileName = multipartFile.getOriginalFilename();

            uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);
            log.info("only file name: " + uploadFileName);


            for (String extension : extensionList) {
                if (uploadFileName.contains(extension))
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("extension Error");
            }


            File savefile = new File(uploadPath, uploadFileName);

            try {
                multipartFile.transferTo(savefile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(filePath);
    }

    @ResponseBody
    @RequestMapping("/file/{fileName:.+}")
    public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("fileName") String fileName) throws IOException {

        File file = new File(EXTERNAL_FILE_PATH + fileName);
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
            fileNameOrg = new String(fileNameOrg.getBytes("UTF-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileNameOrg + "\""));

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());

        }
    }

    //    private String getFolder(){
//        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-mm-dd");
//        Date date= new Date();
//        String str =sdf.format(date);
//        return str.replace("-",File.separator);
//    }


    @GetMapping(value = "/image/{imagename:.+}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> userSearch(@PathVariable("imagename") String imagename) throws IOException {
        InputStream imageStream = new FileInputStream(EXTERNAL_FILE_PATH + imagename);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();
        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
    }
}
