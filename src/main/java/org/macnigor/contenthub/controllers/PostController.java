package org.macnigor.contenthub.controllers;

import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.services.PostService;
import org.macnigor.contenthub.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@RequestMapping("/posts")
@Controller
public class PostController {


    private UserService userService;

    private PostService postService;


    public PostController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute PostDto postDto, Principal principal){
        User user = userService.findByUsername(principal.getName());
        postService.createPost(postDto, user);
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(Model model, Authentication authentication) {
        // Получаем текущего пользователя
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);


        List<Post> allPosts = postService.getAllPosts();


        model.addAttribute("user", currentUser);
        model.addAttribute("posts", allPosts);

        return "home";
    }
}
