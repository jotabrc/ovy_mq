package io.github.jotabrc.ovy_mq.security.handler;

import io.github.jotabrc.ovy_mq.config.CredentialConfig;
import io.github.jotabrc.ovy_mq.security.SecurityChainType;
import io.github.jotabrc.ovy_mq.security.handler.interfaces.AuthHandler;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class AuthBasicHandler implements AuthHandler {

    private final CredentialConfig credential;
    private static final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
    private String basic;

    public AuthBasicHandler(CredentialConfig credential) {
        this.credential = credential;
    }

    @PostConstruct
    private void setBasic() {
        this.basic = "Basic " + Base64.getEncoder().encodeToString(credential.getBcrypt().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String retrieveCredentials(String basic) {
        if (isNull(basic) || basic.isBlank()) return null;
        return getString(basic);
    }

    private String getString(String basic) {
        return new String(getDecodedBasic(basic));
    }

    private byte[] getDecodedBasic(String basic) {
        return Base64.getDecoder().decode(basic.replace("Basic ", ""));
    }

    @Override
    public boolean hasCredentials(String credential) {
        return nonNull(credential) && !credential.isBlank();
    }

    @Override
    public boolean validate(String password) {
        return bcryptEncoder.matches(password, credential.getBcrypt());
    }

    @Override
    public Map<String, Object> createAuthorizationHeader() {
        return Map.of(Key.HEADER_AUTHORIZATION, getBasic());
    }

    private String getBasic() {
        return this.basic;
    }

    @Override
    public SecurityChainType supports() {
        return SecurityChainType.AUTH_BASE64;
    }

    public static void main(String[] args) {
        System.out.println("Basic " + Base64.getEncoder().encodeToString(("$2a$10$FewIB0NHzGc/RexhYkk1eepP0xsUC77UQilUFFz.LbNZFoUQ2N99a").getBytes(StandardCharsets.UTF_8)));
    }
    // TODO remove later
}
