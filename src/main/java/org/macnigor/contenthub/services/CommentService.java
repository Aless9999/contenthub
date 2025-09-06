package org.macnigor.contenthub.services;

import org.macnigor.contenthub.dto.CommentDto;
import org.macnigor.contenthub.entity.Comment;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.mapper.CommentMapper;
import org.macnigor.contenthub.repositories.CommentRepository;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public void addNewComment(Post post, User user, CommentDto commentDto) {
        log.info("Получен запрос на добавление нового комментария для поста с ID: {}", post.getId());

        Comment newComment = new Comment();
        newComment.setUser(user);
        newComment.setPost(post);
        newComment.setMessage(commentDto.getMessage());

        log.info("Создан новый комментарий для пользователя: {} с сообщением: {}", user.getUsername(), commentDto.getMessage());

        commentRepository.save(newComment);

        log.info("Комментарий успешно сохранен для поста с ID: {}", post.getId());
    }

    public List<CommentDto> getAllCommentForPost(Long id) {
        return commentRepository.findCommentByPostId(id).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
}

