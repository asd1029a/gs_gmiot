package com.danusys.web.platform.service.user;

import com.danusys.web.platform.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<HashMap<String, Object>> getListUser(Map<String, Object> paramMap) throws Exception {
        return userMapper.findAll();
    }

}
