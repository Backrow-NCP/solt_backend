package org.backrow.solt.service.plan;

import org.backrow.solt.domain.plan.Theme;
import org.backrow.solt.dto.plan.ThemeDTO;
import org.backrow.solt.repository.plan.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThemeStore {

    @Autowired
    private ThemeRepository themeRepository;

    public ThemeDTO getThemeById(Long themeId) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("테마를 찾을 수 없습니다."));
        return convertToDTO(theme);
    }

    public List<ThemeDTO> getAllThemes() {
        return themeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ThemeDTO convertToDTO(Theme theme) {
        return ThemeDTO.builder()
                .themeId(theme.getThemeId())
                .name(theme.getName())
                .build();
    }

    private Theme convertToEntity(ThemeDTO themeDTO) {
        return Theme.builder()
                .themeId(themeDTO.getThemeId())
                .name(themeDTO.getName())
                .build();
    }
}
