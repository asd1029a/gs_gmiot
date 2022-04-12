package com.danusys.web.commons.api.service.executor;

import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.types.DataType;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
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
                    if (apiReq.getDataType().equals(DataType.ARRAY)) {
                        log.trace(apiReq.getValue());
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<String> values = objectMapper.readValue(apiReq.getValue(), new TypeReference<List<String>>() {});
                        values.forEach(f -> {
                            try {
                                elRequest.addChildElement(apiReq.getFieldMapNm(), webServicePrefix).addTextNode(f);
                            } catch (SOAPException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        elRequest.addChildElement(apiReq.getFieldMapNm(), webServicePrefix).addTextNode(apiReq.getValue());
                    }
                    elRequest.addChildElement(apiReq.getFieldMapNm(), webServicePrefix).addTextNode(apiReq.getValue());
                } catch (SOAPException e) {
//                    e.printStackTrace();
                    log.error("파라미터 세팅 SoapApiExecutor 오류");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // SOAPMessage 를 requestURL 로 전송하고 서버쪽에서 내려보낸 정보가 담긴 SOAPMessage 객체를 얻음
            SOAPMessage responseMessage = soapConnection.call(soapMessage, targetURL);
            log.trace("soapBody : {}", soapBody.getTextContent());
//            soapMessage.writeTo(System.out);
//            responseMessage.writeTo(System.out);

            SOAPBody resBody = responseMessage.getSOAPBody();
            log.trace("response to json : {}", xmlToJsonStr(responseMessage));
            Iterator resBodyChildElements = resBody.getChildElements();
            Node rootNode = (Node) resBodyChildElements.next();

            NodeList nodes = rootNode.getChildNodes();
            int limit = nodes.getLength();
            for (int i = 0; i < limit; i++) {
                Node node = (Node) nodes.item(i);
                log.trace("node to json : {}", xmlToJsonStr(node));
            }
//            NodeList nodes = rootNode.getChildNodes().item(0).getChildNodes();

            //        final Map<String, Object> reqMap = apiRequestParams.stream().collect(Collectors.toMap(ApiParam::getFieldMapNm, ApiParam::getValue));
            log.trace("api.getApiResponseParams() :{}", api.getApiResponseParams().toString());

            List<ApiParam> apiResponseParams = api.getApiResponseParams();

            apiResponseParams.forEach(resApi -> {
                if (resApi.getDataType().equals(DataType.ARRAY)) {
                    List<Map<String, Object>> inner = new ArrayList<>();
                    api.getApiResponseParams().stream().filter(f -> f.getParentSeq() == resApi.getApiId()).forEach(f -> {

                    });
                }
                log.trace("응답 : {} <- {} = {}", resApi.getFieldNm(), resApi.getFieldMapNm(), this.getNodeValue(nodes, resApi.getFieldMapNm()));
                result.put(resApi.getFieldNm(), this.getNodeValue2(nodes, resApi.getFieldMapNm(), resApi, apiResponseParams));
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

    private void createRequestParamElement(List<ApiParam> apiRequestParams) {
        apiRequestParams.stream().filter(f -> f.getDataType().equals(DataType.ARRAY)).peek(f -> {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> list = objectMapper.convertValue(f.getValue(), List.class);
        });
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

    /**
     * 응답값 세팅
     * @param nodes
     * @param fieldMapNm
     * @return
     */
    private Object getNodeValue2(NodeList nodes, String fieldMapNm, ApiParam resApi, List<ApiParam> apiResponseParams) {
        List<Map<String, Object>> result = new ArrayList<>();
        for(int i=0; i<nodes.getLength();i++) {
            if (resApi.getDataType().equals(DataType.ARRAY)) {
                NodeList l = nodes.item(i).getChildNodes();
                for(int j = 0; j < l.getLength(); j++) {
                    String name = l.item(j).getChildNodes().item(0).getLocalName();
                }
            } else {
                if(StrUtils.getStr(nodes.item(i).getLocalName()).equals(fieldMapNm)) {
                    return nodes.item(i).getChildNodes().item(0).getNodeValue();
                }
            }
        }
        return "";
    }

    private String xmlToJsonStr(SOAPMessage message) {
        final StringWriter writer = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(message.getSOAPPart()), new StreamResult(writer));
            String xmlStr = writer.toString();
            return XML.toJSONObject(xmlStr).toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String xmlToJsonStr(Node node) {
        final StringWriter writer = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(node), new StreamResult(writer));
            String xmlStr = writer.toString();
            return XML.toJSONObject(xmlStr).toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return "";
    }
}

