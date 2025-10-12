package org.macnigor.contenthub.mapper;

import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.Post;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PostMapper {
private final CommentMapper commentMapper;

    public PostMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public  PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setComments(
                post.getComments().stream()
                        .map(commentMapper::toDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
