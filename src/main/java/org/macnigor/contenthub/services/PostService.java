package org.macnigor.contenthub.services;

import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.CommentDto;
import org.macnigor.contenthub.dto.ImageDto;
import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.repositories.ImageRepository;
import org.macnigor.contenthub.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public PostService(PostRepository postRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
    }

    public List<Post> getAllPosts() {
        log.debug("Запрос всех постов");
        List<Post> posts = postRepository.findAll();
        log.info("Найдено {} пост(ов)", posts.size());
        return posts;
    }

    public Post createPost(PostDto postDto, User user) {
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
        } catch (Exception e) {
            log.error("Ошибка при создании поста пользователем {}", user.getUsername(), e);
            throw e;
        }
    }

    public List<Post> getAllPostsForUser(Long userId) {
        log.debug("Запрос постов для пользователя id={}", userId);
        List<Post> userPosts = getAllPosts().stream()
                .filter(post -> post.getUser().getId().equals(userId))
                .collect(Collectors.toUnmodifiableList());

        log.info("Найдено {} пост(ов) для пользователя id={}", userPosts.size(), userId);
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

}
