package org.macnigor.contenthub.dto;

import lombok.Data;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageDto {
    private Long id;
    private String name;
    private String imageUrl;
    private User user;
    private Post post;
}

