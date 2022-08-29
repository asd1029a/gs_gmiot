package com.danusys.web.commons.sqlconverter;

import com.danusys.web.commons.sqlconverter.model.mariadb.ErssEmerhydP;
import com.danusys.web.commons.sqlconverter.repository.mariadb.ErssEmerhydPRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;

@WebAppConfiguration
@RunWith(SpringRunner.class)
//@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DataJpaTest
//@ContextConfiguration(classes={SpringBootMultipleJpaApplicationTests.class})
//@EnableJpaRepositories(basePackages = {"com.danusys.web.*"})
//@EntityScan("com.danusys.web.commons.model.mariadb")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@TestPropertySource(properties = "test.type=2")
public class SpringBootMultipleJpaApplicationTests {

    @Autowired
    private ErssEmerhydPRepository erssEmerhydPRepository;


    @Test
   // @Transactional
    public void test() {
       // List<ErssEmerhydP> temp = erssEmerhydPRepository.findAll();
        Optional<ErssEmerhydP> tempById = erssEmerhydPRepository.findById(463L);
        assert (tempById.isPresent());
        System.out.println("aaaa");

        /*erssEmerhydPRepository.findAll()
                .stream()
                .map(i -> i.toString())
                .forEach(log::info);*/

    }
}
