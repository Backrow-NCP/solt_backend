package org.backrow.solt.service;

import org.backrow.solt.dto.personality.*;

import java.util.List;

public interface PersonalityTestLogService {
    void saveLog(PersonalityTestLogInputDTO personalityTestLogInputDTO);
   List<PersonalityTestLogViewDTO> getLogList(Long memberId);
//    SurveyLogViewDTO getLog(Long surveyLogId);
    boolean deleteLog(Long id);

}
