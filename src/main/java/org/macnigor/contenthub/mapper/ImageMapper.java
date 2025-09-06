package org.macnigor.contenthub.mapper;

import org.macnigor.contenthub.dto.ImageDto;
import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ImageMapper {
    public ImageModel toEntity(ImageDto dto, Post post) {
        ImageModel model = new ImageModel();
        model.setName(dto.getName());
        model.setPost(post);
        try {
            model.setImageSize(dto.getImage().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
        return model;
    }
}

