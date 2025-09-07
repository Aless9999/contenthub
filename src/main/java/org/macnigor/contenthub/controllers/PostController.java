package org.macnigor.contenthub.controllers;

import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.services.ImageService;
import org.macnigor.contenthub.services.PostService;
import org.macnigor.contenthub.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Slf4j
@RequestMapping("/posts")
@Controller
public class PostController {

    private final UserService userService;
    private final PostService postService;
    private final ImageService imageService;

    public PostController(UserService userService, PostService postService, ImageService imageService) {
        this.userService = userService;
        this.postService = postService;
        this.imageService = imageService;
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute PostDto postDto,
                           Principal principal,
                           @RequestParam("image") MultipartFile image) {
        String username = principal.getName();
        log.info("Пользователь {} создает новый пост с заголовком: {}", username, postDto.getTitle());

        try {
            User user = userService.findByUsername(username);
            Post post = postService.createPost(postDto, user);

            // Логируем информацию о создании поста
            log.debug("Создан новый пост с id={} для пользователя {}", post.getId(), username);

            imageService.createImage(image, post, user);
            log.info("Изображение успешно загружено для поста id={} пользователем {}", post.getId(), username);

            return "redirect:/home";
        } catch (Exception e) {
            log.error("Ошибка при создании поста для пользователя {}. Ошибка: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        String username = principal.getName();
        log.debug("Пользователь {} зашел на домашнюю страницу", username);

        try {
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
            model.addAttribute("posts", postService.getAllPosts());

            log.info("Домашняя страница подготовлена для пользователя {}", username);
        } catch (Exception e) {
            log.error("Ошибка при подготовке домашней страницы для пользователя {}", username, e);
            // Можно вернуть страницу с ошибкой или что-то еще
            return "error";  // Например, страница с ошибкой
        }
        return "home";
    }

    @PostMapping("/{postId}/saveimage")
    public String saveImage(@RequestParam("image") MultipartFile image,
                            @PathVariable Long postId,
                            Principal principal) {
        String username = principal.getName();
        log.info("Пользователь {} пытается сохранить изображение для поста с id={}", username, postId);

        try {
            User user = userService.findByUsername(username);
            Post post = postService.findById(postId);

            if (post != null) {
                log.debug("Изображение для поста id={} будет сохранено пользователем {}", postId, username);
                imageService.createImage(image, post, user);
                log.info("Изображение успешно сохранено для поста id={} пользователем {}", postId, username);
            } else {
                log.warn("Пост с id={} не найден. Пользователь {} не смог сохранить изображение", postId, username);
            }
        } catch (Exception e) {
            log.error("Ошибка при сохранении изображения для поста с id={} пользователем {}", postId, username, e);
        }

        return "redirect:/home";
    }
}
