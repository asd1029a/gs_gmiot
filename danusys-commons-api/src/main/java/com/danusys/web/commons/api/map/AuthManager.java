package com.danusys.web.commons.api.map;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthManager {


    /**
     * ex) authMap -> <"LG_DRONE",Session>  ->
     * 초안 : 로그인 api 실행후 메인 api 실행
     * 문제점 : 로그인을 너무 자주해서 lock ?
     * 사용법 : 로그인 api 실행시 authMap에 집어놓고
     * api 테이블에 /LG/DRONE 으로 시작하는 api 들은 Session 맵에서 불러와서 쿠키에 값을 넣어준다.
     * 장점 : db를 수정하지 않아도 된다. 여기 저기서 api안에 있는 데이터 수정 안해도 됨
     */
    private Map<String, Object> authMap = new HashMap<>();

    public Map<String, Object> getAuthMap() {
        return this.authMap;
    }

    public void setAuthMap(Map<String, Object> authMap) {
        this.authMap = authMap;
    }

}
