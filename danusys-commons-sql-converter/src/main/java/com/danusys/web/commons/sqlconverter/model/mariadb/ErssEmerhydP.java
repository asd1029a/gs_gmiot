package com.danusys.web.commons.sqlconverter.model.mariadb;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Data
@Table(name = "erss_emerhyd_p")
public class ErssEmerhydP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "g2_id")
    private Long g2Id;

    @Column(name = "g2_datasetid", nullable = false)
    private int g2Datasetid;
}
