package org.macnigor.contenthub.mapper;

import org.macnigor.contenthub.dto.CommentDto;
import org.macnigor.contenthub.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setMessage(comment.getMessage());
        dto.setUsername(comment.getUser().getUsername());
        return dto;
    }
}
