package org.macnigor.contenthub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.mapper.PostMapper;
import org.macnigor.contenthub.services.ImageService;
import org.macnigor.contenthub.services.PostService;
import org.macnigor.contenthub.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/posts")
@Controller
public class PostController {

    private final UserService userService;
    private final PostService postService;
    private final ImageService imageService;
    private final PostMapper postMapper;

    public PostController(UserService userService, PostService postService, ImageService imageService, PostMapper postMapper) {
        this.userService = userService;
        this.postService = postService;
        this.imageService = imageService;
        this.postMapper = postMapper;
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute PostDto postDto,
                           Principal principal,
                           @RequestParam(value = "image", required = false) MultipartFile image) {
        String username = principal.getName();
        log.info("Пользователь {} создает новый пост с заголовком: {}", username, postDto.getTitle());

        try {
            User user = userService.findByUsername(username);
            Post post = postService.createPost(postDto, user);
            log.debug("Создан новый пост с id={} для пользователя {}", post.getId(), username);

            // Проверяем, был ли файл прикреплён
            if (image != null && !image.isEmpty()) {
                try {
                    imageService.createImage(image, post, user);
                    log.info("Изображение успешно загружено для поста id={} пользователем {}", post.getId(), username);
                } catch (Exception imgEx) {
                    log.error("Ошибка при сохранении изображения для поста {}: {}", post.getId(), imgEx.getMessage(), imgEx);
                    // не прерываем выполнение, просто логируем
                }
            } else {
                log.debug("Изображение не прикреплено, пост сохраняется без картинки");
            }

            return "redirect:/home";
        } catch (Exception e) {
            log.error("Ошибка при создании поста пользователем {}: {}", username, e.getMessage(), e);
            // можно вернуть страницу с ошибкой, но без 500
            return "redirect:/home?error=post";
        }
    }


    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        String username = principal.getName();
        log.debug("Пользователь {} зашел на домашнюю страницу", username);

        try {
            User user = userService.findByUsername(username);
            List<Post> posts = postService.getAllPosts();

            // Преобразуем посты в DTO (включая изображения)
            List<PostDto> postDtos = posts.stream()
                    .map(postMapper::toDto)
                    .toList();

            // Преобразуем DTO в JSON (для JavaScript)
            ObjectMapper mapper = new ObjectMapper();
            String postsJson = mapper.writeValueAsString(postDtos);

            // Добавляем данные в модель
            model.addAttribute("user", user);
            model.addAttribute("posts", postDtos);
            model.addAttribute("postsJson", postsJson);

            log.info("Домашняя страница подготовлена для пользователя {}", username);
        } catch (Exception e) {
            log.error("Ошибка при подготовке домашней страницы для пользователя {}", username, e);
            return "error";
        }

        return "home"; // шаблон home.html
    }


    @PostMapping("/{id}/saveimage")
    @ResponseBody
    public Map<String, Object> uploadImage(@PathVariable Long id,
                                           @RequestParam("image") MultipartFile file,Principal principal) throws IOException {
        Post post = postService.getPostById(id);
        User user = userService.findByUsername(principal.getName());
        ImageModel saved =imageService.createImage(file,post,user);


        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("url", saved.getImageUrl());
        return response;
    }

}
