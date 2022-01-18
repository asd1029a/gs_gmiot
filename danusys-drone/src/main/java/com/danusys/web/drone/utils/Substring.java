package com.danusys.web.drone.utils;

import com.danusys.web.drone.dto.response.Gps;
import io.dronefleet.mavlink.MavlinkMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Substring {

    int time = 0;
    int minTime = 0;
    public void getGps(MavlinkMessage message,Gps gps) {
        String gpsInt = message.getPayload().toString();
        int index = gpsInt.indexOf("lat");

        String gpsX = gpsInt.substring(index + 4, index + 4 + 2) + "." + gpsInt.substring(index + 4 + 2 , index + 4 + 2 + 5);

        index = gpsInt.indexOf("lon");
        String gpsY = gpsInt.substring(index + 4, index + 4 + 3) + "." + gpsInt.substring(index + 4 + 3 , index + 4 + 3 + 5);

 //       gps.setGpsX(Float.parseFloat(gpsX));
  //      gps.setGpsY(Float.parseFloat(gpsY));


    }

    public boolean timerFlag(MavlinkMessage message) {

        String attitude = null;
        String timeBootMs = null;


        int index = 0;
        int substringCount = 10;


        if (message.getPayload().getClass().getName().contains("Attitude")) {
            //log.info("Attitude={}", message.getPayload());
            attitude = message.getPayload().toString();
            index = attitude.indexOf("timeBootMs");
            while ((timeBootMs = attitude.substring(index + 11, index + 11 + substringCount)).contains(",")) {


                substringCount--;
            }
            // 10
            //time : 420668904     >  minTime :  420668654
            time = Integer.parseInt(timeBootMs);
            if (time <= minTime || minTime == 0) {
                minTime = time;
            }

            //log.info("time={},minTime={}",time,minTime);
            if (((time - minTime) / 1000) % 5 == 0) {
            //    log.info("time={}",((time - minTime) / 1000) % 5 == 0);
                return true;
            }
        }

            return false;
    }

}
