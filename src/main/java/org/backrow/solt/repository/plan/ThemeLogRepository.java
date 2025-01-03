package org.backrow.solt.repository.plan;

import org.backrow.solt.domain.plan.ThemeLog;
import org.backrow.solt.domain.plan.serialize.ThemeLogId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeLogRepository extends JpaRepository<ThemeLog, ThemeLogId> {
}
