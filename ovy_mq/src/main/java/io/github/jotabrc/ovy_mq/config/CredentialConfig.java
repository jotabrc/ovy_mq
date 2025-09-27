package io.github.jotabrc.ovy_mq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.credential")
public class CredentialConfig {

    private String bcrypt;
}
