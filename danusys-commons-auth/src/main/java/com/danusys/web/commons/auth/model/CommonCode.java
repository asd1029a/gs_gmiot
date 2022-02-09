package com.danusys.web.commons.auth.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@EqualsAndHashCode
@Table(name="t_common_code")
public class CommonCode {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="codeSeq")
        private int codeSeq;


        @Column(name="code_name")
        private String codeName;
        //만약에 에러가 날경우 userId-> username auth 쪽에있는 get set userId username으로 바꿀껏
        //repository도수정햇음



        //위에 문제로 바꿨을경우 이것도 수정할것

        @OneToOne(fetch = FetchType.EAGER)
        @JoinColumn(name ="code_value")
        @JsonBackReference
        private User userCommonCode;


}
