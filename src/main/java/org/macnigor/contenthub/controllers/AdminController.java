package org.macnigor.contenthub.controllers;

import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.services.ImageService;
import org.macnigor.contenthub.services.PostService;
import org.macnigor.contenthub.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Slf4j  // Логирование с Lombok
public class AdminController {

    private final UserService userService;
    private final PostService postService;
    private final ImageService imageService;

    public AdminController(UserService userService, PostService postService, ImageService imageService) {
        this.userService = userService;
        this.postService = postService;
        this.imageService = imageService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        log.info("Получение данных для панели администратора.");
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("posts", postService.getAllPosts());
        log.info("Данные пользователей и постов успешно загружены.");
        return "admin/dashboard";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable long id) {
        log.info("Запрос на удаление пользователя с ID: {}", id);
        userService.deleteUser(id);
        log.info("Пользователь с ID: {} успешно удален.", id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable Long id, @ModelAttribute PostDto postDto) {
        log.info("Редактирование поста с ID: {}", id);
        postService.editPostByAdmin(id, postDto);
        log.info("Пост с ID: {} успешно отредактирован.", id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        log.info("Удаление поста с ID: {}", id);
        imageService.removeImagesWithPost(id);
        postService.deletePostById(id);

        log.info("Пост с ID: {} успешно удален.", id);
        return "redirect:/admin/dashboard";
    }
}
