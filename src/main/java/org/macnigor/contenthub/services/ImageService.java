package org.macnigor.contenthub.services;

import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.images.path}")
    private final String imagePath;

    public ImageService(ImageRepository imageRepository, @Value("${app.images.path}") String pathname) {
        this.imageRepository = imageRepository;
        this.imagePath = pathname;
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

        validateImage(image, post); // Проверяем, что файл не пустой

        // Создаем новый объект ImageModel
        ImageModel newImage = new ImageModel();

        // Устанавливаем имя файла изображения
        String fileName = image.getOriginalFilename();
        newImage.setName(fileName);
        log.debug("Имя файла изображения: {}", fileName);

        // Создаем папку для хранения изображений
        createImageDirectory(imagePath);

        // Сохраняем изображение на сервере
        Path path = saveImageToFileSystem(image);

        // Сохраняем ссылку на файл
        String imageUrl = generateImageUrl(fileName);
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


    private void validateImage(MultipartFile image, Post post) {
        if (image.isEmpty()) {
            log.error("Получен пустой файл изображения для поста с id: {}", post.getId());
            throw new IllegalArgumentException("Пустой файл изображения");
        }
    }
    private void createImageDirectory(String pathname) throws IOException {
        File fileDir = new File(pathname);
        if (!fileDir.exists()) {
            boolean created = fileDir.mkdir(); // или mkdirs() для вложенных директорий
            if (!created) {
                throw new IOException("Не удалось создать папку для изображений: " + fileDir.getAbsolutePath());
            }
        }
    }
    private Path saveImageToFileSystem(MultipartFile image) throws IOException {
        String fileName = image.getOriginalFilename();
        Path path = Path.of("ImageContent", fileName);
        Files.copy(image.getInputStream(), path);
        return path;
    }

    private String generateImageUrl(String fileName) {
        return "/ImageContent/" + fileName;
    }



    // Поиск изображения по его ID
    public ImageModel findById(Long id) {
        log.info("Поиск изображения с id: {}", id);

        return imageRepository.findById(id).orElseThrow(() -> {
            log.error("Изображение с id {} не найдено", id);
            return new RuntimeException("Image with id=" + id + " not found");
        });
    }

    // Удаляет все изображения для поста
    public void removeImagesWithPost(Long postId){
        List<ImageModel> imageModels = getImagesByPostId(postId);
        if (imageModels.isEmpty()) {
            log.warn("Не найдено изображений для поста с id: {}", postId);
        }
        for (ImageModel image : imageModels) {
            removeFromDisk(image);
        }
    }



    // Удаляет изображение с диска
    private void removeFromDisk(ImageModel image) {
        if (image == null || image.getImageUrl() == null) {
            log.warn("Путь к изображению для удаления отсутствует: {}", image);
            return;
        }

        String currentImagePath = (imagePath.startsWith("/opt")) ? imagePath : "file:" + imagePath;

        File file = new File(imagePath + image.getName());

        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                log.info("Изображение с путем {} успешно удалено с диска", image.getImageUrl());
            } else {
                log.error("Не удалось удалить файл с пути: {}", image.getImageUrl());
            }
        } else {
            log.warn("Файл по пути {} не найден, удаление невозможно", image.getImageUrl());
        }
    }

}
