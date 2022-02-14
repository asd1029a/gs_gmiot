package com.danusys.web.platform.service.user;

import com.danusys.web.commons.auth.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserSpecs {

    public static Specification<User> withTitle(Map<String, Object> filter) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            filter.forEach((key, value) -> {
                String likeValue = "%" + value + "%";
              //  log.info("key={}",key);
                switch (key) {
                    case "userName":
                    case "tel":


                        predicates.add(criteriaBuilder.like(root.get(key).as(String.class), likeValue));
                        break;

                }
            });
           // log.info("predicates.size={}",predicates.size());
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

    }
}
