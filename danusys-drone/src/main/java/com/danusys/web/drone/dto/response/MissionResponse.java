package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.Mission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class MissionResponse {
    private Long id;
    private String name;
    private List<MissionDetailResponse> missionDetailResponseList;



    public MissionResponse(Mission mission){
        this.id = mission.getId();
        this.name=mission.getName();

        this.missionDetailResponseList= mission.getMissonDetails().stream().map(MissionDetailResponse::new).collect(Collectors.toList());



    }

}
