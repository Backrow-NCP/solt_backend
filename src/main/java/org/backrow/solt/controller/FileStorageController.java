package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.file.UploadResultDTO;
import org.backrow.solt.service.file.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "파일 관리 API", description = "파일 업로드·다운로드, 삭제 기능을 수행하는 API입니다.")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Log4j2
public class FileStorageController {
    private final FileStorageService fileStorageService;
    private final String bucketName = "solt-objectstorage";
    private final String bucketFolderName = "board/";

    @Operation(summary = "파일 업로드", description = "여러 개의 파일을 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, List<String>>> uploadFiles(@RequestParam List<MultipartFile> files) {

        Map<String, List<String>> response = new HashMap<>();
        List<String> filenames = new ArrayList<>();

        for (MultipartFile file : files) {
            UploadResultDTO uploadResultDTO = fileStorageService.uploadFile(bucketName, bucketFolderName, file);
            filenames.add(uploadResultDTO.getFileName());
        }

        response.put("filenames", filenames);
        log.info(response);

        return ResponseEntity.ok().body(response);
    }


    @Operation(summary = "파일 삭제", description = "파일명을 통해 파일을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Map<String, Boolean>> deleteFiles(@RequestParam List<String> filenames) {
        Map<String, Boolean> response = new HashMap<>();
            for (String filename : filenames) {
                fileStorageService.deleteFile(bucketName, bucketFolderName+ filename);
                if (filename == null) {
                    continue;
                }
                response.put("result", Boolean.TRUE);
            }
            return ResponseEntity.ok().body(response);
    }
}
