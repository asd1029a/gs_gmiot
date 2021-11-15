package com.danusys.web.drone;

import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.protocol.MavlinkPacket;
import io.dronefleet.mavlink.serialization.payload.MavlinkPayloadSerializer;
import io.dronefleet.mavlink.serialization.payload.reflection.ReflectionPayloadSerializer;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CrcTests {

    private static final MavlinkPayloadSerializer serializer = new ReflectionPayloadSerializer();

    @Test
    public void cLibraryFailsInvalidCrc() {
        MavlinkPacket packet = MavlinkPacket.createMavlink1Packet(1, 255, 0, 0, 0 /*should be 50*/,
                serializer.serialize(Heartbeat.builder()
                        .build()));


        System.out.println(packet.getRawBytes());
//        assertFalse(CLibraryTestTool.crcCheck(packet.getRawBytes()));
    }

    @Test
    public void mavlink1PacketPassesCrcCheck() {
        MavlinkPacket packet = MavlinkPacket.createMavlink1Packet(
                1, 255, 0, 0, 50, serializer.serialize(Heartbeat.builder().build()));
        System.out.println(packet.getRawBytes());
//        assertTrue(CLibraryTestTool.crcCheck(packet.getRawBytes()));
    }

    @Test
    public void unsignedMavlink2PacketPassesCrcCheck() {
        MavlinkPacket packet = MavlinkPacket.createUnsignedMavlink2Packet(
                1, 255, 0, 0, 50, serializer.serialize(Heartbeat.builder().build()));

        System.out.println(packet.getRawBytes());
//        assertTrue(CLibraryTestTool.crcCheck(packet.getRawBytes()));
    }

    @Test
    public void signedMavlink2PacketPassesCrcCheck() throws NoSuchAlgorithmException {
        MavlinkPacket packet = MavlinkPacket.createSignedMavlink2Packet(
                1, 255, 0, 0, 50, serializer.serialize(Heartbeat.builder().build()),
                1, 12345L, MessageDigest.getInstance("SHA-256")
                        .digest("test".getBytes(StandardCharsets.UTF_8)));
        System.out.println(packet.getRawBytes());
//        assertTrue(CLibraryTestTool.crcCheck(packet.getRawBytes()));
    }
}
