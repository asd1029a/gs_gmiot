package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.CommonCode;
import com.danusys.web.commons.api.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/08
 * Time : 16:06
 */
public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {
    List<CommonCode> findAll();

    List<CommonCode> findByParentCodeSeq(Long parentCodeSeq);

    List<CommonCode> findAllByCodeSeqIn(List<Long> codeSeqs);

}
