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

    default Member dtoToEntity(MemberInfoDTO memberInfoDTO) {
        Member member = Member.builder()
                .memberId(memberInfoDTO.getMemberId())
                .name(memberInfoDTO.getName())
                .birthYear(memberInfoDTO.getBirthYear())
                .gender(memberInfoDTO.isGender())
                .build();

        if(memberInfoDTO.getFileName() != null){
            String[] arr = memberInfoDTO.getFileName().split("_");
            member.addImage(arr[0],arr[1]);
            }
        return member;
    }

    default  MemberInfoDTO entityToDto(Member member) {
        MemberInfoDTO memberInfoDTO = MemberInfoDTO.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .birthYear(member.getBirthYear())
                .gender(member.getGender())
                .build();

        if(member.getProfileImage() != null) {
            String fileName = member.getProfileImage().getUuid() + "_" +
                    member.getProfileImage().getFileName();

            memberInfoDTO.setFileName(fileName);
        }
        return memberInfoDTO;
    }
}
