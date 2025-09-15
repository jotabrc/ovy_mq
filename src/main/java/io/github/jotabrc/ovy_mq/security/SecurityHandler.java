package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.config.CredentialConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class SecurityHandler {

    private final CredentialConfig credential;
    private static final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
    private String basic;

    public SecurityHandler(CredentialConfig credential) {
        this.credential = credential;
    }

    @PostConstruct
    private void setBasic() {
        this.basic = Base64.getEncoder().encodeToString(("Basic " + credential.getBcrypt() + ":server").getBytes());
    }

    String[] retrieveCredentials(String basic) {
        if (isNull(basic) || basic.isBlank()) return new String[0];
        return getString(basic).split(":");
    }

    private String getString(String basic) {
        return new String(getDecodedBasic(basic)).replace("Basic ", "");
    }

    private byte[] getDecodedBasic(String basic) {
        return Base64.getDecoder().decode(basic);
    }

    boolean hasCredentials(String[] credentials) {
        return nonNull(credentials) && Objects.equals(2, credentials.length);
    }

    boolean validate(String password) {
        return bcryptEncoder.matches(password, credential.getBcrypt());
    }

    public Map<String, Object> createAuthorizationHeader() {
        return Map.of("Authorization", getBasic());
    }

    private String getBasic() {
        return this.basic;
    }

    public static void main(String[] args) {
        System.out.println(bcryptEncoder.encode("1234"));
    }
}
