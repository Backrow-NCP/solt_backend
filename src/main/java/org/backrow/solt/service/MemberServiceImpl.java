package org.backrow.solt.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.backrow.solt.domain.Member;
import org.backrow.solt.dto.*;
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

    @Value("${org.backrow.upload.path}")
    private String uploadPath;

    private final MemberRepository memberRepository;

    @Override
    public MemberInfoDTO getMemberInfo(long memberId) {
        Optional<Member> result = memberRepository.findById(memberId);
        Member member = result.orElseThrow();
        return (MemberInfoDTO) entityToDto(member);
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
    public void modifyMemberImage(long memberId, ImageDTO image) {
        Optional<Member> result = memberRepository.findById(memberId);
        Member member = result.orElseThrow();

        member.addImage(image.getUuid(), image.getFileName());
        memberRepository.save(member);
    }

    @Override
    public ImageDTO uploadImage(ImageUploadDTO imageUploadDTO) {

            MultipartFile multipartFile = (MultipartFile)imageUploadDTO.getImage();
            String originalFilename = multipartFile.getOriginalFilename();

            String uuid = UUID.randomUUID().toString();

            Path savePath = Paths.get(uploadPath, uuid + "_" + originalFilename);

            boolean image = false;

            try {
                multipartFile.transferTo(savePath);

                if (Files.probeContentType(savePath).startsWith("image")) {
                    image = true;
                    File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalFilename);
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return (ImageDTO) ImageDTO.builder()
                    .uuid(uuid)
                    .fileName(originalFilename)
                    .img(image)
                    .build();
    }
}
