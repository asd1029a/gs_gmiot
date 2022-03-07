package com.danusys.web.drone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "drone_in_mission")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DynamicUpdate
public class DroneInMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="index")
    private Long index;


    @OneToOne()
    @JoinColumn(name="drone_seq")
    @JsonIgnore
    private Drone drone;

    @ManyToOne
    @JoinColumn(name="mission_seq")

    private Mission mission;


}
