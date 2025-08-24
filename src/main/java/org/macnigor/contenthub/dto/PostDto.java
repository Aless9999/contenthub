package org.macnigor.contenthub.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostDto {
    private String title;
    private String caption;
    private String location;

}
