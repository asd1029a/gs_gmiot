package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.SubscribeChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/04/08
 * Time : 13:31
 */

@Slf4j
@Service
public class SseService {
    private ConcurrentHashMap<Long, SubscribeChannel> chMap = new ConcurrentHashMap<>();
    /*private SubscribeChannel subscribeChannel;*/
    //private AtomicInteger id = new AtomicInteger();

    public SubscribeChannel connect(Long userId) {
        return chMap.computeIfAbsent(userId, key -> new SubscribeChannel().onClose(() ->
                chMap.remove(userId)));
    }

    public void send(String jsonStr) {
        //String message = "eventOccurs";
        Optional.ofNullable(chMap).ifPresent(ch -> {
            ch.entrySet().stream().forEach(entry -> entry.getValue().send(jsonStr));
        });
    }

    public Flux<ServerSentEvent<String>> userCheck(Long userId) {
        Flux<String> userStream = this.connect(userId).toFlux();
        return Flux.merge(userStream)
                .map(str -> ServerSentEvent.builder(str).build());
    }
}

