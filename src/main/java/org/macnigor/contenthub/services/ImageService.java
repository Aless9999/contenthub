package org.macnigor.contenthub.services;

import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.exeption.ImageUploadException;
import org.macnigor.contenthub.repositories.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;


@Service
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    // Получение изображений для поста по его ID
    public List<ImageModel> getImagesByPostId(Long id) {
        log.info("Получение изображений для поста с id: {}", id);

        List<ImageModel> images = imageRepository.findByPostId(id);

        if (images.isEmpty()) {
            log.warn("Для поста с id: {} не найдены изображения", id);
        } else {
            log.info("Найдено {} изображений для поста с id: {}", images.size(), id);
        }

        return images;
    }

    // Создание изображения для поста
    public void createImage(MultipartFile image, Post post, User user) {
        log.info("Попытка создать изображение для поста с id: {} пользователем: {}", post.getId(), user.getUsername());

        if (image.isEmpty()) {
            log.error("Получен пустой файл изображения для поста с id: {}", post.getId());
            throw new IllegalArgumentException("Пустой файл изображения");
        }

        try {
            // Создаем новый объект ImageModel
            ImageModel newImage = new ImageModel();

            // Устанавливаем имя файла изображения
            newImage.setName(image.getOriginalFilename());
            log.debug("Имя файла изображения: {}", image.getOriginalFilename());

            // Сохраняем файл как массив байтов
            newImage.setImageSize(image.getBytes());
            log.debug("Размер изображения (в байтах): {}", image.getBytes().length);

            // Устанавливаем пользователя и пост
            newImage.setUser(user);
            newImage.setPost(post);

            // Сохраняем изображение в базе данных
            imageRepository.save(newImage);

            log.info("Изображение с именем '{}' успешно создано и сохранено для поста с id: {}", image.getOriginalFilename(), post.getId());
        } catch (IOException e) {
            log.error("Ошибка при создании изображения для поста с id: {} пользователем: {}. Ошибка: {}", post.getId(), user.getUsername(), e.getMessage(), e);
            throw new ImageUploadException("Ошибка при загрузке изображения", e);
        }
    }

    // Поиск изображения по его ID
    public ImageModel findById(Long id) {
        log.info("Поиск изображения с id: {}", id);

        return imageRepository.findById(id).orElseThrow(() -> {
            log.error("Изображение с id {} не найдено", id);
            return new RuntimeException("Image with id=" + id + " not found");
        });
    }


}
