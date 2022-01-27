package com.danusys.web.drone.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "drone_log")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DynamicUpdate
public class DroneLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drone_device_name")
    private String droneDeviceName;

    @Column(name = "mission_name")
    private String missionName;


    @OneToMany(mappedBy = "droneLog" ,fetch = FetchType.EAGER)
    @JsonManagedReference
    private final List<DroneLogDetails> droneLogDetails= new ArrayList<>();
}
