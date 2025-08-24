package org.macnigor.contenthub.controllers;

import org.macnigor.contenthub.dto.UserRegisterDto;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.services.PostService;
import org.macnigor.contenthub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping
public class SecurityController {


    private UserService userService;
    private final PostService postService;

    @Autowired
    public SecurityController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }


    @GetMapping("/home")
    public String homePage(Model model, Authentication authentication) {
        // Получаем текущего пользователя
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);

        // Получаем все посты (свои и чужие)
        List<Post> allPosts = postService.getAllPosts();

        // Добавляем в модель
        model.addAttribute("user", currentUser);
        model.addAttribute("posts", allPosts);

        return "home"; // Шаблон home.html
    }

    // Страница входа
    @GetMapping("/login")
    public String loginPage() {
        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Проверяем, если пользователь уже залогинен, перенаправляем на главную страницу
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";  // Перенаправление на главную страницу
        }

        // Если пользователь не залогинен, показываем страницу логина
        return "login";  // Страница для входа
    }


    @GetMapping("/register")
    public String registerUserForm(Model model) {
        model.addAttribute("user", new UserRegisterDto()); // Передаем новый объект в модель
        return "register";  // Страница с формой
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegisterDto userRegisterDto) {
        // Логика регистрации пользователя (сохранение в базе данных и т.д.)
        userService.createUser(userRegisterDto);
        // После успешной регистрации перенаправляем на страницу входа или домашнюю страницу
        return "redirect:/home";  // Перенаправление на страницу входа после регистрации
    }}


