package io.github.jotabrc.ovy_mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableWebSocketMessageBroker
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class OvyMqApplication {

	public static void main(String[] args) {
		SpringApplication.run(OvyMqApplication.class, args);
	}

}
