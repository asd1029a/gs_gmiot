package com.danusys.web.guardian.service.cctv;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;


public interface CctvService {
	void insertAllcenterSch(String xmlData) throws IOException, SQLException;
	Map<String,Object> makeCode(Map<String,Object> param) throws SQLException, IOException;
	
}
