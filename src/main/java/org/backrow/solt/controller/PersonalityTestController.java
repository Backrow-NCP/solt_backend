package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.personality.*;
import org.backrow.solt.service.PersonalityTestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "유형검사 API", description = "유형검사를 시행하는 API입니다.")
@RestController
@RequestMapping("/personalityTest")
@RequiredArgsConstructor
@Log4j2
public class PersonalityTestController {

    private final PersonalityTestService personalityTestService;

    @Operation(summary = "유형 검사 정보 가져오기", description = "유형 검사 질문, 답변정보를 가져옵니다")
    @GetMapping("/getTest/{id}")
    public PersonalityTestDTO getPersonalityTestById(@PathVariable int id) {
        return personalityTestService.getPersonalityTestById(id);
    }

    @Operation(summary = "유형검사 결과 가져오기", description = "합산된 score JSon정보를 받아서 유형 검사 결과 정보를 가져옵니다")
    @PostMapping("/result")
    public ResultDTO getResult(@RequestBody List<ScoreDTO> dtoList) {
        return personalityTestService.getResult(dtoList);
    }
}
