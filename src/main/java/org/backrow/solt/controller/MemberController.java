package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.member.ModifyDTO;
import org.backrow.solt.dto.file.UploadResultDTO;
import org.backrow.solt.security.CustomUserDetails;
import org.backrow.solt.service.file.FileStorageService;
import org.backrow.solt.service.member.MemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "회원정보 API", description = "회원 정보 조회, 수정, 삭제 및 프로필 사진 관리 기능을 수행하는 API입니다.")
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final FileStorageService fileStorageService;
    private final String bucketName = "solt-objectstorage";
    private final String bucketFolderName = "profile/";

    @Operation(summary = "회원 정보 조회", description = "회원 ID를 통해 회원의 상세 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<MemberInfoDTO> getMemberInfo(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            long memberId = userDetails.getMemberId();
            MemberInfoDTO memberInfo = memberService.getMemberInfo(memberId);
            return ResponseEntity.ok(memberInfo);
        } catch (Exception e){
           log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    @PutMapping
    public ResponseEntity<Map<String,Boolean>> modifyMember(
            @RequestBody ModifyDTO modifyDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info(modifyDTO);
       Map<String,Boolean> response = new HashMap<>();
       try {
           modifyDTO.setMemberId(userDetails.getMemberId());
           log.info(modifyDTO);
           memberService.modifyMember(modifyDTO);
           response.put("result", true);
           return ResponseEntity.ok(response);
       }catch (Exception e){
           log.error(e.getMessage());
           return ResponseEntity.internalServerError().build();
       }
    }

    @Operation(summary = "회원 탈퇴", description = "회원 ID를 통해 회원을 탈퇴 처리합니다.")
    @DeleteMapping
    public ResponseEntity<Map<String,Boolean>> deleteMember(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String,Boolean> response = new HashMap<>();
        try {
            long memberId = userDetails.getMemberId();
            memberService.deleteMember(memberId);
            response.put("result", true);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "회원 프로필 사진 수정", description = "회원 ID를 통해 회원의 프로필 사진을 수정합니다.")
    @PutMapping(value ="/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Boolean>> modifyMemberImage(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("image") MultipartFile image
    ) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            long memberId = userDetails.getMemberId();
            UploadResultDTO imageDTO = fileStorageService.uploadFile(bucketName,bucketFolderName,image);
            memberService.modifyMemberImage(memberId, imageDTO);
            response.put("result", true);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "회원 프로필 사진 삭제", description = "회원 ID를 통해 회원의 프로필 사진을 삭제합니다.")
    @DeleteMapping(value="/image")
    public ResponseEntity<Map<String,Boolean>> deleteMemberImage(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String,Boolean> response = new HashMap<>();
        try {
            long memberId = userDetails.getMemberId();
            MemberInfoDTO memberInfo = memberService.getMemberInfo(memberId);
            memberService.deleteMemberImage(memberId);
            fileStorageService.deleteFile(bucketName,bucketFolderName+memberInfo.getFileName());
            response.put("result", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
