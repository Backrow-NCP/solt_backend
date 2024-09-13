package org.backrow.solt.service;

import org.backrow.solt.dto.file.FileDownloadDTO;
import org.backrow.solt.dto.file.UploadResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<UploadResultDTO> uploadFile(List<MultipartFile> files);
    FileDownloadDTO downloadFile(String fileName) throws IOException;
    boolean deleteFile(String fileName) throws IOException;
}