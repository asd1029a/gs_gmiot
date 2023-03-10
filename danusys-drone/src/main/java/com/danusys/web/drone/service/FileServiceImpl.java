package com.danusys.web.drone.service;

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

        File filePath = new File(sPath); // ??????????????????

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // ??????????????????
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

                sName = dateTime.format(new Date(time)) + index + multipartFile.getOriginalFilename(); // ???????????????
                sFile = sPath + sName;

                System.out.println("???????????? = [" + sFile + "]");

                File file = new File(sFile); // ????????????

                multipartFile.transferTo(file); // ????????????

                param.put("fileNameOrg",    multipartFile.getOriginalFilename());   // ?????? ?????????
                param.put("fileNameSave",   sName);         // ?????? ?????????
                param.put("filePath",       sPath);         // ?????? ??????
                param.put("fileSize",       multipartFile.getSize());   // ?????? ?????????
                param.put("downloadCount",  "0");       // ???????????? ???

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

        File filePath = new File(sPath); // ??????????????????

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // ??????????????????
        }
        
        long time = System.currentTimeMillis();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        String sName = dateTime.format(new Date(time)) + mFile.getOriginalFilename(); // ???????????????
        String sFile = sPath + sName;

        System.out.println("???????????? = [" + sFile + "]");

        File file = new File(sFile); // ????????????

        mFile.transferTo(file); // ????????????

        param.put("fileNameOrg",    mFile.getOriginalFilename());   // ?????? ?????????
        param.put("fileNameSave",   sName);         // ?????? ?????????
        param.put("filePath",       sPath);         // ?????? ??????
        param.put("fileSize",       mFile.getSize());   // ?????? ?????????
        param.put("downloadCount",  "0");       // ???????????? ???

        Map<String, Object> resData = new HashMap<String, Object>();
        
        return resData;
    }
    
    @Override
    @Transactional
    public File setFileUploadCreate3(MultipartFile mFile, String sPath) throws IllegalStateException, IOException
    {
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd");
        sPath = sPath + sDate.format(new Date()) + "/";

        File filePath = new File(sPath); // ??????????????????

		System.out.println(filePath);

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // ??????????????????
        }
        
        long time = System.currentTimeMillis();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        String sName = dateTime.format(new Date(time)) + mFile.getOriginalFilename(); // ???????????????
        String sFile = sPath + sName;

        System.out.println("???????????? = [" + sFile + "]");

        File file = new File(sFile); // ????????????

        mFile.transferTo(file); // ????????????

        return file;
    }
    
    @Override
    @Transactional
    public File setFileUploadCreate4(MultipartFile mFile, String root) throws IllegalStateException, IOException
    {
        File filePath = new File(root); // ??????????????????

		System.out.println(filePath);

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // ??????????????????
        }

        String sName = mFile.getOriginalFilename(); // ???????????????
        String sFile = root + sName;

        System.out.println("???????????? = [" + sFile + "]");

        File file = new File(sFile); // ????????????

        mFile.transferTo(file); // ????????????

        return file;
    }
    
    @Override
    @Transactional
    public File setFileUploadCreate4(MultipartFile mFile, String sPath, String fileName) throws IllegalStateException, IOException
    {
        File filePath = new File(sPath); // ??????????????????

		System.out.println(filePath);

        if (filePath.exists() == false)
        {
            filePath.mkdirs(); // ??????????????????
        }
        
        String sFile = sPath + fileName;

        System.out.println("???????????? = [" + sFile + "]");

        File file = new File(sFile); // ????????????

        mFile.transferTo(file); // ????????????

        return file;
    }
}
