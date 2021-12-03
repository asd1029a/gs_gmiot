package com.danusys.web.drone.types;

import io.dronefleet.mavlink.annotations.MavlinkEntryInfo;
import io.dronefleet.mavlink.annotations.MavlinkEnum;

@MavlinkEnum
public enum MavModeType {
    @MavlinkEntryInfo(0)
    STABILIZE,

    @MavlinkEntryInfo(1)
    ACRO,

    @MavlinkEntryInfo(2)
    ALT_HOLD,

    @MavlinkEntryInfo(3)
    AUTO,

    @MavlinkEntryInfo(4)
    GUIDED,

    @MavlinkEntryInfo(5)
    LOITER,

    @MavlinkEntryInfo(6)
    RTL,

    @MavlinkEntryInfo(7)
    CIRCLE,

    @MavlinkEntryInfo(8)
    POSITION,

    @MavlinkEntryInfo(9)
    LAND
}
