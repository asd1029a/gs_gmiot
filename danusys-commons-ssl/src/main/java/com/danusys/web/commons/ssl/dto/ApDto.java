package com.danusys.web.commons.ssl.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/06/03
 * Time : 10:25
 */
public class ApDto {
    @Getter
    @Setter
    public static class RequestDto {
        private List<MsgDto> presenceResultList;

        @JsonCreator
        public RequestDto(@JsonProperty("Presence_result") List<MsgDto> presenceResultList) {
            this.presenceResultList = presenceResultList;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Setter
    @Getter
    public static class MsgDto {
        private MsgContent msg;

        @JsonCreator
        public MsgDto(@JsonProperty("msg") MsgContent msg) {
            this.msg = msg;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Setter
    @Getter
    public static class MsgContent {
        private String apName;
        private StaEthMac staEthMac;

        @JsonCreator
        public MsgContent(@JsonProperty("sta_eth_mac") StaEthMac staEthMac, @JsonProperty("ap_name") String apName) {
            this.apName = apName;
            this.staEthMac = staEthMac;
        }
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Setter
    @Getter
    public static class StaEthMac {
        private String addr;

        @JsonCreator
        public StaEthMac(@JsonProperty("addr") String addr) {
            this.addr = addr;
        }
    }
}
