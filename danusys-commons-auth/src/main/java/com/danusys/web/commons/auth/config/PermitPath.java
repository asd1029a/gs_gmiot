package com.danusys.web.commons.auth.config;

import java.util.ArrayList;
import java.util.Arrays;

public class PermitPath {


    public static ArrayList<String> PERMIT_PATH = new ArrayList<String>(Arrays.asList(
            "/js/**","/css/**","/webjars/**","/login/error","/font/**",
            "/images/**","/mntr/**","/resources/**","/api/**","/aepel/**",
            "/favicon.ico","/selectNoSession/**","/file/**","/sound/**",
            "/svg/**","/ui/**","/swagger-ui/**","/","/drone"));

    public static ArrayList<String> PERMIT_ADD = new ArrayList<String>(Arrays.asList("/auth/generateToken","/loginpage","/loginpage2","/auth/**","/permitallpage","/login/errorTest","/loginpagetest"));

    public static ArrayList<String> PERMIT_MANAGER = new ArrayList<String>(Arrays.asList("/test/**","/permitmanagerpage"));

    public static ArrayList<String> PERMIT_ADMIN = new ArrayList<String>(Arrays.asList("/tokenTest","/auth/regenerateToken","/test2","/permitadminpage"));



}
