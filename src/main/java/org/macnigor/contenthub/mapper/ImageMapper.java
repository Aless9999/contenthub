package org.macnigor.contenthub.mapper;

import org.macnigor.contenthub.dto.ImageDto;
import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Component
public class ImageMapper {
    public ImageModel toEntity(ImageDto dto, Post post) {
        ImageModel model = new ImageModel();
        model.setName(dto.getName());
        model.setPost(post);
        model.setImageUrl(dto.getImageUrl());
        return model;
    }


    }




