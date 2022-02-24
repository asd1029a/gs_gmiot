package com.danusys.web.commons.app;

import java.net.URLDecoder;

//import static org.junit.jupiter.api.Assertions.*;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : kai
 * Date : 2022/01/12
 * Time : 13:00
 */
public class CrytoJSUtilTest {
    public static void main(String[] args) {


        String key = "baf4a499-9053-4958-b25e-b9149152afa9";
        String enc = "U2FsdGVkX1%2Bo9RK05%2B5c%2B6cy8xN8GCLNN0%2Bjyt%2F2aVE%3D";

        String dec = CrytoJSUtil.decrypt(URLDecoder.decode(enc), key);
        System.out.println(dec);
    }
}