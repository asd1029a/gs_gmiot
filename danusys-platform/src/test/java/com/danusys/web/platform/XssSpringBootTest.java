package com.danusys.web.platform;

import com.danusys.web.commons.app.model.XssRequestDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/18
 * Time : 09:41
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "test.type=2")
public class XssSpringBootTest {

    @Autowired
    private RestTemplate restTemplate;

    // HTMLCharacterEscapes
    @Test
    public void xssJsonTest() {

        String content = "<script></script>";
        String expected = "&lt;script&gt;&lt;/script&gt;";

        Map<String, Object> param = new HashMap<>();
        param.put("content", content);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:8082/xssMap", param, Map.class);

//        assertEquals(response.getHeaders().getContentType(), ContentType.APPLICATION_JSON);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()), expected);
    }

    // lucy filter
    @Test
    public void xssFormTest() {
        String content = "<li>content</li>";
        String expected = "&lt;li&gt;content&lt;/li&gt;";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", content);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<XssRequestDto> response = restTemplate.exchange("/form",
                HttpMethod.POST,
                entity,
                XssRequestDto.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(Objects.requireNonNull(response.getHeaders().getContentType()).toString(), "text/plain;charset=UTF-8");

        assertEquals(Objects.requireNonNull(response.getBody()).getContent(), expected);
    }

}
