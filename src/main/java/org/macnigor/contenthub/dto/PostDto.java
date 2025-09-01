package org.macnigor.contenthub.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String caption;
    private String location;
    private List<ImageDto> images;
    private List<CommentDto> comments;
}
