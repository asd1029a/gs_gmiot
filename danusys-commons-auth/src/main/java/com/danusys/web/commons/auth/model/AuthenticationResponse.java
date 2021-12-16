package com.danusys.web.commons.auth.model;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    //private final TokenDto jwt;

        private final String accessToken;

        public AuthenticationResponse(String accessToken){
            this.accessToken=accessToken;

        }

        public String getAccessToken(){
            return accessToken;
        }

//    public AuthenticationResponse(TokenDto jwt)  {
//        this.jwt = jwt;
//    }
//
//    public TokenDto getJwt() {
//        return jwt;
//    }
}
