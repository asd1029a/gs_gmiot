package com.danusys.web.drone.api;

import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/23
 * Time : 10:12 오전
 */
public class DroneMessageRecevice {
    public static void main(String[] args) {
        //try (Socket socket = new Socket("172.20.14.84", 14550)) {
            try (Socket socket = new Socket("172.20.14.87", 14550)) {
            MavlinkConnection connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

//
//            CommandAck commandAck = CommandAck.builder()
//                    .command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
//                    .result(MavResult.MAV_RESULT_ACCEPTED)
//                    .progress(0)
//                    .resultParam2(0)
//                    .targetSystem(255)
//                    .targetComponent(190)
//                    .build();
//
//            int systemId = 1;
//            int componentId = 1;
//            int linkId = 1;
//            long timestamp = System.currentTimeMillis();/* provide microsecond time */;
//            byte[] secretKey = MessageDigest.getInstance("SHA-256")
//                    .digest("danusys".getBytes(StandardCharsets.UTF_8));
////            connection.send2(systemId, componentId, commandAck, linkId, timestamp, secretKey);
//
//            GpsGlobalOrigin gpsGlobalOrigin = GpsGlobalOrigin.builder()
//                    .latitude(-353643232)
//                    .longitude(1491705072)
//                    .altitude(577850)
//                    .build();
//            connection.send2(systemId, componentId, gpsGlobalOrigin, linkId, timestamp, secretKey);
//
//            HomePosition homePosition = HomePosition.builder()
//                    .latitude(-353643232)
//                    .longitude(1491705072)
//                    .altitude(577850)
//                    .build();
//            connection.send2(systemId, componentId, homePosition, linkId, timestamp, secretKey);


            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                if (message instanceof Mavlink2Message) {
                     Mavlink2Message message2 = (Mavlink2Message)message;



                    if(     message2.getPayload().getClass().getName().contains("GlobalPositionInt") || //x
                            message2.getPayload().getClass().getName().contains("SysStatus") ||  //x
                            message2.getPayload().getClass().getName().contains("PowerStatus") || //payload=PowerStatus{vcc=5000, vservo=0, flags=EnumValue{value=0, entry=null}}}
                            message2.getPayload().getClass().getName().contains("NavControllerOutput") || //wpdist 목적지와의 거리
                            message2.getPayload().getClass().getName().contains("MissionCurrent") ||
                            message2.getPayload().getClass().getName().contains("ServoOutputRaw") || //ServoOutputRaw{timeUsec=2772547204, port=0, servo1Raw=1591, servo2Raw=1591, servo3Raw=1590, servo4Raw=1591, servo5Raw=0, servo6Raw=0, servo7Raw=0, servo8Raw=0, servo9Raw=0, servo10Raw=0, servo11Raw=0, servo12Raw=0, servo13Raw=0, servo14Raw=0, servo15Raw=0, servo16Raw=0}
                            message2.getPayload().getClass().getName().contains("RcChannels") || //RcChannels{timeBootMs=2637547, chancount=16, chan1Raw=1500, chan2Raw=1500, chan3Raw=1000, chan4Raw=1500, chan5Raw=1800, chan6Raw=1000, chan7Raw=1000, chan8Raw=1800, chan9Raw=0, chan10Raw=0, chan11Raw=0, chan12Raw=0, chan13Raw=0, chan14Raw=0, chan15Raw=0, chan16Raw=0, chan17Raw=0, chan18Raw=0, rssi=255}}
                            message2.getPayload().getClass().getName().contains("RawImu") || //RawImu{timeUsec=2582049267, xacc=0, yacc=1, zacc=-997, xgyro=4, ygyro=3, zgyro=2, xmag=151, ymag=258, zmag=413, id=0, temperature=4499}
                            message2.getPayload().getClass().getName().contains("ScaledImu2") || //payload=ScaledImu2{timeBootMs=3097547, xacc=4, yacc=0, zacc=-1001, xgyro=-1, ygyro=0, zgyro=0, xmag=119, ymag=274, zmag=413, temperature=4499}
                            message2.getPayload().getClass().getName().contains("ScaledImu3") ||
                            message2.getPayload().getClass().getName().contains("ScaledPressure") ||
                            message2.getPayload().getClass().getName().contains("ScaledPressure2") ||
                            message2.getPayload().getClass().getName().contains("GpsRawInt") || //lat=374456473, lon=1268953303, alt=18230, eph=121, epv=200, vel=0, cog=3988, satellitesVisible=10, altEllipsoid=0, hAcc=300, vAcc=300, velAcc=40, hdgAcc=0
                            message2.getPayload().getClass().getName().contains("SystemTime") ||
                            message2.getPayload().getClass().getName().contains("TerrainReport") || //TerrainReport{lat=374433470, lon=1268897507, spacing=100, terrainHeight=14.5117655, currentHeight=100.431244, pending=0, loaded=504}}
                            message2.getPayload().getClass().getName().contains("LocalPositionNed") ||
                            message2.getPayload().getClass().getName().contains("Vibration") ||
                            message2.getPayload().getClass().getName().contains("BatteryStatus") ||
                            message2.getPayload().getClass().getName().contains("Attitude") ||
                            message2.getPayload().getClass().getName().contains("VfrHud") ||
                            message2.getPayload().getClass().getName().contains("Heartbeat") ||
                            message2.getPayload().getClass().getName().contains("Meminfo") ||
                            message2.getPayload().getClass().getName().contains("Ahrs") ||
                            message2.getPayload().getClass().getName().contains("Hwstatus") || //x
                            message2.getPayload().getClass().getName().contains("MountStatus") || //x
                            message2.getPayload().getClass().getName().contains("EkfStatusReport") ||
                            message2.getPayload().getClass().getName().contains("Simstate") || //Simstate{roll=5.885804E-4, pitch=-5.997561E-7, yaw=-1.0545728, xacc=-0.09285733, yacc=-0.050651476, zacc=-9.81958, xgyro=-0.0067729917, ygyro=-0.0050521465, zgyro=3.3953006E-4, lat=374456475, lng=1268953310}
                            message2.getPayload().getClass().getName().contains("Ahrs2") || //x
                            message2.getPayload().getClass().getName().contains("Timesync") ||
                            message2.getPayload().getClass().getName().contains("ParamValue") || //x
                            message2.getPayload().getClass().getName().contains("PositionTargetGlobalInt") ||
                            message2.getPayload().getClass().getName().contains("EscTelemetry1To4") //EscTelemetry1To4{temperature=[B@553a3d88, voltage=[0, 0, 0, 0], current=[0, 0, 0, 0], totalcurrent=[0, 0, 0, 0], rpm=[0, 0, 0, 0], count=[0, 0, 0, 0]}
                    //여기서부터 내가작성
                           ) {
                    } else {


                        {

                            System.out.println(message2.getPayload().getClass().getName() + " > " + message2 );
                            System.out.println("==========================================================================================");
                        }

                    }

                    if (message2.isSigned()) {

                    } else {

                    }
                } else {
                    // This is a Mavlink1 message.
                }

//                if (message.getPayload() instanceof Heartbeat) {
//                    MavlinkMessage<Heartbeat> heartbeatMessage = (MavlinkMessage<Heartbeat>)message;
//                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
