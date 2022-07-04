package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.dto.CctvDTO;
import com.danusys.web.commons.api.util.XmlDataUtil;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/06/17
 * Time : 5:24 PM
 */
public class CctvMain {
    public static void main(String[] args) {
        CctvDTO cctvDTO = XmlDataUtil.getCctvInfo_test("data/gm_soap/cctv_test.xml");

        System.out.println(cctvDTO.getGetAllCenterListRsp().getCount());
        System.out.println(cctvDTO.getGetAllCenterListRsp().getStreamServerList().getCount());
        System.out.println(cctvDTO.getGetAllCenterListRsp().getStreamServerList().getStreamServerInfo().size());
        System.out.println(cctvDTO);
    }
}
