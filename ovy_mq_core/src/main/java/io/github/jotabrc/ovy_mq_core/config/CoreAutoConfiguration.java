package io.github.jotabrc.ovy_mq_core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io.github.jotabrc.ovy_mq_core")
@EnableConfigurationProperties(CredentialConfig.class)
public class CoreAutoConfiguration {
}
