package com.danusys.web.drone.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


@Entity
@Table(name = "drone")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DynamicUpdate
@DynamicInsert
public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drone_device_name", unique = true, nullable = false)
    private String droneDeviceName;


    @Column(name = "user_id")
    private String userId;

    @Column(name ="socket_index" ,unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long socketIndex;

    @Column(name = "update_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")

    private Timestamp updateDt;


    @Column(name = "arm_status")
    private int armStatus;

    @OneToOne(mappedBy = "drone")
    @JsonManagedReference
    private DroneDetails droneDetails;


    private String status;


//    @OneToOne(mappedBy = "drone")
//    @JsonManagedReference
//    private Mission mission;
//


    @OneToOne(mappedBy ="drone")
    private DroneInMission droneInmission;


    @ManyToOne
    @JoinColumn(name="drone_base_id")

    private DroneBase droneBase;
}
