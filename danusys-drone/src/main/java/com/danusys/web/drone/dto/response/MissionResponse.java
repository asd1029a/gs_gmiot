package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MissionResponse {
    private Long id;
    private String name;
    private String adminUserId;
    private String insertDt;
    private String updateDt;
    private int insertUserSeq;
    private int updateUserSeq;

    //private List<MissionDetailResponse> missionDetailResponseList;



    public MissionResponse(Mission mission){
        this.id = mission.getId();
        this.name=mission.getName();
        this.adminUserId=mission.getAdminUserId();


        this.insertDt=mission.getInsertDt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.updateDt=mission.getUpdateDt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.insertUserSeq= mission.getInsertUserSeq();
        this.updateUserSeq= mission.getUpdateUserSeq();

        //this.missionDetailResponseList= mission.getMissonDetails().stream().map(MissionDetailResponse::new).collect(Collectors.toList());



    }

}
