package com.danusys.web.platform.service.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserService {
    public List<HashMap<String, Object>> getListUser(Map<String, Object> paramMap) throws Exception;
}
