package org.macnigor.contenthub.services;

import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.CommentDto;
import org.macnigor.contenthub.dto.ImageDto;
import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.exception.PostNotFoundException;
import org.macnigor.contenthub.mapper.ImageMapper;
import org.macnigor.contenthub.mapper.PostMapper;
import org.macnigor.contenthub.repositories.ImageRepository;
import org.macnigor.contenthub.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
private final ImageMapper imageMapper;
private final PostMapper postMapper;
    @Autowired
    public PostService(PostRepository postRepository, ImageRepository imageRepository, ImageMapper imageMapper, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.postMapper = postMapper;
    }

    public List<Post> getAllPosts() {
        try {
            log.debug("Запрос всех постов");
            List<Post> posts = postRepository.findAll();

            if (posts == null) {
                log.warn("Найдены пустые данные для постов.");
                posts = new ArrayList<>(); // Защита от null
            }

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


    public List<Post> getAllPostsForUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        log.debug("Запрос постов для пользователя id={}", userId);

        List<Post> userPosts;
        try {
            // Оптимизация: фильтруем на уровне базы данных, если это возможно
            userPosts = Collections.singletonList(postRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Posts for userId" + userId + "not found")));

            // Если нельзя сделать запрос на уровне базы данных, фильтруем на уровне памяти
            if (userPosts == null) {
                userPosts = getAllPosts().stream()
                        .filter(post -> post.getUser().getId().equals(userId))
                        .collect(Collectors.toUnmodifiableList());
            }

            log.info("Найдено {} пост(ов) для пользователя id={}", userPosts.size(), userId);
        } catch (Exception e) {
            log.error("Ошибка при запросе постов для пользователя id={}", userId, e);
            throw new RuntimeException("Ошибка при запросе постов для пользователя", e);
        }

        return userPosts;
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

    public PostDto editPostByAdmin(Long id, PostDto postDto) {
        Post oldPost = postRepository.findPostById(id).orElseThrow(() -> new PostNotFoundException("Post with id " + id + " not found"));
        oldPost.setLocation(postDto.getLocation());
        oldPost.setCaption(postDto.getCaption());
        oldPost.setTitle(postDto.getTitle());
        oldPost.setImages(postDto.getImages().stream()
                .map(imageDto -> imageMapper.toEntity(imageDto,oldPost))
                .collect(Collectors.toUnmodifiableList()));
        return postMapper.toDto(oldPost);
    }
}
