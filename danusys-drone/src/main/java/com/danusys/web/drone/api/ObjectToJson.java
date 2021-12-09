package com.danusys.web.drone.api;

import com.google.gson.Gson;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;

import java.io.IOException;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : kai
 * Date : 2021/12/09
 * Time : 14:46
 */
public class ObjectToJson {
    public static void main(String[] args) throws IOException {
        Object object = Heartbeat.builder()
                .autopilot(MavAutopilot.MAV_AUTOPILOT_GENERIC)
                .type(MavType.MAV_TYPE_GENERIC)
                .systemStatus(MavState.MAV_STATE_UNINIT)
                .baseMode()
                .mavlinkVersion(3)
                .build();

//        String str = object.toString().replaceAll("=","\":\"");
//                str = str.replaceAll("[{]","\":{\"");
//                str = str.replaceAll("}, ","\"}, \"");
//                str = str.replaceAll(", ","\", ");
//
//        System.out.println("{\"" + str + "}");

        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        System.out.println(jsonString);
        System.out.println("{\"" + object.getClass().getSimpleName() + "\":" + jsonString + "}");

//        ObjectMapper objectMapper = new ObjectMapper();
//        String str = objectMapper.writeValueAsString(object);
//
//        System.out.println(str);

    }
}
