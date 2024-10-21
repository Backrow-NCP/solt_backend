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
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class PersonalityTestServiceImpl implements PersonalityTestService {
    private final PersonalityTestRepository personalityTestRepository;
    private final ModelMapper modelMapper;
    private final PersonalityTestResultRepository personalityTestResultRepository;

    private final Map<String, Integer> resultKeyMap = initializeResultKeyMap();


    @Override
    public PersonalityTestDTO getPersonalityTestById(int id) {
        PersonalityTest personalityTest = personalityTestRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Test not found..." + id));
        return modelMapper.map(personalityTest, PersonalityTestDTO.class);
    }

    @Override
    public ResultDTO getResultById(int id) {
        Result result = findResultById(id);
        return mapResultToDTO(result);
    }

    @Override
    public ResultDTO getResult(List<ScoreDTO> dtoList) {
        String key = createKeyFromScoreDTO(dtoList);
        int resultId = mapKeyToResultId(key);

        Result result = findResultById(resultId);
        return mapResultToDTO(result);
    }

    @Override
    public List<ResultAllDTO> getAllResults() {
        List<Result> results = personalityTestResultRepository.findAll();
        return results.stream()
                .map(result -> modelMapper.map(result, ResultAllDTO.class))
                .collect(Collectors.toList());
    }

    // 공통 로직 메서드로 분리하여 재사용성 향상
    private Result findResultById(int id) {
        return personalityTestResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found... " + id));
    }

    private ResultDTO mapResultToDTO(Result result) {
        ResultDTO resultDTO = modelMapper.map(result, ResultDTO.class);
        // matchSeasoning과 misMatchSeasoning 값을 사용해 PersonalityMatchDTO 설정
        resultDTO.setMatchPersonality(getPersonalityMatchDTO(result.getMatchSeasoning()));
        resultDTO.setMisMatchPersonality(getPersonalityMatchDTO(result.getMisMatchSeasoning()));
        return resultDTO;
    }

    private PersonalityMatchDTO getPersonalityMatchDTO(int seasoningId) {
        Result seasoningResult = personalityTestResultRepository.findById(seasoningId)
                .orElseThrow(() -> new RuntimeException("Seasoning Result not found... " + seasoningId));
        return modelMapper.map(seasoningResult, PersonalityMatchDTO.class);
    }


    private String createKeyFromScoreDTO(List<ScoreDTO> dtoList) {
        // type과 score를 맵핑할 수 있도록 Map 생성
        Map<String, Integer> scoreMap = dtoList.stream()
                .collect(Collectors.toMap(ScoreDTO::getType, ScoreDTO::getScore));

        // 필요한 type에 대해 키 생성
        List<String> types = Arrays.asList("i", "s", "t", "j");
        return types.stream()
                .map(type -> type + (scoreMap.getOrDefault(type, 0) >= 0 ? "P" : "N"))
                .collect(Collectors.joining("-"));
    }

    private int mapKeyToResultId(String key) {
        return Optional.ofNullable(resultKeyMap.get(key))
                .orElseThrow(() -> new IllegalArgumentException("Invalid score combination: " + key));
    }

    // 초기화 시점에 Key-ResultId 맵핑 설정
    private static Map<String, Integer> initializeResultKeyMap() {
        Map<String, Integer> keyMap = new HashMap<>();
        keyMap.put("iP-sP-tP-jP", 1);
        keyMap.put("iP-sP-tN-jP", 2);
        keyMap.put("iP-sN-tN-jP", 3);
        keyMap.put("iP-sN-tP-jP", 4);
        keyMap.put("iP-sP-tP-jN", 5);
        keyMap.put("iP-sP-tN-jN", 6);
        keyMap.put("iP-sN-tN-jN", 7);
        keyMap.put("iP-sN-tP-jN", 8);
        keyMap.put("iN-sP-tP-jN", 9);
        keyMap.put("iN-sP-tN-jN", 10);
        keyMap.put("iN-sN-tN-jN", 11);
        keyMap.put("iN-sN-tP-jN", 12);
        keyMap.put("iN-sP-tP-jP", 13);
        keyMap.put("iN-sP-tN-jP", 14);
        keyMap.put("iN-sN-tN-jP", 15);
        keyMap.put("iN-sN-tP-jP", 16);
        return keyMap;
    }
}
