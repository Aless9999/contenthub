package org.macnigor.contenthub.services;

import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.exception.ImageUploadException;
import org.macnigor.contenthub.repositories.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public ImageModel createImage(MultipartFile image, Post post, User user) throws IOException {
        log.info("Попытка создать изображение для поста с id: {} пользователем: {}", post.getId(), user.getUsername());

        if (image.isEmpty()) {
            log.error("Получен пустой файл изображения для поста с id: {}", post.getId());
            throw new IllegalArgumentException("Пустой файл изображения");
        }

        // Создаем новый объект ImageModel
        ImageModel newImage = new ImageModel();

        // Устанавливаем имя файла изображения
        String fileName = image.getOriginalFilename();
        newImage.setName(fileName);
        log.debug("Имя файла изображения: {}", fileName);

        // Создаем папку для хранения изображений
        File fileDir = new File("ImageContent");
        if (!fileDir.exists()) {
            boolean created = fileDir.mkdir(); // или mkdirs()
            if (!created) {
                throw new IOException("Не удалось создать папку для изображений: " + fileDir.getAbsolutePath());
            }
        }
        //Сохраняем изображение в папку на сервере
        Path path = Path.of("ImageContent",fileName);
        Files.copy(image.getInputStream(),path);

        // Сохраняем ссылку на файл
        String imageUrl = "/ImageContent/"+image.getOriginalFilename();
        newImage.setImageUrl(imageUrl);
        log.debug("Ссылка на изображение: {}", imageUrl);

        // Устанавливаем пользователя и пост
        newImage.setUser(user);
        newImage.setPost(post);

        // Сохраняем изображение в базе данных
        imageRepository.save(newImage);

        log.info("Изображение с именем '{}' успешно создано и сохранено для поста с id: {}", image.getOriginalFilename(), post.getId());
        return newImage;
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
