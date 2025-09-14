package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.config.CredentialConfig;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@Component
public class SecurityHandler {

    private static final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
    private final CredentialConfig credential;

    public static void main(String[] args) {
        System.out.println(bcryptEncoder.encode("1234"));
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
        return Map.of("Authorization", createBasic() + ":server");
    }

    private String createBasic() {
        return Base64.getEncoder().encodeToString(("Basic " + credential.getBcrypt()).getBytes());
    }
}
