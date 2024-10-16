package org.backrow.solt.domain.plan.serialize;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ThemeLogId implements Serializable {
    private Long theme;
    private Long plan;
}
