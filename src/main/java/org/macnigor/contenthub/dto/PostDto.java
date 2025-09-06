package org.macnigor.contenthub.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostDto {
    private Long id;
    private String title;
    private String caption;
    private String location;
    private List<ImageDto> images;
    private List<CommentDto> comments;
}
