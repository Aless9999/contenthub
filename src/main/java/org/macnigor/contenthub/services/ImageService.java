package org.macnigor.contenthub.services;

import org.macnigor.contenthub.dto.ImageDto;
import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.repositories.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImageService {
    private ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<ImageModel> getImagesByPostId(Long id) {
        return imageRepository.findByPostId(id);
    }

    public void createImage(MultipartFile image, Post post, User user) {
        try {
            // Создаем новый объект ImageModel
            ImageModel newImage = new ImageModel();

            // Устанавливаем имя файла
            newImage.setName(image.getOriginalFilename());

            // Сохраняем файл как массив байтов
            newImage.setImageSize(image.getBytes());

            // Устанавливаем пользователя и пост
            newImage.setUser(user);
            newImage.setPost(post);

            // Сохраняем изображение в базе данных
            imageRepository.save(newImage);
        } catch (IOException e) {
            // Обработка ошибок
            e.printStackTrace();
            // В реальном приложении стоит использовать обработку ошибок (например, выбрасывать исключения или возвращать ошибку в ответ)
        }
    }

    public ImageModel findById(Long id) {
        return imageRepository.findById(id).orElseThrow(()->new RuntimeException("Image with id="+id+" not found"));
    }
}
