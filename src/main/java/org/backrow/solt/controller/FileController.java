package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.file.FileDownloadDTO;
import org.backrow.solt.dto.file.UploadResultDTO;
import org.backrow.solt.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.NoSuchFileException;
import java.util.*;

@Tag(name = "파일 관리 API", description = "파일 업로드·다운로드, 삭제 기능을 수행하는 API입니다.")
@RestController("/file")
@RequiredArgsConstructor
@Log4j2
public class FileController {
    private final FileService fileService;

    @Operation(summary = "파일 업로드", description = "여러 개의 파일을 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> uploadFile(@RequestPart("files") List<MultipartFile> files) {
        return fileService.uploadFile(files);
    }

    @Operation(summary = "파일 다운로드", description = "파일명을 통해 파일을 다운로드합니다.")
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            FileDownloadDTO fileDownloadDTO = fileService.downloadFile(fileName);
            Resource resource = fileDownloadDTO.getResource();
            String contentType = fileDownloadDTO.getContentType();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            if (!contentType.startsWith("image")) { // 이미지 파일이 아닐 경우 파일 다운로드 처리.
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            }

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (NoSuchFileException e) {
            log.error("File not found: {}", fileName);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("File download failed for {}: {}", fileName, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "파일 삭제", description = "파일명을 통해 파일을 삭제합니다.")
    @DeleteMapping("/{fileName}")
    public ResponseEntity<Map<String, Boolean>> deleteFile(@PathVariable String fileName) {
        try {
            boolean result = fileService.deleteFile(fileName);
            return ResponseEntity.ok(Map.of("isSuccess", result));
        } catch (NoSuchFileException e) {
            log.error("File not found or not readable: {}", fileName);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("File deletion failed for {}: {}", fileName, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("isSuccess", false));
        }
    }
}
