package com.danusys.web.drone.api;

import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/13
 * Time : 4:36 오전
 */
public class MessageService {

    public static MavlinkConnection tcpSocket() {
        // This example uses a TCP socket, however we may also use a UDP socket by injecting
// PipedInputStream/PipedOutputStream to MavlinkConnection, or even USB by using any
// implementation that will eventually yield an InputStream and an OutputStream.
        MavlinkConnection connection = null;
        try (Socket socket = new Socket("127.0.0.1", 5760)) {
            // After establishing a connection, we proceed to building a MavlinkConnection instance.
            connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            // Now we are ready to read and send messages.
            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                // The received message could be either a Mavlink1 message, or a Mavlink2 message.
                // To check if the message is a Mavlink2 message, we could do the following:
                if (message instanceof Mavlink2Message) {
                    // This is a Mavlink2 message.
                    Mavlink2Message message2 = (Mavlink2Message)message;

                    if (message2.isSigned()) {
                        // This is a signed message. Let's validate its signature.
//                        if (message2.validateSignature(mySecretKey)) {
//                            // Signature is valid.
//                        } else {
//                            // Signature validation failed. This message is suspicious and
//                            // should not be handled. Perhaps we should log this incident.
//                        }
                    } else {
                        // This is an unsigned message.
                    }
                } else {
                    // This is a Mavlink1 message.
                }

                // When a message is received, its payload type isn't statically available.
                // We can resolve which kind of message it is by its payload, like so:
                if (message.getPayload() instanceof Heartbeat) {
                    // This is a heartbeat message
                    MavlinkMessage<Heartbeat> heartbeatMessage = (MavlinkMessage<Heartbeat>)message;
                }
                // We are better off by publishing the payload to a pub/sub mechanism such
                // as RxJava, JMS or any other favorite instead, though.
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return connection;
    }

    public static void heartbeatTest() throws NoSuchAlgorithmException, IOException {
        MavlinkConnection connection = tcpSocket();
        int systemId = 255;
        int componentId = 0;
        Heartbeat heartbeat = Heartbeat.builder()
                .type(MavType.MAV_TYPE_GCS)
                .autopilot(MavAutopilot.MAV_AUTOPILOT_INVALID)
                .systemStatus(MavState.MAV_STATE_UNINIT)
                .mavlinkVersion(3)
                .build();

// Write an unsigned heartbeat
        connection.send2(systemId, componentId, heartbeat);

// Write a signed heartbeat
        int linkId = 1;
        long timestamp = System.currentTimeMillis() /* provide microsecond time */;
        byte[] secretKey = MessageDigest.getInstance("SHA-256")
                .digest("a secret phrase".getBytes(StandardCharsets.UTF_8));
//        connection.send2(systemId, componentId, heartbeat, linkId, timestamp, secretKey);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        heartbeatTest();
    }
}
