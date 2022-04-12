package com.danusys.web.commons.auth.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "v_user_status")
public class UserStatus implements Serializable {

    @Id
    @Column(name="code_id")
    private String codeId;

    @Column(name="code_name")
    private String codeName;

    @Column(name="code_value")
    private String codeValue;

}
