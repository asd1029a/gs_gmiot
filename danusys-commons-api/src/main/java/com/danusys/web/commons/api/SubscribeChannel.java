package com.danusys.web.commons.api;

import com.danusys.web.commons.app.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/04/14
 * Time : 10:35
 */
@Slf4j
public class SubscribeChannel {
    private EmitterProcessor<String> processor;
    private Flux<String> flux;
    private FluxSink<String> sink;
    private Runnable closeCallback;

    public SubscribeChannel() {
        processor = EmitterProcessor.create();
        this.sink = processor.sink();
        this.flux = processor
                .doOnCancel(() -> {
                    log.info("doOnCancle, downstream : " + processor.downstreamCount());
                    if(processor.downstreamCount() == 1) close();
                })
                .doOnTerminate(() -> {
                    log.info("donOnterminate, downstream : " + processor.downstreamCount());
                })
                .doOnComplete(() -> {
                    log.info("Complete");
                });

    }

    public void send(Map<String, Object> param) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(param);
            //발행 시작
            sink.next(jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Flux<String> toFlux() {
        return flux;
    }

    private void close() {
        if (closeCallback != null) closeCallback.run();
        sink.complete();
    }

    public SubscribeChannel onClose(Runnable closeCallback) {
        this.closeCallback = closeCallback;
        return this;
    }
}
