package org.backrow.solt.dto.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Builder
@Getter
@Setter
public class FileDownloadDTO {
    private Resource resource;
    private String contentType;
}
