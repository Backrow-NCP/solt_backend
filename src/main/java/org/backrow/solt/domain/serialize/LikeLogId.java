package org.backrow.solt.domain.serialize;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LikeLogId implements Serializable {
    private Long board;
    private Long member;
}
