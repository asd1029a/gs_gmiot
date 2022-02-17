package com.danusys.web.drone.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.bouncycastle.util.Times;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mission")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DynamicUpdate
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "user_id")
    private String userId;

    //    @Column(name="insert_dt")
//    private Timestamp insertDt;
//
    @Column(name = "update_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp updateDt;

//    @Column(name="insert_user_seq")
//    private int insertUserSeq;
//
//    @Column(name="update_user_seq")
//    private int updateUserSeq;
    @Column(name ="total_distance")
    private int totalDistance;

    @Column(name="estimated_time")
    private int estimatedTime;

    // @JsonIgnore
    //  @OneToMany(mappedBy ="mission",cascade =CascadeType.ALL,fetch = FetchType.EAGER)
    @OneToMany(mappedBy = "mission", fetch = FetchType.EAGER)  //마지막에 수정됨
    @JsonManagedReference
    private final List<MissionDetails> missonDetails = new ArrayList<>();


    @OneToOne
    @JoinColumn(name = "drone_id")
    @JsonBackReference
    private Drone drone;


}
