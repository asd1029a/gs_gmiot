package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.Mission;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class MissionResponse {
    private Long id;
    private String name;
    private List<MissionDetailResponse> missionDetailResponseList;


    public MissionResponse(Mission misson){
        this.id = misson.getId();
        this.name=misson.getName();
        this.missionDetailResponseList= misson.getMissonDetails().stream().map(MissionDetailResponse::new).collect(Collectors.toList());



    }
}
