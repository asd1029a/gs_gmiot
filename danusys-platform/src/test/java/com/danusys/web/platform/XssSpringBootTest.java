package com.danusys.web.platform;

import com.danusys.web.platform.model.XssRequestDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    private TestRestTemplate testRestTemplate;

    // HTMLCharacterEscapes
    @Test
    public void xssJsonTest() {

        String content = "{'content':'<script></script>'}";
        String expected = "&lt;script&gt;&lt;/script&gt;";

        ResponseEntity<String> response = testRestTemplate.postForEntity("/xss", content, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()), expected);
    }

    // lucy filter
    @Test
    public void xssFormTest() {
        String content = "<li>content</li>";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", content);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<XssRequestDto> response = testRestTemplate.exchange("/form",
                HttpMethod.POST,
                entity,
                XssRequestDto.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(Objects.requireNonNull(response.getHeaders().getContentType()).toString(), "text/plain;charset=UTF-8");

        assertEquals(Objects.requireNonNull(response.getBody()).getContent(), content);
    }

}
