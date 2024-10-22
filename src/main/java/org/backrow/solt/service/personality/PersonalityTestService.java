package org.backrow.solt.service.personality;

import org.backrow.solt.dto.personality.*;

import java.util.List;

public interface PersonalityTestService {
    PersonalityTestDTO getPersonalityTestById(int id);
    ResultDTO getResult(List<ScoreDTO> dtoList);
    List<ResultAllDTO> getAllResults();
    ResultDTO getResultById(int id);
}
