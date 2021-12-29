package com.danusys.web.drone.utils;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@Slf4j
public class Flight {
    @Value("${tcp.server.host}")
    private String tcpServerHost;

    @Value("${tcp.server.port}")
    private int tcpServerPort;

    public String takeoff(float takeOffAlt) {
        MavlinkConnection connection=null;
        try (Socket socket = new Socket("172.20.14.87", 14550)) {

             connection= MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;
            String TerrianReportMessage=null;
            int index=0;
            String currentHeight=null;
            float currentHeightFloat=0;
            /*
        //돌아가기
            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH)
                    .build(), linkId, timestamp, secretKey);
*/

            //   System.out.println(EnumValue.of(MavMode.MAV_MODE_GUIDED_DISARMED).value());

            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_DO_SET_MODE)
                    .param1(1)
                    .param2(4)
                    .build(), linkId, timestamp, secretKey);

            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
                    .param1(1)
                    .param2(0)
                    .build(), linkId, timestamp, secretKey);


            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_TAKEOFF)
                    .param1(15).param2(0).param3(0).param4(0).param5(0).param6(0).param7(takeOffAlt)
                    .build(), linkId, timestamp, secretKey);


//armed


            while ((message = connection.next()) != null) {
                if (message.getPayload().getClass().getName().contains("TerrainReport")) {
                    TerrianReportMessage = message.getPayload().toString();
                    index = TerrianReportMessage.indexOf("currentHeight");
                    currentHeight = TerrianReportMessage.substring(index + 14, index + 14+3);   //float형이라 8자리에서짤라도됨
                    log.info("wow={}",currentHeight);
                    currentHeightFloat=Float.parseFloat(currentHeight);
                    log.info("float={}",currentHeightFloat);
                    if(currentHeightFloat>=takeOffAlt-0.1){
                        break;
                    }
                }

            }
//mavmodetype



        } catch (Exception ioe) {
            ioe.printStackTrace();
            return "fail";
        } finally {
            System.out.println("전송됨");
        }

        return "success";
    }

    public String wayPoint(float x, float y,float z){
        MavlinkConnection connection=null;
        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {

            connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;

            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(1)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(x)
                    //37.445521894457784f
                    .y(y)
                    //126.89555727255802f
                    .z(z)
                    .build(), linkId, timestamp, secretKey);


//            CommandLong STATUS_gps = new CommandLong.Builder().command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL).param1(24).param2(1000000).param7(0).build();
//            connection.send1(255, 0, STATUS_gps);


            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            // MavlinkMessage message;
            String NavControllerOutput = null;
            String wpDistString = null;
            int wpDist = 0;
            int substringCount=10;
            int index=0;

            while ((message = connection.next()) != null) {

                    if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {
                    //        log.info(message.getPayload().toString());
                        NavControllerOutput = message.getPayload().toString();
                        index = NavControllerOutput.indexOf("wpDist");

                     //   log.info("istrue={}",b.contains(","));
                        while((wpDistString=NavControllerOutput.substring(index + 7, index + 7 + substringCount)).contains(",")){
                            substringCount--;

                        }
                    //    log.info(wpDistString);


                        wpDist = Integer.parseInt(wpDistString);
                        //   log.info("c={}",c * 0.000001);
                        //   log.info("x1={}",x1);
                        if (wpDist==0) {
                            log.info("도착");
                            break;
                        }

                    }


            }


        } catch (Exception ioe) {
            ioe.printStackTrace();
            return "fail";
        } finally {
            System.out.println("전송됨");

        }
        return "success";
    }



    public String returnDrone() {
        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {

            MavlinkConnection connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));


            MavlinkMessage message;
            String TerrianReportMessage=null;
            int index=0;
            String currentHeight=null;
            float currentHeightFloat=0;
            //돌아가기
            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH)
                    .build(), linkId, timestamp, secretKey);


            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";

            while ((message = connection.next()) != null) {
                if (message.getPayload().getClass().getName().contains("TerrainReport")) {
                    TerrianReportMessage = message.getPayload().toString();
                    index = TerrianReportMessage.indexOf("currentHeight");
                    currentHeight = TerrianReportMessage.substring(index + 14, index + 14+3);   //float형이라 8자리에서짤라도됨
                    log.info("wow={}",currentHeight);
                    currentHeightFloat=Float.parseFloat(currentHeight);
                    log.info("float={}",currentHeightFloat);
                    if(currentHeightFloat<=0.01){
                        break;
                    }
                }
            }


        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }
        return "success";
    }

}

