package com.danusys.web.commons.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "api_param_conv")
public class ApiParamConv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int conv_seq;

    @Column(nullable = false)
    private int api_param_seq;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;
}
