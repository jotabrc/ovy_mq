package io.github.jotabrc.ovy_mq.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "broker")
public class EndPointsConfig {

    private List<String> endpoints;
}
