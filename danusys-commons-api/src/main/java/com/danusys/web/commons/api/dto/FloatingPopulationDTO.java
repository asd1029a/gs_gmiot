package com.danusys.web.commons.api.dto;

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
 * Date : 2022/05/26
 * Time : 19:02
 */

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FloatingPopulationDTO {

    @Getter
    public static class RequestDto {
        private final List<PresenceResult> presenceResultList;

        @JsonCreator
        public RequestDto(@JsonProperty("Presence_result")List<PresenceResult> presenceResultList) {
            this.presenceResultList = presenceResultList;
        }
    }

    @Getter
    public static class PresenceResult {
        private final Msg msg;

        @JsonCreator
        public PresenceResult(@JsonProperty Msg msg) {
            this.msg = msg;
        }
    }

    @Getter
    public static class Msg {
        private final String apName;
        private final StaEthMac staEthMac;

        @JsonCreator
        public Msg(@JsonProperty("sta_eth_mac") StaEthMac staEthMac, @JsonProperty("ap_name") String apName) {
            this.apName = apName;
            this.staEthMac = staEthMac;
        }
    }

    @Getter
    public static class StaEthMac {
        private final String addr;

        @JsonCreator
        public StaEthMac(@JsonProperty("addr") String addr) {
            this.addr = addr;
        }
    }
}
