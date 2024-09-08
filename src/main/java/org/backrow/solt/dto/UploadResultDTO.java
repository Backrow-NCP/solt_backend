package org.backrow.solt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResultDTO {
    private String uuid;
    private String fileName;
    private boolean image;

    public String getLink() {
        String result = uuid + "_" + fileName;

        if (image) result = "s_" + result;

        return result;
    }
}
