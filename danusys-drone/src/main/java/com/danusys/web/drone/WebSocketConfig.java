package com.danusys.web.drone;

import com.danusys.web.drone.config.AgentWebSocketHandlerDecoratorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {



	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

			//TODO setClientLibraryUrl 수정해야됨
		registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS()
				.setClientLibraryUrl("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.min.js");;

	}


	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.setApplicationDestinationPrefixes("/app");
		config.enableSimpleBroker("/topic");
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		//registration.setSendTimeLimit(15 * 100000).setSendBufferSizeLimit(512 * 102400).setMessageSizeLimit(128 * 102400);
		registration.setDecoratorFactories(new AgentWebSocketHandlerDecoratorFactory());
	}

}
