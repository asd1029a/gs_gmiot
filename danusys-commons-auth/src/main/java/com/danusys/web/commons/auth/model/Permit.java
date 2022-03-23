package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "v_permit")
public class Permit {
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
