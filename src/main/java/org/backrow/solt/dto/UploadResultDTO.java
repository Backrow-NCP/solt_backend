package org.backrow.solt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResultDTO {
    private String uuid;
    private String fileName;
    private boolean image;
}