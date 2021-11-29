package com.danusys.web.commons.api.service.executor;

import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.model.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/26
 * Time : 3:20 오후
 */
@Slf4j
@Service("MAV_TCP_SOCKET")
public class MavTcpSocketApiExecutor implements ApiExecutor  {


    @Override
    public ResponseEntity execute(final Api api) throws Exception {
        if (api == null)
            throw new IllegalArgumentException("입력된 API 파라미터 값이 null 입니다.");
        if (api.getCallUrl() == null)
            throw new IllegalArgumentException("호출 URL 값이 null 입니다.");
        if (api.getApiRequestParams() == null || api.getApiRequestParams().isEmpty())
            throw new IllegalArgumentException("apiRequestParams 값이 null 입니다.");

        final List<ApiParam> apiRequestParams = api.getApiRequestParams().stream().sorted(Comparator.comparing((ApiParam p) -> p.getSeq())).collect(Collectors.toList());
        log.trace("### apiRequestParams : {}", apiRequestParams.toString());

//        final Map<String, Object> paramMap = new HashMap<>();
//        MultiValueMap<String, String> reqMap = new LinkedMultiValueMap<>();
//        for(ApiRequestParam apiRequestParam : api.getApiRequestParams()){
//            paramMap.put(apiRequestParam.getName(), apiRequestParam.getValue().get());
//            reqMap.add(apiRequestParam.getName(), apiRequestParam.getValue().toString());
//        }
//
//        log.trace("TCP/IP Address:{}, TCP/IP Port:{}, 파라미터:{}", api.getTcpIpAddress(), api.getTcpIpPort(), paramMap);
//
//        // 호스트를 정의합니다.
//        final String HOST = api.getTcpIpAddress();
//        // 접속할 포트를 정의합니다.
//        final int PORT = api.getTcpIpPort();
//
//        paramMap.put("_API_NAME", api.getUrl());
//        final String json = objectMapper.writeValueAsString(paramMap);
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//
//        log.trace("is isConTest : {}", api.isConnectionTest());
//
//        if(api.isConnectionTest()){
//            return portCheck(api.getTcpIpAddress(), api.getTcpIpPort());
//        }
//
//
//        String finalMessageFromServer = soctetClient(HOST, PORT, json);
//
//        stopWatch.stop();
//
//        log.trace("finalMessageFromServer {} :",finalMessageFromServer);
//
//        /**
//         * API OUTPUT DATA
//         * boolean isLog, isEnc, encKey logSn, String url, String LogUsrId, String inputData, String outputData
//         */
//        /*
//         * proporties true 일때만 DB를 보며
//         * true일 경우 getInOutLogYn을 확인 하여 true경우 로그를 생성하지 않는다.
//         */
//        apiInOutLogService.apiInOutLogCreater(api.getInOutLogYn(), encKey, api.getTransactionId().get(), ApiType.TCP_SOCKET, api.getUrl(),"MCARE-QAB", json, finalMessageFromServer, stopWatch.getTotalTimeMillis());
//
//        if(finalMessageFromServer== "" ){
//            return CompletableFuture.completedFuture(JsonResponse.builder()
//                    .bodyType(JsonResponse.BODY_TYPE_OBJECT)
//                    .body("ERROR")
//                    .build());
//        }
//
//
//        Map<String, Object> map = new HashMap<String, Object>();
//
//        Map<String, Object> bodyMap = objectMapper.readValue(finalMessageFromServer, Map.class);
//
//        String status = bodyMap.get("RESULT").toString();
//
//        log.trace("bodyMap:{}", bodyMap);
//
//        if("EMPTY".equals(status)){
//            List<Map<String, String>> finalList = new ArrayList<>();
//
//            return CompletableFuture.completedFuture(JsonResponse.builder()
//                    .bodyType(isArray ? JsonResponse.BODY_TYPE_ARRAY : JsonResponse.BODY_TYPE_OBJECT)
//                    .body(isArray ? finalList : finalList.isEmpty() ? new HashMap() : finalList.get(0))
//                    .build());
//        }else{
//            String str = (String) bodyMap.get("DATA");
//
//            List<Map<String, String>> list = objectMapper.readValue(str, List.class);
//
//            List<ApiResponseParam> params = new ArrayList(api.getApiResponseParams());
//            log.trace("정렬전 params:{}", params);
//            params.sort(Comparator.comparing(ApiResponseParam::getSeq));
//            log.trace("정렬후 params:{}", params);
//
//            List<Map<String, String>> finalList = new ArrayList<>();
//
//            log.trace("정렬후 params size:{}", params.size());
//
//            if(params.size()>0){
//                for(int i=0;i<list.size();i++){
//                    final Map<String, String> row = new LinkedHashMap<>();
//                    Map<String, String> dataMap = list.get(i);
//
//                    for (ApiResponseParam responseParam : params) {
//                        String alias = responseParam.getAlias();
//                        row.put(alias, dataMap.get(alias));
//                    }
//                    finalList.add(row);
//                }
//            }else{
//                finalList = list;
//            }
//
//            return CompletableFuture.completedFuture(JsonResponse.builder()
//                    .bodyType(isArray ? JsonResponse.BODY_TYPE_ARRAY : JsonResponse.BODY_TYPE_OBJECT)
//                    .body(isArray ? finalList : finalList.isEmpty() ? new HashMap() : finalList.get(0))
//                    .build());
//
//        }
        return null;


    }

    public String soctetClient(String ip, int port, String json) throws Exception {
        String str ="";
        try {
            Socket s = new Socket(ip, port);

            OutputStream os = s.getOutputStream();
            InputStream is = s.getInputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String dataEof = "<_DATA_EOF_>";
            String jsonStr = json+dataEof;
            log.trace("jsonStr: " + jsonStr);

            bw.write(jsonStr);
            bw.flush();

            String messageFromServer;
            while ((messageFromServer = br.readLine()) != null) {
                log.trace(messageFromServer);
                str += messageFromServer;
            }

            str = str.replace(dataEof,"");

        } catch (UnknownHostException e) {
            throw new UnknownHostException("[경고] 서버를 찾을 수 없습니다." + e);
        } catch (IOException e) {
            throw new IOException("[경고] 사용되지 않는 PORT 번호 입니다 " + e);
        } catch (Exception e) {
            throw new Exception("[경고] Exception " + e);
        }

        log.trace("str2: " + str);

        return str;
    }


//    public CompletableFuture<JsonResponse>  portCheck (String ip, int port) throws IOException {
//
//        CompletableFuture<JsonResponse> completableFuture = new CompletableFuture<>();
//        String body = "";
//        JSONObject jsonObjectMain = new JSONObject();
//        JSONObject jsonObjectBody = new JSONObject();
//
//        int timeout = 30000;
//        try {
//            SocketAddress socketAddress = new InetSocketAddress(ip, port);
//            Socket socket = new Socket();
////            Socket socket = new Socket(ip, port);
//            socket.setSoTimeout(timeout);
//            socket.connect(socketAddress, timeout);
//
//            boolean connected = socket.isConnected() && ! socket.isClosed();
//
//            if (connected){
//                socket.close();
//
//                jsonObjectBody.put(RETURN_CD, SUCCESS_CD);
//                jsonObjectBody.put(RETURN_MSG, "Connection Success");
//                jsonObjectMain.put(bodyName, jsonObjectBody);
//
//                body = jsonObjectMain.toJSONString();
//
//            }else{
//                jsonObjectBody.put(RETURN_CD, ERROR_CD);
//                jsonObjectBody.put(RETURN_MSG, "Not connected");
//                jsonObjectMain.put(bodyName, jsonObjectBody);
//                body = jsonObjectMain.toJSONString();
//            }
//        }catch (Exception e){
//            jsonObjectBody.put(RETURN_CD, ERROR_CD);
//            jsonObjectBody.put(RETURN_MSG, e.getMessage());
//            jsonObjectMain.put(bodyName, jsonObjectBody);
//
//            body = jsonObjectMain.toJSONString();
//        }finally {
//            try {
//                SimpleModule module = new SimpleModule();
//                module.addDeserializer(JsonResponse.class, new JsonResponseDeserialize(bodyName));
//                objectMapper.registerModule(module);
//                JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
//                jsonResponse.setBodyType(JsonResponse.BODY_TYPE_OBJECT);
//                completableFuture.complete(jsonResponse);
//            } catch (Exception e) {
//                completableFuture.completeExceptionally(e);
//            }
//        }
//
//        return completableFuture;
//    }

}
