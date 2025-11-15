package io.github.jotabrc.ovy_mq_core.security.interfaces;

import java.util.List;
import java.util.Map;

public interface SecurityResolver {

    Map<String, List<String>> create(String clientType);
    Map<String, String> createSimple(String clientType);
}
