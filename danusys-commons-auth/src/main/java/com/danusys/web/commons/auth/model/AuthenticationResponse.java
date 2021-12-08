package com.danusys.web.commons.auth.model;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    private final TokenDto jwt;

    public AuthenticationResponse(TokenDto jwt)  {
        this.jwt = jwt;
    }

    public TokenDto getJwt() {
        return jwt;
    }
}
