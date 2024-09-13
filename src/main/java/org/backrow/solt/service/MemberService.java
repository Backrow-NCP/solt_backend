package org.backrow.solt.service;

import org.backrow.solt.domain.Member;
import org.backrow.solt.dto.ImageDTO;
import org.backrow.solt.dto.ImageUploadDTO;
import org.backrow.solt.dto.MemberInfoDTO;
import org.backrow.solt.dto.ModifyDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface MemberService {

    MemberInfoDTO getMemberInfo(long memberId);

    void modifyMember(ModifyDTO modifyDTO);

    void deleteMember(long memberId, String password);

    void modifyMemberImage(long memberId, ImageDTO image);

    ImageDTO uploadImage(ImageUploadDTO imageUploadDTO);

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
