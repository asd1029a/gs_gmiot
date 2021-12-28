package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.MissionDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissionDetailResponse {
    private Long id;
    private String name;
    private int index;
    private float mapX;
    private float mapY;
    private float mapZ;

    public MissionDetailResponse(MissionDetails missonDetails){
        this.id = missonDetails.getId();
        this.name= missonDetails.getName() ;
        this.index= missonDetails.getIndex();
        this.mapX= missonDetails.getX();
        this.mapY= missonDetails.getY();
        this.mapZ= missonDetails.getZ();


    }
}
