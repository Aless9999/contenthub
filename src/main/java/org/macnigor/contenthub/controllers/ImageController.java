package org.macnigor.contenthub.controllers;

import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.services.ImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /*@GetMapping("/images/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        log.info("Запрос изображения с id={}", id);

        // Попытка найти изображение по id
        ImageModel image = imageService.findById(id);

        if (image == null) {
            // Логирование и возврат ошибки 404 если изображение не найдено
            log.warn("Изображение с id={} не найдено.", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Изображение с id={} найдено, отправка данных клиенту.", id);

        // Получаем изображение в формате BLOB из базы данных
        byte[] bytes = image.getImageSize();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)  // Здесь можно менять на другой тип изображения, если необходимо
                .body(bytes);
    }*/
    @GetMapping("/images/{id}")
    @ResponseBody
    public ResponseEntity<String> getImage(@PathVariable Long id) {
        log.info("Запрос изображения с id={}", id);

        // Попытка найти изображение по id
        ImageModel image = imageService.findById(id);

        if (image == null) {
            // Логирование и возврат ошибки 404 если изображение не найдено
            log.warn("Изображение с id={} не найдено.", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Изображение с id={} найдено, отправка данных клиенту.", id);

        // Получаем изображение в формате BLOB из базы данных
        String urlImage = image.getImageUrl();

        return ResponseEntity.ok(urlImage)
                ;
    }
}
