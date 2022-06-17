package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.CommonCode;
import com.danusys.web.commons.api.repository.CommonCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/08
 * Time : 16:41
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCodeService {
    private final CommonCodeRepository commonCodeRepository;


    public List<CommonCode> findAll() {
        return commonCodeRepository.findAll();
    }

    public List<CommonCode> findByParentCodeSeq(Long parentCodeSeq) {
        return commonCodeRepository.findByParentCodeSeq(parentCodeSeq);
    }

    public List<CommonCode> findAllByCodeSeqIn(List<Long> codeSeqs) {
        return commonCodeRepository.findAllByCodeSeqIn(codeSeqs);
    }

}
