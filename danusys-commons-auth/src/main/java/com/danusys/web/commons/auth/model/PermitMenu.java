package com.danusys.web.commons.auth.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "v_permit_menu")
public class PermitMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_seq")
    private int codeSeq;
    @Column(name = "code_id")
    private String codeId;
    @Column(name = "code_name")
    private String codeName;
    @Column(name = "code_value")
    private String codeValue;
}
