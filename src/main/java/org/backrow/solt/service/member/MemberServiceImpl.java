package org.backrow.solt.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.member.Member;
import org.backrow.solt.dto.file.UploadResultDTO;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.member.ModifyDTO;
import org.backrow.solt.dto.member.ModifyPasswordDTO;
import org.backrow.solt.repository.member.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Override
    public MemberInfoDTO getMemberInfo(long memberId) {
        Optional<Member> result = memberRepository.findById(memberId);
        Member member = result.orElseThrow();
        return (MemberInfoDTO)modelMapper.map(member, MemberInfoDTO.class);
    }

    @Override
    public void  modifyMember(ModifyDTO modifyDTO) {
        Optional<Member> result = memberRepository.findById(modifyDTO.getMemberId());
        Member member = result.orElseThrow();
        member.changeMemberName(modifyDTO.getName());
        if(modifyDTO.getPassword() != null && !modifyDTO.getPassword().isEmpty()) {
            member.changeMemberPassword(modifyDTO.getPassword());
        } else {
            memberRepository.save(member);
        }
        memberRepository.save(member);
    }

    @Override
    public void deleteMember(long memberId) {
       Member member = memberRepository.findById(memberId).orElseThrow();
            memberRepository.save(member.deleteMember());
    }

    @Override
    public void modifyMemberImage(long memberId, UploadResultDTO uploadResultDTO) {
        Optional<Member> result = memberRepository.findById(memberId);
        Member member = result.orElseThrow();
        member.setProfileImage(uploadResultDTO.getFileName());
        memberRepository.save(member);
    }

    @Override
    public void deleteMemberImage(long memberId) {
        Optional<Member> result = memberRepository.findById(memberId);
        Member member = result.orElseThrow();
        member.deleteProfileImage();
        memberRepository.save(member);
    }

    @Override
    public void modifyPassword(ModifyPasswordDTO modifyPasswordDTO) {
        Optional<Member> result = memberRepository.findByEmail(modifyPasswordDTO.getEmail());
        Member member = result.orElseThrow();
        member.changeMemberPassword(modifyPasswordDTO.getPassword());
        memberRepository.save(member);
    }
}
