package com.danusys.web.drone.api;

import com.danusys.web.drone.types.MavModeType;
import io.dronefleet.mavlink.util.EnumValue;

import java.io.IOException;

public class MavClient {


    public static void main(String[] args) throws IOException, InterruptedException {


        DroneCommand droneCommand = new DroneCommand();
        droneCommand.SET_MODE_FUNCTION(EnumValue.of(MavModeType.GUIDED).value());
        Thread.sleep(2000);
//        droneCommand.ARM_DISARM_FUNCTION(EnumValue.of(MavArmDisarm.ARM).value());
//        Thread.sleep(2000);
//        droneCommand.TAKEOFF_FUNCTION(10);
//        Thread.sleep(2000);
//        droneCommand.GOTO_FUNCTION(-35.364321, 149.170504, 10.000000f);
//        Thread.sleep(2000);
//        droneCommand.GOTO_FUNCTION(-35.3644487, 149.1701317, 10.000000f);
    }
}
