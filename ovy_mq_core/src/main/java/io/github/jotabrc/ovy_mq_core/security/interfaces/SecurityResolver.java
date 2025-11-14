package io.github.jotabrc.ovy_mq_core.security.interfaces;

import java.util.List;
import java.util.Map;

public interface SecurityResolver {

    <T> Map<String, List<String>> create(String clientType);
}
