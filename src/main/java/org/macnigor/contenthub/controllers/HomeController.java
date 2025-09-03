package org.macnigor.contenthub.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.services.ImageService;
import org.macnigor.contenthub.services.PostService;
import org.macnigor.contenthub.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
public class HomeController {

    private final UserService userService;
    private final PostService postService;
    private final ImageService imageService;
    private final ObjectMapper objectMapper;

    // Конструктор для инициализации зависимостей
    public HomeController(UserService userService, PostService postService, ImageService imageService) {
        this.userService = userService;
        this.postService = postService;
        this.imageService = imageService;
        this.objectMapper = new ObjectMapper(); // Создаем объект ObjectMapper один раз
    }

    // Домашняя страница
    @GetMapping("/home")
    public String home(Model model, Principal principal) throws JsonProcessingException {
        String username = principal.getName();
        log.info("Загрузка домашней страницы для пользователя: {}", username);

        User user = userService.findByUsername(username);
        model.addAttribute("user", user);

        // Получаем DTO постов
        List<PostDto> posts = postService.getAllPostDtos();
        model.addAttribute("posts", posts); // для рендеринга списка заголовков

        // Преобразуем посты в JSON для JavaScript
        String postsJson = objectMapper.writeValueAsString(posts);
        model.addAttribute("postsJson", postsJson);

        log.debug("Передаем в модель список из {} постов", posts.size());
        return "home";
    }

    // Получение одного поста (AJAX подгрузка)
    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        log.info("Запрос на получение поста с id={}", id);

        // Получаем пост и изображения для него
        Post post = postService.getPostById(id);
        if (post == null) {
            log.warn("Пост с id={} не найден", id);
            model.addAttribute("error", "Пост не найден");
            return "error"; // Страница ошибки или редирект
        }

        List<ImageModel> images = imageService.getImagesByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("images", images);

        log.debug("Пост с id={} найден. Загружаем изображения.", id);
        return "fragments/post :: post"; // Thymeleaf вернет только div с постом
    }
}
