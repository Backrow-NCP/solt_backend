package org.backrow.solt.service;

import org.backrow.solt.domain.Member;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.member.ModifyDTO;
import org.backrow.solt.dto.file.UploadResultDTO;

public interface MemberService {

    MemberInfoDTO getMemberInfo(long memberId);

    void modifyMember(ModifyDTO modifyDTO);

    void deleteMember(long memberId);

    void modifyMemberImage(long memberId, UploadResultDTO uploadResultDTO);

    void deleteMemberImage(long memberId);

    default  MemberInfoDTO entityToDto(Member member) {
        MemberInfoDTO memberInfoDTO = MemberInfoDTO.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .birthYear(member.getBirthYear())
                .gender(member.getGender())
                .build();

        if(member.getProfileImage() != null) {
            String fileName = member.getProfileImage().getFileName();
            memberInfoDTO.setFileName(fileName);
        }
        return memberInfoDTO;
    }
}
