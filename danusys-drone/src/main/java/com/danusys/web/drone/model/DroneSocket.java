package com.danusys.web.drone.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "drone_socket")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DynamicUpdate
public class DroneSocket {

    @Id
    Long index;

    String ip;

    String port;

    String localport;
    @Column(name="system_id")
    int systemId;
}
