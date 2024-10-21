package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.personality.*;
import org.backrow.solt.service.personality.PersonalityTestService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "유형검사 API", description = "유형검사 정보 및 결과를 조회하는 API입니다.")
@RestController
@RequestMapping("/personalityTest")
@RequiredArgsConstructor
@Log4j2
public class PersonalityTestController {
    private final PersonalityTestService personalityTestService;

    @Operation(summary = "유형검사 정보 조회", description = "유형검사 ID를 통해 질문, 답변 정보를 가져옵니다.")
    @GetMapping("/getTest/{id}")
    @Transactional
    public PersonalityTestDTO getPersonalityTestById(@PathVariable int id) {
        return personalityTestService.getPersonalityTestById(id);
    }

    @Operation(summary = "유형검사 결과 계산 및 조회", description = "합산 점수 정보를 통해 유형검사 결과 정보를 가져옵니다.")
    @PostMapping("/result")
    public ResultDTO getResult(@RequestBody List<ScoreDTO> dtoList) {
        return personalityTestService.getResult(dtoList);
    }

    @Operation(summary="유형검사 전체 결과 조회", description = "모든 유형 결과 목록을 가져옵니다.")
    @GetMapping("/getAllResults")
    public List<ResultAllDTO> getAllResults() {
        return personalityTestService.getAllResults();
    }

    @Operation(summary = "유형검사 결과 상세조회", description = "ID를 통해 특정 유형검사 결과를 상세조회합니다.")
    @GetMapping("/result/{id}")
    @Transactional
    public ResultDTO getResultById(@PathVariable int id) {
        return personalityTestService.getResultById(id);
    }
}
