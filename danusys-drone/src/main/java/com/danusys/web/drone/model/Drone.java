package com.danusys.web.drone.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;


@Entity
@Table(name="drone")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DynamicUpdate
public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="drone_device_name")
    private String droneDeviceName;

    @Column(name="insert_user_id")
    private Long insertUserId;

    @Column(name="insert_dt")
    private Date insertDt;

    @Column(name="update_user_id")
    private Long updateUserId;

    @Column(name="update_dt")
    private Date updateDt;



    @OneToOne(mappedBy ="drone")
    @JsonManagedReference
    private  DroneDetails droneDetails;


    @OneToOne(mappedBy ="drone")
    @JsonManagedReference
    private Mission mission;
}
