package com.danusys.web.drone.service;

import com.danusys.web.drone.api.MAVLinkConnection;
import com.danusys.web.drone.utils.Flight;
import io.dronefleet.mavlink.MavlinkConnection;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FlightManager {
    private Map<Integer, Flight> flightMap = new HashMap<>();

    public String getSessionId() {
        return null;
    }

    public void addData() {
        flightMap.get("droneId");
    }

    private Map<MavlinkConnection,Integer> connectionMap =new HashMap<>();

    public void addConecctionMap(MavlinkConnection connection, int isConnected){
        connectionMap.put(connection,isConnected);
    }

    public Map<MavlinkConnection,Integer> getConnectionMap(){
        return this.connectionMap;
    }

}
