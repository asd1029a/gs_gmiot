package com.danusys.web.drone.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name="mission_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DynamicUpdate
public class MissionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int index;
    @Column(name = "gps_x")
    private double gpsX;

    @Column(name= "gps_y")
    private double gpsY;


    private int alt;

    private int speed;

    private int time;

    private double yaw;
   //@ManyToOne(fetch = FetchType.EAGER)
   //@JoinColumn(name ="mission_id")

    private int radius;
    @Column(name="ko_name")
    private String koName;

    @ManyToOne(fetch = FetchType.EAGER)     //마지막에수정됨
    @JoinColumn(name ="mission_id")
    @JsonBackReference
    private Mission mission;
}
