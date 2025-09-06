package org.macnigor.contenthub.controllers;

import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.CommentDto;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.exception.PostNotFoundException;
import org.macnigor.contenthub.repositories.PostRepository;
import org.macnigor.contenthub.repositories.UserRepository;
import org.macnigor.contenthub.services.CommentService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentController(CommentService commentService, PostRepository postRepository, UserRepository userRepository) {
        this.commentService = commentService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/posts/{id}/comment/save")
    public String addComment(@PathVariable Long id, Principal principal,CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария для поста с ID: {}", id);
        Post post = postRepository.findPostById(id).orElseThrow(()->new PostNotFoundException("Post with id "+ id+" not found"));
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(()->new UsernameNotFoundException("User with username "+principal.getName()+" not found"));
        // Логируем пользователя, который добавляет комментарий
        log.info("Пользователь с именем: {} добавляет комментарий", user.getUsername());

        // Добавляем новый комментарий через сервис
        commentService.addNewComment(post, user, commentDto);

        log.info("Комментарий для поста с ID: {} успешно добавлен пользователем: {}", post.getId(), user.getUsername());

        return "redirect:/home";
    }
}
