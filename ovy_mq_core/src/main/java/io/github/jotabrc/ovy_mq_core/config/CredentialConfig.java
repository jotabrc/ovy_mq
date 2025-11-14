package io.github.jotabrc.ovy_mq_core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "application.credential")
public class CredentialConfig {

    private String bcrypt;
}
