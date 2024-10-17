package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.personality.PersonalityTest;
import org.backrow.solt.domain.personality.Result;
import org.backrow.solt.dto.personality.*;
import org.backrow.solt.repository.PersonalityTestResultRepository;
import org.backrow.solt.repository.PersonalityTestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class PersonalityTestServiceImpl implements PersonalityTestService {
    private final PersonalityTestRepository personalityTestRepository;
    private final ModelMapper modelMapper;
    private final PersonalityTestResultRepository personalityTestResultRepository;


    @Override
    public PersonalityTestDTO getPersonalityTestById(int id) {
        PersonalityTest personalityTest = personalityTestRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Test not found..." + id));
        return modelMapper.map(personalityTest, PersonalityTestDTO.class);
    }

    @Override
    public ResultDTO getResult(List<ScoreDTO> dtoList) {
        String key = createKeyFromScoreDTO(dtoList);
        int resultId = mapKeyToResultId(key);

        Result result = personalityTestResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found... " + resultId));

        ResultDTO resultDTO = modelMapper.map(result, ResultDTO.class);

        // matchSeasoning과 misMatchSeasoning 값을 사용해 PersonalityMatchDTO 설정
        resultDTO.setMatchPersonality(getPersonalityMatchDTO(result.getMatchSeasoning()));
        resultDTO.setMisMatchPersonality(getPersonalityMatchDTO(result.getMisMatchSeasoning()));

        return resultDTO;
    }

    // 공통 메서드로 분리하여 중복 제거
    private PersonalityMatchDTO getPersonalityMatchDTO(int seasoningId) {
        Result seasoningResult = personalityTestResultRepository.findById(seasoningId)
                .orElseThrow(() -> new RuntimeException("Seasoning Result not found... " + seasoningId));
        return modelMapper.map(seasoningResult, PersonalityMatchDTO.class);
    }


    private String createKeyFromScoreDTO(List<ScoreDTO> dtoList) {
        // type과 score를 맵핑할 수 있도록 Map 생성
        Map<String, Integer> scoreMap = new HashMap<>();

        for (ScoreDTO scoreDTO : dtoList) {
            scoreMap.put(scoreDTO.getType(), scoreDTO.getScore());
        }

        // 모든 필요한 type에 대해 키 생성
        StringBuilder keyBuilder = new StringBuilder();
        String[] types = {"i", "s", "t", "j"};

        for (String type : types) {
            Integer score = scoreMap.getOrDefault(type, 0);  // type이 없는 경우 기본값 0 사용
            String scoreSign = score >= 0 ? "P" : "N";
            keyBuilder.append(type).append(scoreSign).append("-");
        }

        // 마지막 "-" 제거
        return keyBuilder.toString().replaceAll("-$", "");
    }


//    private String createKeyFromScoreDTO(List<ScoreDTO> dtoList) {
//
//        StringBuilder keyBuilder = new StringBuilder();
//
//
//        for (ScoreDTO scoreDTO : dtoList) {
//            String type = scoreDTO.getType();
//            int score = scoreDTO.getScore();
//
//            String scoreSign = score >= 0 ? "P" : "N";
//
//            keyBuilder.append(type).append(scoreSign).append("-");
//        }
//        // 마지막 "-" 제거
//        return keyBuilder.toString().replaceAll("-$", "");
//    }


    // Key를 기반으로 resultId 매핑
    private int mapKeyToResultId(String key) {
        switch (key) {
            case "iP-sP-tP-jP":
                return 1;
            case "iP-sP-tN-jP":
                return 2;
            case "iP-sN-tN-jP":
                return 3;
            case "iP-sN-tP-jP":
                return 4;
            case "iP-sP-tP-jN":
                return 5;
            case "iP-sP-tN-jN":
                return 6;
            case "iP-sN-tN-jN":
                return 7;
            case "iP-sN-tP-jN":
                return 8;
            case "iN-sP-tP-jN":
                return 9;
            case "iN-sP-tN-jN":
                return 10;
            case "iN-sN-tN-jN":
                return 11;
            case "iN-sN-tP-jN":
                return 12;
            case "iN-sP-tP-jP":
                return 13;
            case "iN-sP-tN-jP":
                return 14;
            case "iN-sN-tN-jP":
                return 15;
            case "iN-sN-tP-jP":
                return 16;
            default:
                throw new IllegalArgumentException("Invalid score combination: " + key);
        }
    }
}
