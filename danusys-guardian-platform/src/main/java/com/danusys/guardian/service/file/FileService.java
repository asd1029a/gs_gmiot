package com.danusys.guardian.service.file;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FileService
{
    // 파일 다운로드
    public void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
    
	// 파일 업로드 파일 생성
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<Map<String, Object>> setFileUploadCreate(List<MultipartFile> fileList, String sPath, Map<String, Object> param) throws Exception;
	
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Map<String, Object> setFileUploadCreate2(MultipartFile file, String sPath, Map<String, Object> param) throws IllegalStateException, IOException;
	
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public File setFileUploadCreate3(MultipartFile file, String sPath) throws IllegalStateException, IOException;

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public File setFileUploadCreate4(MultipartFile file, String root) throws IllegalStateException, IOException;
	
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public File setFileUploadCreate4(MultipartFile file, String sPath, String fileName) throws IllegalStateException, IOException;
}
