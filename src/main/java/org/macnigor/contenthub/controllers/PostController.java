package org.macnigor.contenthub.controllers;

import org.macnigor.contenthub.dto.ImageDto;
import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.services.ImageService;
import org.macnigor.contenthub.services.PostService;
import org.macnigor.contenthub.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RequestMapping("/posts")
@Controller
public class PostController {


    private UserService userService;

    private PostService postService;
    private ImageService imageService;

    public PostController(UserService userService, PostService postService, ImageService imageService) {
        this.userService = userService;
        this.postService = postService;
        this.imageService = imageService;
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute PostDto postDto, Principal principal){
        User user = userService.findByUsername(principal.getName());
        postService.createPost(postDto, user);
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("posts", postService.getAllPosts());
        return "home";
    }

    @PostMapping("/{postId}/saveimage")
    public String saveImage(@RequestParam("image") MultipartFile image, @PathVariable Long postId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Post post = postService.findById(postId); // Найдите пост по ID

        if (post != null) {
            // Логика для сохранения изображения
            imageService.createImage(image, post, user);
        }

        return "redirect:/home/"; // Перенаправление на страницу поста
    }


}
