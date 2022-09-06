package com.danusys.web.commons.sqlconverter.scheduler;

import com.danusys.web.commons.sqlconverter.repository.mariadb.ErssEmerhydPRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MariadbScheduler {

    @Autowired
    private ErssEmerhydPRepository erssEmerhydPRepository;

    @Bean
    public void table1() {
        var test = erssEmerhydPRepository.findById(463L);
        log.info("test = {}", test);
    }

}
