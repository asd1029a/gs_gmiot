package com.danusys.web.commons.api.dto;

import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.types.DataType;
import com.danusys.web.commons.api.types.ParamType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/04/21
 * Time : 20:02
 */

/**
 *
 */
public class ApiParamDto {

    @NoArgsConstructor
    @Setter
    @Getter
    public static class Request {
        private Long id;
        private int seq;
        private String fieldNm;
        private String fieldMapNm;
        private DataType dataType;
        private ParamType paramType;
        private String value;
        private Long apiId;

        public ApiParam toEntity() {
            ApiParam apiParam = ApiParam.builder()
                    .id(id)
                    .seq(seq)
                    .fieldNm(fieldNm)
                    .fieldMapNm(fieldMapNm)
                    .dataType(dataType)
                    .paramType(paramType)
                    .value(value)
                    .apiId(apiId)
                    .build();
            return apiParam;
        }
    }

    /**
     * test
     */
    @NoArgsConstructor
    @Getter
    public static class Response {
        private Long id;
        private Long apiId;
        private String value;

        public Response(ApiParam apiParam) {
            this.id = apiParam.getId();
            this.value = apiParam.getValue();
        }
    }

}
