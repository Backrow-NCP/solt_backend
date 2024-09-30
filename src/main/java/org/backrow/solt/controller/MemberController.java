package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.member.ModifyDTO;
import org.backrow.solt.dto.file.UploadResultDTO;
import org.backrow.solt.service.FileService;
import org.backrow.solt.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "회원정보 API", description = "회원정보와 프로필사진의 조회,수정,삭제를 수행하는 API입니다.")
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final FileService fileService;

    @Operation(summary = "Member정보 가져오기 GET", description = "GET 회원정보")
    @GetMapping
    public ResponseEntity<MemberInfoDTO> getMemberInfo(long memberId) {
        try {
            MemberInfoDTO memberInfo = memberService.getMemberInfo(memberId);
            return ResponseEntity.ok(memberInfo);
        } catch (Exception e){
           log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Member정보 수정 POST", description = "POST 회원정보수정")
    @PutMapping
    public ResponseEntity<Map<String,Boolean>> modifyMember(ModifyDTO modifyDTO) {
       Map<String,Boolean> response = new HashMap<>();
       try {
           memberService.modifyMember(modifyDTO);
           response.put("result", true);
           return ResponseEntity.ok(response);
       }catch (Exception e){
           log.error(e.getMessage());
           return ResponseEntity.internalServerError().build();
       }
    }

    @Operation(summary = "회원 삭제 member", description = "DELETE 회원탈퇴")
    @DeleteMapping
    public ResponseEntity<Map<String,Boolean>> deleteMember(long memberId) {
        Map<String,Boolean> response = new HashMap<>();
        try {
            memberService.deleteMember(memberId);
            response.put("result", true);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "회원 프로필 사진 수정", description = "POST 회원 프로필 사진 수정")
    @PutMapping(value ="/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Boolean>> modifyMemberImage(long memberId,@RequestPart("image") MultipartFile image) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            UploadResultDTO imageDTO = fileService.uploadFile(List.of(image)).get(0);
            memberService.modifyMemberImage(memberId, imageDTO);
            response.put("result", true);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "회원프로필 사진 삭제", description = "DELETE 회원 프로필 사진 삭제")
    @DeleteMapping(value="/image")
    public ResponseEntity<Map<String,Boolean>> deleteMemberImage(long memberId) {
        Map<String,Boolean> response = new HashMap<>();
        try {
            memberService.deleteMemberImage(memberId);
            response.put("result", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
