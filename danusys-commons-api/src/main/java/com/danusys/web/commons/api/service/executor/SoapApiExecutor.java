package com.danusys.web.commons.api.service.executor;

import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.util.StrUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author odlodl
 * @since 2020-0-24
 */
@Slf4j
@Service("SOAP")
public class SoapApiExecutor implements ApiExecutor  {

    @Override
    public ResponseEntity execute(Api api) throws Exception {
        if (api == null)
            throw new IllegalArgumentException("입력된 API 파라미터 값이 null 입니다.");
        if (api.getCallUrl() == null)
            throw new IllegalArgumentException("호출 URL 값이 null 입니다.");
        if (api.getApiRequestParams() == null || api.getApiRequestParams().isEmpty())
            throw new IllegalArgumentException("apiRequestParams 값이 null 입니다.");

        final List<ApiParam> apiRequestParams = api.getApiRequestParams().stream().sorted(Comparator.comparing((ApiParam p) -> p.getSeq())).collect(Collectors.toList());
        log.trace("### apiRequestParams : {}", apiRequestParams.toString());

        Map<String, Object> result = new HashMap<>();
        //paramMap
//        final Map<String, Object> reqMap = apiRequestParams.stream().collect(Collectors.toMap(ApiParam::getFieldMapNm, ApiParam::getValue));
        final String targetURL = api.getTargetUrl(); //호출 URL
        final String webServiceURI = api.getTargetPath(); //서비스 URL
        final String webServicePrefix = api.getServicePrefix(); //prefix
        final String serviceName = api.getServiceNm(); //서비스명
        final SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection(); // ConnectionFactory 로 부터 Connection 생성


        try {
            MessageFactory messageFactory = MessageFactory.newInstance();// MessageFactory 생성
            SOAPMessage soapMessage = messageFactory.createMessage();// MessageFactory 로 부터 SOAPMessage 생성
//        SOAPHeader soapHeader = soapMessage.getSOAPHeader(); // SOAPMessage 에서 SOAPHeader 를 얻음
            // SOAPMessage 에서 SOAPBody 를 얻음
            SOAPFactory soapFactory = SOAPFactory.newInstance(); // SOAPFactory 생성

            SOAPBody soapBody = soapMessage.getSOAPBody();
            log.trace("### serviceName : {}", serviceName);
            log.trace("### webServicePrefix : {}", webServicePrefix);
            log.trace("### webServiceURI : {}", webServiceURI);
            Name nameRequest = soapFactory.createName(serviceName, webServicePrefix, webServiceURI); // SOAPFactory 로 부터 Name 객체 생성 (이름, Prefix, URI)

            SOAPBodyElement elRequest = soapBody.addBodyElement(nameRequest); // 생성한 Name 객체를 SOAPBody 에 추가하고 BodyElement 를 얻음

            // 생성한 SOAPBodyElement 에 다시 ChildElement 를 추가하고 각각의 ChildElement 에 TextNode 를 추가
            apiRequestParams.forEach(apiReq -> {
                try {
                    elRequest.addChildElement(apiReq.getFieldMapNm(), webServicePrefix).addTextNode(apiReq.getValue());
                } catch (SOAPException e) {
//                    e.printStackTrace();
                    log.error("파라미터 세팅 SoapApiExecutor 오류");
                }
            });

            // SOAPMessage 를 requestURL 로 전송하고 서버쪽에서 내려보낸 정보가 담긴 SOAPMessage 객체를 얻음
            SOAPMessage responseMessage = soapConnection.call(soapMessage, targetURL);
            responseMessage.writeTo(System.out);

            SOAPBody resBody = responseMessage.getSOAPBody();
            Iterator resBodyChildElements = resBody.getChildElements();
            Node rootNode = (Node) resBodyChildElements.next();
            NodeList nodes = rootNode.getChildNodes().item(0).getChildNodes();

            //        final Map<String, Object> reqMap = apiRequestParams.stream().collect(Collectors.toMap(ApiParam::getFieldMapNm, ApiParam::getValue));
            log.trace("api.getApiResponseParams() :{}", api.getApiResponseParams().toString());

            api.getApiResponseParams().forEach(resApi -> {
                log.trace("응답 : {} <- {} = {}", resApi.getFieldNm(), resApi.getFieldMapNm(), this.getNodeValue(nodes, resApi.getFieldMapNm()));
                result.put(resApi.getFieldNm(), this.getNodeValue(nodes, resApi.getFieldMapNm()));
            });

            log.trace("result {} ", result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            soapConnection.close();
        } finally {
            soapConnection.close();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ObjectMapper().writeValueAsString(result));
    }

    /**
     * 응답값 세팅
     * @param nodes
     * @param fieldMapNm
     * @return
     */
    private Object getNodeValue(NodeList nodes, String fieldMapNm) {
        for(int i=0; i<nodes.getLength();i++) {
            if(StrUtils.getStr(nodes.item(i).getLocalName()).equals(fieldMapNm)) {
                return StrUtils.getStr(nodes.item(i).getChildNodes().item(0).getNodeValue());
            }
        }
        return "";
    }
}

