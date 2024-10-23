package org.backrow.solt.service.personality;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.member.Member;
import org.backrow.solt.domain.personality.PersonalityTest;
import org.backrow.solt.domain.personality.PersonalityTestLog;
import org.backrow.solt.domain.personality.Result;
import org.backrow.solt.dto.personality.PersonalityTestLogInputDTO;
import org.backrow.solt.dto.personality.PersonalityTestLogViewDTO;
import org.backrow.solt.repository.personality.PersonalityTestLogRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalityTestLogServiceImpl implements PersonalityTestLogService {
    private final PersonalityTestLogRepository personalityTestLogRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveLog(PersonalityTestLogInputDTO personalityTestLogInputDTO) {
        PersonalityTestLog personalityTestLog = convertToEntity(personalityTestLogInputDTO);
        personalityTestLogRepository.save(personalityTestLog);
        List<PersonalityTestLog> logs = personalityTestLogRepository.findByMemberMemberId(personalityTestLogInputDTO.getMemberId());

        // 로그가 8개를 초과하는 경우 오래된 로그를 삭제
        if (logs.size() > 8) {
            logs.sort((log1, log2) -> log1.getRegDate().compareTo(log2.getRegDate()));
            int logsToRemove = logs.size() - 8;
            for (int i = 0; i < logsToRemove; i++) {
                personalityTestLogRepository.delete(logs.get(i));
            }
        }
    }

    @Override
    public List<PersonalityTestLogViewDTO> getLogList(Long memberId) {
        List<PersonalityTestLog> personalityTestLogs = personalityTestLogRepository.findByMemberMemberId(memberId);

        return personalityTestLogs.stream()
                .map(testLog -> modelMapper.map(testLog, PersonalityTestLogViewDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteLog(Long id) {
        try{
            personalityTestLogRepository.deleteById(id);
            return true;
        } catch(EmptyResultDataAccessException e){
            throw new NotFoundException("PersonalityTestLog Not Found" + id);
        }
    }


    private PersonalityTestLog convertToEntity(PersonalityTestLogInputDTO personalityTestLogInputDTO) {
        PersonalityTestLog personalityTestLog = new PersonalityTestLog();
        personalityTestLog.setLogId(null);

        Member member = Member.builder()
                .memberId(personalityTestLogInputDTO.getMemberId())
                .build();
        personalityTestLog.setMember(member);

        Result result = Result.builder()
                .resultId(personalityTestLogInputDTO.getResultId())
                .build();
        personalityTestLog.setResult(result);

        PersonalityTest personalityTest = PersonalityTest.builder()
                .personalityTestId(personalityTestLogInputDTO.getPersonalityTestId())
                .build();
        personalityTestLog.setPersonalityTest(personalityTest);

        personalityTestLog.setRegDate(personalityTestLogInputDTO.getRegDate());

        return personalityTestLog;

    }
}
