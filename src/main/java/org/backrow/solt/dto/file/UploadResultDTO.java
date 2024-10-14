package org.backrow.solt.dto.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResultDTO {
    private String fileName;
    private boolean image;
}
