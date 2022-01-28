package com.danusys.web.drone.api;

import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
            File file =new File("d:/test.txt");
             BufferedWriter writer=null;

            try (Socket socket = new Socket("172.20.14.87", 14550)) {
            MavlinkConnection connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());
                writer = new BufferedWriter(new FileWriter(file));
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


//
//                    if(
//                            message2.getPayload().getClass().getName().contains("SysStatus") || 시스템정보 + 배터리   // battery voltage배터리  //battery_remaining 배터리
//                            message2.getPayload().getClass().getName().contains("PowerStatus") || //payload=PowerStatus{vcc=5000, vservo=0, flags=EnumValue{value=0, entry=null}}}
//                            message2.getPayload().getClass().getName().contains("NavControllerOutput") || //wpdist 목적지와의 거리
//                            message2.getPayload().getClass().getName().contains("MissionCurrent") || //payload=MissionCurrent{seq=0}
//                            message2.getPayload().getClass().getName().contains("ServoOutputRaw") || //ServoOutputRaw{timeUsec=2772547204, port=0, servo1Raw=1591, servo2Raw=1591, servo3Raw=1590, servo4Raw=1591, servo5Raw=0, servo6Raw=0, servo7Raw=0, servo8Raw=0, servo9Raw=0, servo10Raw=0, servo11Raw=0, servo12Raw=0, servo13Raw=0, servo14Raw=0, servo15Raw=0, servo16Raw=0}
//                            message2.getPayload().getClass().getName().contains("RcChannels") || //RcChannels{timeBootMs=2637547, chancount=16, chan1Raw=1500, chan2Raw=1500, chan3Raw=1000, chan4Raw=1500, chan5Raw=1800, chan6Raw=1000, chan7Raw=1000, chan8Raw=1800, chan9Raw=0, chan10Raw=0, chan11Raw=0, chan12Raw=0, chan13Raw=0, chan14Raw=0, chan15Raw=0, chan16Raw=0, chan17Raw=0, chan18Raw=0, rssi=255}}
//                            message2.getPayload().getClass().getName().contains("RawImu") || //RawImu{timeUsec=2582049267, xacc=0, yacc=1, zacc=-997, xgyro=4, ygyro=3, zgyro=2, xmag=151, ymag=258, zmag=413, id=0, temperature=4499}
//                            message2.getPayload().getClass().getName().contains("ScaledImu2") || //payload=ScaledImu2{timeBootMs=3097547, xacc=4, yacc=0, zacc=-1001, xgyro=-1, ygyro=0, zgyro=0, xmag=119, ymag=274, zmag=413, temperature=4499}
//                            message2.getPayload().getClass().getName().contains("ScaledImu3") ||
//                            message2.getPayload().getClass().getName().contains("ScaledPressure") ||
//                            message2.getPayload().getClass().getName().contains("ScaledPressure2") ||
//                            message2.getPayload().getClass().getName().contains("GlobalPositionInt") ||
//                            message2.getPayload().getClass().getName().contains("GpsRawInt") || //lat=374456473, lon=1268953303, alt=18230, eph=121, epv=200, vel=0, cog=3988, satellitesVisible=10, altEllipsoid=0, hAcc=300, vAcc=300, velAcc=40, hdgAcc=0
//                            message2.getPayload().getClass().getName().contains("SystemTime") ||
//                            message2.getPayload().getClass().getName().contains("TerrainReport") ||//TerrainReport{lat=374433470, lon=1268897507, spacing=100, terrainHeight=14.5117655, currentHeight=100.431244, pending=0, loaded=504}}
//                            message2.getPayload().getClass().getName().contains("LocalPositionNed") || //LocalPositionNed{timeBootMs=12024498, x=-0.07747139, y=0.061710242, z=0.001238235, vx=-0.013305673, vy=-1.9261298E-4, vz=4.191259E-4}}
//                            message2.getPayload().getClass().getName().contains("Vibration") || //Vibration{timeUsec=14555498804, vibrationX=0.0026672243, vibrationY=0.0027407336, vibrationZ=0.0027245833, clipping0=0, clipping1=0, clipping2=0}}
//                            message2.getPayload().getClass().getName().contains("BatteryStatus") || //batteryFunction=EnumValue{value=0, entry=MAV_BATTERY_FUNCTION_UNKNOWN}, type=EnumValue{value=0, entry=MAV_BATTERY_TYPE_UNKNOWN}, temperature=32767, voltages=[12587, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535], currentBattery=0, currentConsumed=151475, energyConsumed=68546, batteryRemaining=0, timeRemaining=0, chargeState=EnumValue{value=1, entry=MAV_BATTERY_CHARGE_STATE_OK}}}
//                            message2.getPayload().getClass().getName().contains("Attitude") || //=Attitude{timeBootMs=11556094, roll=0.0018187475, pitch=6.057985E-4, yaw=2.2014248, rollspeed=2.1905173E-4, pitchspeed=2.5810348E-4, yawspeed=7.619873E-4}}
//                            message2.getPayload().getClass().getName().contains("VfrHud") || // payload=VfrHud{airspeed=3.3680003, groundspeed=3.021782, heading=37, throttle=34, alt=99.979996, climb=0.015121608}}
//                            message2.getPayload().getClass().getName().contains("Heartbeat") || //Heartbeat{type=EnumValue{value=2, entry=MAV_TYPE_QUADROTOR}, autopilot=EnumValue{value=3, entry=MAV_AUTOPILOT_ARDUPILOTMEGA}, baseMode=EnumValue{value=89, entry=null}, customMode=6, systemStatus=EnumValue{value=3, entry=MAV_STATE_STANDBY}, mavlinkVersion=3}}
//                            message2.getPayload().getClass().getName().contains("Meminfo") || //payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}
//                            message2.getPayload().getClass().getName().contains("Ahrs") || //Ahrs{omegaix=-0.0025094047, omegaiy=-0.0025298656, omegaiz=-0.0020406283, accelWeight=0.0, renormVal=0.0, errorRp=0.002322554, errorYaw=0.0013488759}}
//                            message2.getPayload().getClass().getName().contains("Hwstatus") || // payload=Hwstatus{vcc=5000, i2cerr=0}}
//                            message2.getPayload().getClass().getName().contains("MountStatus") || //x
//                            message2.getPayload().getClass().getName().contains("EkfStatusReport") || //velocityVariance=0.021335527, posHorizVariance=0.025864147, posVertVariance=0.0017659252, compassVariance=0.035943523, terrainAltVariance=0.0, airspeedVariance=0.0}}
//                            message2.getPayload().getClass().getName().contains("Simstate") || //Simstate{roll=5.885804E-4, pitch=-5.997561E-7, yaw=-1.0545728, xacc=-0.09285733, yacc=-0.050651476, zacc=-9.81958, xgyro=-0.0067729917, ygyro=-0.0050521465, zgyro=3.3953006E-4, lat=374456475, lng=1268953310}
//                            message2.getPayload().getClass().getName().contains("Ahrs2") || //x
//                            message2.getPayload().getClass().getName().contains("Timesync") ||
//                            message2.getPayload().getClass().getName().contains("ParamValue") || //x
//                            message2.getPayload().getClass().getName().contains("PositionTargetGlobalInt") || //x
//                            message2.getPayload().getClass().getName().contains("EscTelemetry1To4") || //EscTelemetry1To4{temperature=[B@553a3d88, voltage=[0, 0, 0, 0], current=[0, 0, 0, 0], totalcurrent=[0, 0, 0, 0], rpm=[0, 0, 0, 0], count=[0, 0, 0, 0]}
//                            message2.getPayload().getClass().getName().contains("SensorOffsets") //EscTelemetry1To4{temperature=[B@553a3d88, voltage=[0, 0, 0, 0], current=[0, 0, 0, 0], totalcurrent=[0, 0, 0, 0], rpm=[0, 0, 0, 0], count=[0, 0, 0, 0]}
//                    //여기서부터 내가작성
//                           ) {}
//                           else {


                        {

                            writer.write(message2.getPayload()+"\n");
                          //  System.out.println(message2.getPayload().getClass().getName() + " > " + message2 );
//                            System.out.println("==========================================================================================");
                        }

                  //  }

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
            finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
