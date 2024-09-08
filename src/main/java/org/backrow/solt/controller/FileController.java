package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.backrow.solt.dto.UploadResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Tag(name = "파일 관리 API", description = "파일 업로드·다운로드, 삭제 기능을 수행하는 API입니다.")
@RestController("/file")
@Log4j2
public class FileController {
    @Value("${org.backrow.upload.path}")
    private String uploadPath;

    @Operation(summary = "파일 업로드", description = "여러 개의 파일을 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> uploadFile(@RequestPart("files") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        List<UploadResultDTO> result = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                UploadResultDTO uploadResult = processFileUpload(file);
                result.add(uploadResult);
            } catch (Exception e) {
                log.error("File upload failed for {}: {}", file.getOriginalFilename(), e.getMessage());
            }
        }
        return result;
    }

    @Operation(summary = "파일 다운로드", description = "파일명을 통해 파일을 다운로드합니다.")
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            String safeFileName = sanitizeFileName(fileName);
            Resource resource = getResource(safeFileName);
            if (resource == null) {
                log.error("File not found: {}", safeFileName);
                return ResponseEntity.notFound().build();
            }

            String contentType = getContentType(resource.getFile().toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            if (!contentType.startsWith("image")) { // 이미지 파일이 아닐 경우 파일 다운로드 처리.
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + safeFileName);
            }

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (Exception e) {
            log.error("File download failed for {}: {}", fileName, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "파일 삭제", description = "파일명을 통해 파일을 삭제합니다.")
    @DeleteMapping("/{fileName}")
    public ResponseEntity<Map<String, Boolean>> deleteFile(@PathVariable String fileName) {
        try {
            String safeFileName = sanitizeFileName(fileName);
            Path filePath = getPath(safeFileName);
            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                log.error("File not found or not readable: {}", safeFileName);
                return ResponseEntity.notFound().build();
            }

            // 이미지의 경우, 썸네일 파일 삭제
            String contentType = getContentType(filePath);
            if (contentType.startsWith("image")) {
                deleteThumbnail(safeFileName);
            }

            // 파일 삭제
            Files.delete(filePath);

            return ResponseEntity.ok(Map.of("isSuccess", true));
        } catch (Exception e) {
            log.error("File deletion failed for {}: {}", fileName, e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("isSuccess", false));
        }
    }

    /** 파일 업로드 */
    private UploadResultDTO processFileUpload(MultipartFile file) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String safeFileName = sanitizeFileName(originalFileName);
        String uuid = UUID.randomUUID().toString();
        boolean image = false;

        Path savePath = getPath(uuid + "_" + safeFileName);
        file.transferTo(savePath);

        String contentType = getContentType(savePath);
        if (contentType.startsWith("image")) {
            image = true;
            createThumbnail(savePath.toFile(), uuid, safeFileName);
        }

        return UploadResultDTO.builder()
                .uuid(uuid)
                .fileName(safeFileName)
                .image(image)
                .build();
    }

    /** 썸네일 파일 생성 */
    private void createThumbnail(File file, String uuid, String fileName) throws Exception {
        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + fileName);
        Thumbnailator.createThumbnail(file, thumbFile, 200, 200);
    }

    /** 파일명 검증 */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }

    /** 파일 경로를 통해 리소스 가져오기 */
    private Resource getResource(String fileName) {
        try {
            Path filePath = getPath(fileName);
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists() || !resource.isReadable()) {
                return null;
            }
            return resource;
        } catch (Exception e) {
            return null;
        }
    }

    /** 파일 Path 가져오기 */
    private Path getPath(String fileName) {
        return Paths.get(uploadPath).resolve(fileName).normalize();
    }

    /** 파일의 MIME 타입 추론 */
    private String getContentType(Path path) throws Exception {
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream"; // 기본적으로 바이너리 파일로 설정
        }
        return contentType;
    }

    /** 썸네일 파일 제거 */
    private void deleteThumbnail(String fileName) {
        try {
            Path thumbPath = getPath("s_" + fileName);
            Files.delete(thumbPath);
        } catch (NoSuchFileException e) {
            log.error("Thumbnail file not found: {}", fileName);
        } catch (Exception e) {
            log.error("Failed to delete thumbnail file: {}", fileName);
        }
    }
}
