package com.danusys.web.commons.api.service.executor;

import com.danusys.web.commons.api.dto.LogicalfolderDTO;
import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.service.ApiCallService;
import com.danusys.web.commons.api.types.BodyType;
import com.danusys.web.commons.api.types.DataType;
import com.danusys.web.commons.api.util.XmlDataUtil;
import com.danusys.web.commons.app.StrUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.util.*;

import static java.util.stream.Collectors.toList;


/**
 * @author odlodl
 * @since 2020-0-24
 */
@Slf4j
@Service("SOAP")
@RequiredArgsConstructor
public class SoapApiExecutor implements ApiExecutor  {
    private final ApiCallService apiCallService;

    @Override
    public ResponseEntity execute(Api api) throws Exception {
        if (api == null)
            throw new IllegalArgumentException("입력된 API 파라미터 값이 null 입니다.");
        if (api.getCallUrl() == null)
            throw new IllegalArgumentException("호출 URL 값이 null 입니다.");
        if (api.getApiRequestParams() == null || api.getApiRequestParams().isEmpty())
            throw new IllegalArgumentException("apiRequestParams 값이 null 입니다.");

        final List<ApiParam> apiRequestParams = api.getApiRequestParams().stream().sorted(Comparator.comparing((ApiParam p) -> p.getSeq())).collect(toList());
        log.trace("### apiRequestParams : {}", apiRequestParams.toString());

        Object result = "";
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
            List<ApiParam> apiRootParams = apiRequestParams.stream().filter(f -> f.getParentSeq() == 0).collect(toList());

            // 생성한 SOAPBodyElement 에 다시 ChildElement 를 추가하고 각각의 ChildElement 에 TextNode 를 추가
            apiRootParams.forEach(apiReq -> {
                try {
                    if( apiReq.getDataType().equals(DataType.COOKIE)) {
                        String thisColValue = apiCallService.getCookie(apiReq.getValue());
                        apiReq.setValue(thisColValue.isEmpty() ? String.valueOf(apiCallService.getSoapClientId(api)) : thisColValue);
                        elRequest.addChildElement(apiReq.getFieldMapNm()).addTextNode(apiReq.getValue());

                    } else if(apiReq.getDataType() == DataType.SOAP_DATA_PATH) {
                        List<LogicalfolderDTO.Logicalpoints.Lpt> lpts = XmlDataUtil.getGmSoapPostList(apiReq.getValue());
                        List<String> pointPaths = lpts.stream().map(m -> m.getPth()).collect(toList());
                        for(String path : pointPaths) {
                            elRequest.addChildElement(apiReq.getFieldMapNm()).addTextNode("point:" + path);
                        }
                    } else if(apiReq.getDataType() == DataType.ARRAY) { //TODO 1단계 하위 데이터만 처리가능하고, 필요시에 더 하위 목록도 세팅할 수 있도록...
                        int parentSeq = apiReq.getSeq();
                        List<ApiParam> apiSubParams = apiRequestParams.stream().filter(f -> f.getParentSeq() == parentSeq).collect(toList());
                        SOAPElement elSubRequest = elRequest.addChildElement(apiReq.getFieldMapNm());
                        for(ApiParam subParam : apiSubParams) {
                            log.trace("요청 {} sub {} => {} = {} ", apiReq.getFieldMapNm(), subParam.getFieldNm() , subParam.getFieldMapNm(), subParam.getValue());
                            elSubRequest.addChildElement(subParam.getFieldMapNm()).addTextNode(subParam.getValue());
                        }
                    } else {
                        elRequest.addChildElement(apiReq.getFieldMapNm()).addTextNode(apiReq.getValue());
                    }

                    log.trace("요청 : {} -> {} = {}", apiReq.getFieldNm(), apiReq.getFieldMapNm(), apiReq.getValue());

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("파라미터 세팅 SoapApiExecutor 오류");
                }
            });

//            log.trace(":::::요청 xml:::::");
//            soapMessage.writeTo(System.out);
//            System.out.println("\n");

            // SOAPMessage 를 requestURL 로 전송하고 서버쪽에서 내려보낸 정보가 담긴 SOAPMessage 객체를 얻음
            SOAPMessage responseMessage = soapConnection.call(soapMessage, targetURL);

//            log.trace(":::::응답 xml:::::");
//            responseMessage.writeTo(System.out);
//            System.out.println("\n");

            SOAPBody resSoapBody = responseMessage.getSOAPBody();
            NodeList nodes0 = (NodeList) resSoapBody.getChildElements().next();

            if (api.getResponseBodyType() == BodyType.OBJECT_MAPPING) {
                log.trace("### Response 객체 리턴 {}", BodyType.OBJECT_MAPPING);
                Map<String, Object> rootResult = null;//new HashMap<>();
                if( nodes0.item(0).getFirstChild() != null ) {
                    rootResult = this.nodeToMap(0, nodes0, api);
                }
                result = rootResult;
            }

            log.trace("result {} ", result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            soapConnection.close();
        } finally {
            soapConnection.close();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
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
                return StrUtils.getStr(nodes.item(i).getFirstChild().getNodeValue());
            }
        }
        return "";
    }

    /**
     * soap Node를 Map으로 파싱
     * @param seq
     * @param nodes
     * @param api
     * @return
     */
    private Map<String, Object> nodeToMap(int seq, NodeList nodes, Api api) {
        final Map<String, Object> result = new HashMap<>();
        final List<Map<String, Object>> lists = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            final String nodeName = nodes.item(i).getNodeName();
            final String nodeValue = nodes.item(i).getFirstChild().getNodeValue();
            final NodeList childNodes = nodes.item(i).getChildNodes();
            if(nodes.item(i).getFirstChild() != null) {
//                log.trace("{} {} {}", seq, nodeName , nodeValue);
                api.getApiResponseParams().stream().filter(f -> f.getParentSeq() == seq && f.getFieldMapNm().equals(nodeName)).forEach(apiRes -> {
                    log.trace("응답 res{} : {} <- {} = {}", seq, apiRes.getFieldNm(), apiRes.getFieldMapNm(), nodeValue);
                    if( nodeValue != null) {
                        result.put(apiRes.getFieldNm(), nodeValue);
                    } else {
                        if( apiRes.getDataType() == DataType.ARRAY ) {
                            List<Map<String,Object>> resListData = (List<Map<String, Object>>) result.get(apiRes.getFieldNm());
                            if(resListData != null && resListData.size() > 0) {
                                resListData.addAll(this.nodeToList(seq + 1, childNodes, api));
                                result.put(apiRes.getFieldNm(), resListData);
                            } else {
                                result.put(apiRes.getFieldNm(), this.nodeToList(seq + 1, childNodes, api));
                            }
                        } else {
                            result.put(apiRes.getFieldNm(), this.nodeToMap(seq + 1, childNodes, api));
                        }
                    }
                });
            }
        }

        return result;
    }

    /**
     * node 목록 데이터를 list로 파싱
     * @param seq
     * @param nodes
     * @param api
     * @return
     */
    private List<Map<String, Object>> nodeToList(int seq, NodeList nodes, Api api) {
        final List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            final String nodeName = nodes.item(i).getNodeName();
            final String nodeValue = nodes.item(i).getFirstChild().getNodeValue();
            if(nodes.item(i).getFirstChild() != null) {
                for(ApiParam apiParam : api.getApiResponseParams()) {
                    if(apiParam.getParentSeq() == seq && apiParam.getFieldMapNm().equals(nodeName)) {
                        log.trace("응답 res{} : {} <- {} = {}", seq, apiParam.getFieldNm(), apiParam.getFieldMapNm(), nodeValue);
                        data.put(apiParam.getFieldNm(), nodeValue);
                    }
                }
            }
        }
        result.add(data);

        log.trace("result : {}", result);

        return result;
    }
}

