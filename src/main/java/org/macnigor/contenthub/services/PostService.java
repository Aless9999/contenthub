package org.macnigor.contenthub.services;

import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.CommentDto;
import org.macnigor.contenthub.dto.ImageDto;
import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.exception.PostNotFoundException;
import org.macnigor.contenthub.mapper.CommentMapper;
import org.macnigor.contenthub.mapper.ImageMapper;
import org.macnigor.contenthub.mapper.PostMapper;
import org.macnigor.contenthub.repositories.ImageRepository;
import org.macnigor.contenthub.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final ImageMapper imageMapper;
    private final CommentMapper commentMapper;

    @Autowired
    public PostService(PostRepository postRepository, ImageRepository imageRepository, ImageMapper imageMapper, PostMapper postMapper, CommentMapper commentMapper) {
        this.postRepository = postRepository;
        this.imageMapper = imageMapper;
        this.commentMapper = commentMapper;
    }

    public List<Post> getAllPosts() {
        try {
            log.debug("Запрос всех постов");
            List<Post> posts = postRepository.findAll();

            log.info("Найдено {} пост(ов)", posts.size());
            return posts;

        } catch (DataAccessException e) {
            log.error("Ошибка доступа к базе данных при загрузке всех постов: {}", e.getMessage());
            throw new RuntimeException("Ошибка доступа к базе данных при загрузке всех постов", e);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при загрузке всех постов: {}", e.getMessage());
            throw new RuntimeException("Неожиданная ошибка при загрузке всех постов", e);
        }
    }


    public Post createPost(PostDto postDto, User user) {
        if (postDto == null || user == null) {
            throw new IllegalArgumentException("PostDto и User не могут быть null");
        }

        log.info("Создание поста пользователем: {}", user.getUsername());
        try {
            Post post = new Post();
            post.setTitle(postDto.getTitle());
            post.setCaption(postDto.getCaption());
            post.setLocation(postDto.getLocation());
            post.setUser(user);

            postRepository.save(post);

            log.info("Пост с id={} успешно создан пользователем {}", post.getId(), user.getUsername());
            return post;

        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при создании поста: нарушение целостности данных для пользователя {}", user.getUsername(), e);
            throw new RuntimeException("Ошибка при создании поста: нарушение целостности данных", e);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании поста пользователем {}", user.getUsername(), e);
            throw new RuntimeException("Неожиданная ошибка при создании поста", e);
        }
    }





    public Post findById(Long postId) {
        log.debug("Поиск поста по id={}", postId);
        return postRepository.findPostById(postId)
                .orElseThrow(() -> {
                    log.warn("Пост с id={} не найден", postId);
                    return new RuntimeException("Post with id=" + postId + " not found");
                });
    }

    public Post getPostById(Long id) {
        log.debug("Получение поста по id={}", id);
        return findById(id);
    }

    public List<PostDto> getAllPostDtos() {
        log.debug("Запрос DTO всех постов");
        List<PostDto> postDtos = postRepository.findAll().stream()
                .map(post -> {
                    PostDto dto = new PostDto();
                    dto.setId(post.getId());
                    dto.setTitle(post.getTitle());
                    dto.setCaption(post.getCaption());
                    dto.setLocation(post.getLocation());

                    dto.setImages(
                            post.getImages().stream()
                                    .map(img -> {
                                        ImageDto i = new ImageDto();
                                        i.setId(img.getId());
                                        i.setImageUrl(img.getImageUrl());
                                        return i;
                                    })
                                    .toList()
                    );

                    dto.setComments(
                            post.getComments().stream()
                                    .map(c -> {
                                        CommentDto cd = new CommentDto();
                                        cd.setUsername(c.getUser().getUsername());
                                        cd.setMessage(c.getMessage());
                                        return cd;
                                    })
                                    .toList()
                    );

                    return dto;
                })
                .toList();

        log.info("Сформировано {} DTO пост(ов)", postDtos.size());
        return postDtos;
    }

    public void editPostByAdmin(Long id, PostDto postDto) {
        // Логирование начала выполнения метода
        log.info("Начало обновления поста с id = {}", id);

        // Находим пост по id
        Post oldPost = postRepository.findPostById(id)
                .orElseThrow(() -> new PostNotFoundException("Пост с id " + id + " не найден"));

        // Логирование, что пост найден
        log.info("Пост с id = {} найден. Начинаем обновление.", id);

        // Обновляем только те поля, которые не пустые или изменились
        if (postDto.getTitle() != null && !postDto.getTitle().equals(oldPost.getTitle())) {
            log.info("Обновляем заголовок поста с '{}' на '{}'", oldPost.getTitle(), postDto.getTitle());
            oldPost.setTitle(postDto.getTitle());
        }

        if (postDto.getCaption() != null && !postDto.getCaption().equals(oldPost.getCaption())) {
            log.info("Обновляем описание поста с '{}' на '{}'", oldPost.getCaption(), postDto.getCaption());
            oldPost.setCaption(postDto.getCaption());
        }

        if (postDto.getLocation() != null && !postDto.getLocation().equals(oldPost.getLocation())) {
            log.info("Обновляем локацию поста с '{}' на '{}'", oldPost.getLocation(), postDto.getLocation());
            oldPost.setLocation(postDto.getLocation());
        }

        // Обрабатываем изображения (если они есть)
        if (postDto.getImages() != null && !postDto.getImages().isEmpty()) {
            log.info("Обновляем изображения для поста с id = {}", id);
            oldPost.setImages(postDto.getImages().stream()
                    .map(imageDto -> imageMapper.toEntity(imageDto, oldPost))
                    .collect(Collectors.toList()));
        }

        // Если комментарии есть и они не пустые, обновляем их
        if (postDto.getComments() != null && !postDto.getComments().isEmpty()) {
            log.info("Обновляем комментарии для поста с id = {}", id);
            oldPost.setComments(postDto.getComments().stream()
                    .map(commentDto -> commentMapper.toEntity(commentDto, oldPost))
                    .collect(Collectors.toList()));
        }

        // Сохраняем обновленный пост
        postRepository.save(oldPost);

        // Логирование успешного завершения обновления
        log.info("Пост с id = {} успешно обновлен.", id);
    }
}


