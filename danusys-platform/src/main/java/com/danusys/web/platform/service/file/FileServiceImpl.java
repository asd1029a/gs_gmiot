package com.danusys.web.platform.service.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class FileServiceImpl extends AbstractView implements FileService
{
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        logger.info("FileServiceImpl renderMergedOutputModel()");
        
        File file = (File)model.get("downloadFile");

        response.setContentType("application/download; utf-8");
        //response.setContentLength((int)file.length());
        
        String userAgent = request.getHeader("User-Agent");
        String fileName = file.getName().substring(file.getName().indexOf("_", file.getName().indexOf("_") + 1) + 1, file.getName().length());
        
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) 
        {
        	fileName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";");
        } 
        else
        {
        	fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        }       
        
        if(file.length() <= Integer.MAX_VALUE)
        {
        	response.setContentLength((int)file.length());
        }
        else
        {
        	response.setHeader("Content-Length", Long.toString(file.length()));
        }
        
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Transfer-Encoding", "binary;");
        response.setHeader("Pragma", "no-cache;");
        response.setHeader("Expires", "-1;");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        
        OutputStream out = response.getOutputStream();

        FileInputStream fis = null;

        try
        {
            fis = new FileInputStream(file);

            FileCopyUtils.copy(fis, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (Exception e) {}
            }
            file.delete();
        }      
        out.flush();
    }
    
    
    
   
    @Override
    @Transactional
    public List<Map<String, Object>> setFileUploadCreate(List<MultipartFile> fileList, String sPath, Map<String, Object> param) throws Exception
    {
        boolean isResult = true;

        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd");
        sPath = sPath + sDate.format(new Date()) + "/";

        File filePath = new File(sPath); // 파일경로생성

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // 디렉토리생성
        }

        String sFile = "";
        String sName = "";
        int   index = 1;

        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();

        try
        {
            for (MultipartFile multipartFile : fileList)
            {
                if (multipartFile.getOriginalFilename().equals("") == true) continue;

                long time = System.currentTimeMillis();
                SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

                sName = dateTime.format(new Date(time)) + index + multipartFile.getOriginalFilename(); // 저장파일명
                sFile = sPath + sName;

                System.out.println("파일경로 = [" + sFile + "]");

                File file = new File(sFile); // 파일생성

                multipartFile.transferTo(file); // 파일전송

                param.put("fileNameOrg",    multipartFile.getOriginalFilename());   // 원본 파일명
                param.put("fileNameSave",   sName);         // 저장 파일명
                param.put("filePath",       sPath);         // 파일 경로
                param.put("fileSize",       multipartFile.getSize());   // 파일 사이즈
                param.put("downloadCount",  "0");       // 다운로드 수

                resList.add((index - 1), param);

                index++;
            }
        }
        catch (Exception e)
        {
            logger.error(e.toString());

            isResult = false;
        }

        return resList;
    }

    @Override
    @Transactional
    public Map<String, Object> setFileUploadCreate2(MultipartFile mFile, String sPath, Map<String, Object> param) throws IllegalStateException, IOException
    {
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd");
        sPath = sPath + sDate.format(new Date()) + "/";

        File filePath = new File(sPath); // 파일경로생성

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // 디렉토리생성
        }
        
        long time = System.currentTimeMillis();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        String sName = dateTime.format(new Date(time)) + mFile.getOriginalFilename(); // 저장파일명
        String sFile = sPath + sName;

        System.out.println("파일경로 = [" + sFile + "]");

        File file = new File(sFile); // 파일생성

        mFile.transferTo(file); // 파일전송

        param.put("fileNameOrg",    mFile.getOriginalFilename());   // 원본 파일명
        param.put("fileNameSave",   sName);         // 저장 파일명
        param.put("filePath",       sPath);         // 파일 경로
        param.put("fileSize",       mFile.getSize());   // 파일 사이즈
        param.put("downloadCount",  "0");       // 다운로드 수

        Map<String, Object> resData = new HashMap<String, Object>();
        
        return resData;
    }
    
    @Override
    @Transactional
    public File setFileUploadCreate3(MultipartFile mFile, String sPath) throws IllegalStateException, IOException
    {
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd");
        sPath = sPath + sDate.format(new Date()) + "/";

        File filePath = new File(sPath); // 파일경로생성

		System.out.println(filePath);

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // 디렉토리생성
        }
        
        long time = System.currentTimeMillis();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        String sName = dateTime.format(new Date(time)) + mFile.getOriginalFilename(); // 저장파일명
        String sFile = sPath + sName;

        System.out.println("파일경로 = [" + sFile + "]");

        File file = new File(sFile); // 파일생성

        mFile.transferTo(file); // 파일전송

        return file;
    }
    
    @Override
    @Transactional
    public File setFileUploadCreate4(MultipartFile mFile, String root) throws IllegalStateException, IOException
    {
        File filePath = new File(root); // 파일경로생성

		System.out.println(filePath);

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // 디렉토리생성
        }

        String sName = mFile.getOriginalFilename(); // 저장파일명
        String sFile = root + sName;

        System.out.println("파일경로 = [" + sFile + "]");

        File file = new File(sFile); // 파일생성

        mFile.transferTo(file); // 파일전송

        return file;
    }
    
    @Override
    @Transactional
    public File setFileUploadCreate4(MultipartFile mFile, String sPath, String fileName) throws IllegalStateException, IOException
    {
        File filePath = new File(sPath); // 파일경로생성

		System.out.println(filePath);

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // 디렉토리생성
        }
        
        String sFile = sPath + fileName;

        System.out.println("파일경로 = [" + sFile + "]");

        File file = new File(sFile); // 파일생성

        mFile.transferTo(file); // 파일전송

        return file;
    }
}
