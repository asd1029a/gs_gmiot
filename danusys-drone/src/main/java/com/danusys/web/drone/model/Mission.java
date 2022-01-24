package com.danusys.web.drone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="mission")
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


   // @JsonIgnore
  //  @OneToMany(mappedBy ="mission",cascade =CascadeType.ALL,fetch = FetchType.EAGER)
   @OneToMany(mappedBy ="mission")
   @JsonManagedReference
    private final List<MissionDetails> missonDetails= new ArrayList<>();

}
