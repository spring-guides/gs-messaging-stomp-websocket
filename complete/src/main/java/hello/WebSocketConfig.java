package hello;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.EnableWebSocketMessageBroker;
import org.springframework.messaging.simp.config.MessageBrokerConfigurer;
import org.springframework.messaging.simp.config.StompEndpointRegistry;
import org.springframework.messaging.simp.config.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerConfigurer config) {
		config.enableSimpleBroker("/queue/");
		config.setAnnotationMethodDestinationPrefixes("/app");
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/hello").withSockJS();
	}

}