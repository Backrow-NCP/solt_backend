package org.backrow.solt.service.file;

import org.backrow.solt.dto.file.UploadResultDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    UploadResultDTO uploadFile(String bucketName, String directoryPath, MultipartFile file);

    void deleteFile(String bucketName, String fileName);
}
