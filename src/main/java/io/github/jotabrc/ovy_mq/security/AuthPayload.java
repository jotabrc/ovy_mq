package io.github.jotabrc.ovy_mq.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthPayload {

    private String basic;
    private String clientName;
}
