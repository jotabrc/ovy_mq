package io.github.jotabrc.ovy_mq_client.test;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "test")
public class TestObj {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String objeto;
}
