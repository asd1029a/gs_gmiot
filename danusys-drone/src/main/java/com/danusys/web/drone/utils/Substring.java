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

    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    /**
     * Binary byte to String
     *
     * @param n
     * @return
     */
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    /**
     * Binary String to byte[]
     *
     * @param s
     * @return
     */
    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    /**
     * Binary String to byte
     *
     * @param s
     * @return
     */
    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }

}
