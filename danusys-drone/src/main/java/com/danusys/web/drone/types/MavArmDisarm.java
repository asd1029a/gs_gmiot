package com.danusys.web.drone.types;

import io.dronefleet.mavlink.annotations.MavlinkEntryInfo;
import io.dronefleet.mavlink.annotations.MavlinkEnum;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : kai
 * Date : 2021/12/02
 * Time : 18:36
 */
@MavlinkEnum
public enum MavArmDisarm {
    @MavlinkEntryInfo(0)
    DISARM,

    @MavlinkEntryInfo(1)
    ARM
}
