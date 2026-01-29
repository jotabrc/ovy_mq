package io.github.jotabrc.ovy_mq_core.domain.client.listener_config;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Replica implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer quantity;
    private Integer max;
    private Integer min;
    private Integer step;
    private Boolean autoManage;
}
