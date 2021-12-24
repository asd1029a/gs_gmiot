package com.danusys.web.drone.service;


import com.danusys.web.drone.model.Misson;
import com.danusys.web.drone.repository.MissonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissonService {

    private final MissonRepository missonRepository;

    @Transactional
    public Misson saveMisson(Misson misson){ return missonRepository.save(misson);}


    public List<Misson> findMisson(String name) {
        return missonRepository.findByName(name);
    }


}
