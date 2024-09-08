package org.backrow.solt.service;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.backrow.solt.dto.FileDownloadDTO;
import org.backrow.solt.dto.UploadResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Log4j2
public class FileServiceImpl implements FileService {
    @Value("${org.backrow.upload.path}")
    private String uploadPath;

    @Override
    public List<UploadResultDTO> uploadFile(List<MultipartFile> files) {
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

    @Override
    public FileDownloadDTO downloadFile(String fileName) throws IOException {
        String safeFileName = sanitizeFileName(fileName);
        return FileDownloadDTO.builder()
                .resource(getResource(safeFileName))
                .contentType(getContentType(getPath(safeFileName)))
                .build();
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        String safeFileName = sanitizeFileName(fileName);
        Path filePath = getPath(safeFileName);
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new NoSuchFileException("File not found:" + safeFileName);
        }

        // 이미지의 경우, 썸네일 파일 삭제
        String contentType = getContentType(filePath);
        if (contentType.startsWith("image")) {
            deleteThumbnail(safeFileName);
        }

        // 파일 삭제
        Files.delete(filePath);

        return true;
    }

    /** 파일 업로드 */
    private UploadResultDTO processFileUpload(MultipartFile file) throws IOException {
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
    private void createThumbnail(File file, String uuid, String fileName) throws IOException {
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
    private String getContentType(Path path) throws IOException {
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
