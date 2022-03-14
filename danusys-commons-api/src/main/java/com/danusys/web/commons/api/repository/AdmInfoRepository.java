package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.AdmInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdmInfoRepository extends JpaRepository<AdmInfo, String> {
    @Query(value = "select t3.area_name as area_name , t3.area_code as area_code from (" +
            " select t1.emd_cd from t_area_emd t1" +
            " where st_contains(t1.geom, ST_GEOMFROMTEXT(\'POINT(\'|| :lon ||\' \'|| :lat ||\')\',4326)) = true" +
            " ) t2  inner join t_area_code_name t3 on t2.emd_cd||\'00\' = t3.area_code", nativeQuery = true)
    AdmInfo findArea(String lon, String lat);
}
