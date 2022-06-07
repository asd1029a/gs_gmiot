package com.danusys.web.commons.api.util;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * 서버 및 장비 ping ckecked
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/06/03
 * Time : 10:51 AM
 */
public class IpCheckedUtil {

    public static void main(String[] args) {
        List<Map<String, Object>> ips = new ArrayList<>();
        Map<String, Object> map = null;

        for(int i=0; i<3; i++) {
            map = new HashMap<>();
            map.put("ip", "127.0.0.1");
            ips.add(map);
        }

        List<Map<String, Object>> result = ipCheckedList(ips);

        System.out.println(result);

    }

    private static int PING_CHECKED_TIME_OUT = 1000;

    /**
     * 기본 타임아웃 설정 3초
     * @param ip
     * @return
     */
    public static boolean ipChecked(String ip) {
        return ipChecked(ip, PING_CHECKED_TIME_OUT);
    }

    /**
     * ping checked
     * @param ip
     * @param timeout
     * @return
     */
    public static boolean ipChecked(String ip, int timeout) {
        InetAddress pingChecked = null;
        boolean isAlive = false;
        try {
            pingChecked = InetAddress.getByName(ip);
            isAlive = pingChecked.isReachable(timeout); //타임아웃 설정
            System.out.println(ip  + " > " + isAlive);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isAlive;
    }

    /**
     * ip목록을 받아 ping check 후 결과를 목록으로 리턴
     * @param ipLists
     * @return
     */
    public static List<Map<String, Object>> ipCheckedList(List<Map<String, Object>> ipLists) {
        return ipLists.stream().peek(p ->
            p.put("active", IpCheckedUtil.ipChecked(String.valueOf(p.get("ip"))))
        ).collect(toList());

    }

}
