package com.danusys.web.commons.api.querydsl;

import com.danusys.web.commons.api.model.Facility;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FacilityQueryDslRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public List<Facility> findAll() {
        return this.em.createQuery("select f from Facility f", Facility.class)
                .getResultList();
    }

    public List<Facility> findByGeomSql(double latitude, double longitude, String administZone) {
        List fcltList = em.createNativeQuery(
            Expressions.stringTemplate(
                "select * from fn_lonlat_nearest_facility({0},{1},{2},{3},{4})",
                longitude, latitude, administZone, 500, 5
            ).toString()
            , Facility.class
        ).getResultList();
        return fcltList;
    }
}
