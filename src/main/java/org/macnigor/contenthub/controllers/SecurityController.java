package org.macnigor.contenthub.controllers;

import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.UserDto;
import org.macnigor.contenthub.services.ImageService;
import org.macnigor.contenthub.services.PostService;
import org.macnigor.contenthub.services.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping
public class SecurityController {

    private final UserService userService;
    private final PostService postService;
    private final ImageService imageService;

    public SecurityController(UserService userService, PostService postService, ImageService imageService) {
        this.userService = userService;
        this.postService = postService;
        this.imageService = imageService;
    }

    // Страница входа
    @GetMapping("/login")
    public String loginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Логирование входа
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("Пользователь уже авторизован: {}", authentication.getName());
            return "redirect:/home";  // Перенаправление на главную страницу
        }

        log.info("Пользователь не авторизован, показываем страницу логина");
        return "login";  // Страница для входа
    }

    @GetMapping("/register")
    public String registerUserForm(Model model) {
        log.info("Открыта страница регистрации пользователя.");
        model.addAttribute("user", new UserDto()); // Передаем новый объект в модель
        return "register";  // Страница с формой
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserDto userRegisterDto) {
        log.info("Регистрация нового пользователя: {}", userRegisterDto.getUsername());
        // Логика регистрации пользователя
        userService.createUser(userRegisterDto);
        log.info("Пользователь успешно зарегистрирован: {}", userRegisterDto.getUsername());
        return "redirect:/home";  // Перенаправление на страницу входа после регистрации
    }
}
