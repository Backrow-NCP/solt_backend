package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.Member;
import org.backrow.solt.dto.file.UploadResultDTO;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.member.ModifyDTO;
import org.backrow.solt.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public MemberInfoDTO getMemberInfo(long memberId) {
        Optional<Member> result = memberRepository.findById(memberId);
        Member member = result.orElseThrow();
        return entityToDto(member);
    }

    @Override
    public void  modifyMember(ModifyDTO modifyDTO) {
        Optional<Member> result = memberRepository.findById(modifyDTO.getMemberId());
        Member member = result.orElseThrow();

        member.changeMemberInfo(modifyDTO.getPassword(), modifyDTO.getName(), modifyDTO.getBirthYear());

        memberRepository.save(member);
    }

    @Override
    public void deleteMember(long memberId, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
       Member member = memberRepository.findById(memberId).orElseThrow();
       boolean matches = encoder.matches(password, member.getPassword());
        if(matches) {
            memberRepository.deleteById(memberId);
        } else {
            throw new RuntimeException("password does not match");
        }
    }

    @Override
    public void modifyMemberImage(long memberId, UploadResultDTO uploadResultDTO) {
        Optional<Member> result = memberRepository.findById(memberId);
        Member member = result.orElseThrow();

        member.addImage(uploadResultDTO.getUuid(), uploadResultDTO.getFileName());
        memberRepository.save(member);
    }
}
