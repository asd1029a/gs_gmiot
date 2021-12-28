package com.danusys.web.drone.model;

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

    private float x;

    private float y;

    private float z;

   @ManyToOne(fetch = FetchType.EAGER)
  // @ManyToOne
   @JoinColumn(name="mission_id")
    private Mission mission;
}
