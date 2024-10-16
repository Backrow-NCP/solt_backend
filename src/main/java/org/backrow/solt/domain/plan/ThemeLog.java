package org.backrow.solt.domain.plan;

import lombok.*;
import org.backrow.solt.domain.plan.serialize.ThemeLogId;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@IdClass(ThemeLogId.class)
@NoArgsConstructor
@AllArgsConstructor
public class ThemeLog {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Override
    public String toString() {
        return "ThemeLog [theme=" + (theme == null ? null : "id." + theme.getThemeId())
                + ", plan=" + (plan == null ? null : "id." + plan.getPlanId()) + "]";
    }
}
