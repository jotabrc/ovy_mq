package io.github.jotabrc.ovy_mq_client.test;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepo extends JpaRepository<TestObj, Long> {
}
