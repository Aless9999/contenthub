package org.macnigor.contenthub.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Controller
public class HomeController {

    private final UserService userService;
    private final PostService postService;
    private final ImageService imageService;

    public HomeController(UserService userService, PostService postService, ImageService imageService) {
        this.userService = userService;
        this.postService = postService;
        this.imageService = imageService;
    }

    // Домашняя страница
    @GetMapping("/home")
    public String home(Model model, Principal principal) throws JsonProcessingException {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        // Получаем DTO постов
        List<PostDto> posts = postService.getAllPostDtos();
        model.addAttribute("posts", posts); // для рендеринга списка заголовков

        // JSON для JS, чтобы showPost() работал
        ObjectMapper mapper = new ObjectMapper();
        String postsJson = mapper.writeValueAsString(posts);
        model.addAttribute("postsJson", postsJson);

        return "home";
    }




    // Получение одного поста (AJAX подгрузка)
    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        List<ImageModel> images = imageService.getImagesByPostId(id);

        model.addAttribute("post", post);
        model.addAttribute("images", images);

        return "fragments/post :: post"; // Thymeleaf вернет только div с постом
    }
}

