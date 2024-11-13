package org.backrow.solt.service.plan.recommendation;

import org.backrow.solt.dto.plan.ThemeDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class ThemeFetcher {

    private final ThemeFetcher themeFetcher;

    public ThemeFetcher(ThemeFetcher themeFetcher) {
        this.themeFetcher = themeFetcher;
    }

    public Set<ThemeDTO> fetchThemes(Set<Long> themeIds) {
        return themeIds.stream()
                .map(themeId -> {
                    ThemeDTO themeDTO = themeFetcher.getThemeById(themeId);
                    return themeDTO != null ? themeDTO : ThemeDTO.builder().themeId(themeId).name("알 수 없는 테마").build();
                })
                .collect(Collectors.toSet());
    }
}
