package com.danusys.web.commons.api.querydsl;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.QFacility;
import com.danusys.web.commons.api.model.QStation;
import com.danusys.web.commons.app.CommonUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FacilityQueryDslRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    QFacility facility = QFacility.facility;

    public List<Facility> findAll() {
        return this.em.createQuery("select f from Facility f", Facility.class)
                .getResultList();
    }

    public List<Facility> findByGeomSql(double latitude, double longitude, String administZone) {
        Integer maxDistance = 500;
        Integer limitRank = 5;

        List fcltList = em.createNativeQuery(
            Expressions.stringTemplate(
                "select * from fn_lonlat_nearest_facility({0},{1},{2},{3},{4})",
                longitude, latitude, administZone, maxDistance, limitRank
            ).toString()
            , Facility.class
        ).getResultList();
        return fcltList;
    }

    public List<Facility> findByParam(Map<String, Object> paramMap) {

        String stationSeq = CommonUtil.validOneNull(paramMap, "stationSeq");


        List<Facility> fcltList = queryFactory
            .selectFrom(facility)
            .distinct()
            .where(
//                stationSeqEq(stationSeq),
                facilityKindIn((ArrayList) paramMap.get("facilityKind")),
                administZoneIn((ArrayList) paramMap.get("administZone"))
            )
            .fetch();

        System.out.println("^^^^^^^^^^^^^^^^^^");
//        System.out.println(fcltList);

        return fcltList;
    }

//    private BooleanExpression stationSeqEq(String stationSeq) {
//        return stationSeq != "" ? facility.stationSeq.eq(Long.parseLong(stationSeq)) : null;
//    }

    private BooleanExpression facilityKindIn(ArrayList facilityKindList) {
        return facilityKindList != null ? facility.facilityKind.in(facilityKindList) : null;
    }

    private BooleanExpression administZoneIn(ArrayList administZoneList) {
        return administZoneList != null ? facility.administZone.in(administZoneList) : null;
    }



}
