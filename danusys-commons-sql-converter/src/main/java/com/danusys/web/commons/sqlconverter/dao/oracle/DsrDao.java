package com.danusys.web.commons.sqlconverter.dao.oracle;


import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DsrDao {
    @Select("select * from cm_call")
    List<?> getListDsr();
}
