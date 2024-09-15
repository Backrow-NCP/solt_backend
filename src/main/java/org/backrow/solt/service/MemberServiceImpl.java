package org.backrow.solt.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.backrow.solt.domain.Member;
import org.backrow.solt.dto.*;
import org.backrow.solt.dto.file.UploadResultDTO;
import org.backrow.solt.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
