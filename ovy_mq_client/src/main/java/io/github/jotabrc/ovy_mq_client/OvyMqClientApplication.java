package io.github.jotabrc.ovy_mq_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class OvyMqClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(OvyMqClientApplication.class, args);
	}

}
