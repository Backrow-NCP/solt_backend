package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.personality.*;
import org.backrow.solt.service.personality.PersonalityTestLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "유형검사 기록 API", description = "유형검사 기록에 대한 조회, 삭제 기능을 수행하는 API입니다.")
@RestController
@RequestMapping("/personalityTestLog")
@RequiredArgsConstructor
@Log4j2
public class PersonalityTestLogController {
    private final PersonalityTestLogService personalityTestLogService;

    @Operation(summary = "유형검사 결과 저장", description = "완료한 유형검사 결과를 저장합니다.")
    @PostMapping
    public ResponseEntity<Void> saveLog(@RequestBody PersonalityTestLogInputDTO personalityTestLogInputDTO) {
        personalityTestLogService.saveLog(personalityTestLogInputDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유형검사 결과 목록 조회", description = "사용자가 실시한 유형검사 결과 목록을 조회합니다.")
    @GetMapping("/list")
    @Transactional
    public ResponseEntity<List<PersonalityTestLogViewDTO>> getLogList(Long memberId) {
        return ResponseEntity.ok().body(personalityTestLogService.getLogList(memberId));
    }

//    @Operation(summary = "유형검사 결과 상세 조회", description = "ID를 통해 특정 유형검사 결과를 조회합니다.")
//    @GetMapping("/{id}")
//    public ResponseEntity<SurveyLogViewDTO> getLog(@PathVariable Long surveyLogId) {
//        return null;
//    }

    @Operation(summary = "유형검사 결과  삭제", description = "ID를 통해 유형검사 결과를 삭제합니다.")
    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteLog(@PathVariable Long id) {
        return Map.of("isSuccess", personalityTestLogService.deleteLog(id));

    }
}
