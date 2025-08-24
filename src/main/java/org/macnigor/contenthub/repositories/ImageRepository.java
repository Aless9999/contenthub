package org.macnigor.contenthub.repositories;

import org.macnigor.contenthub.entity.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageModel, Long> {

    // Найти изображение по имени
    Optional<ImageModel> findByName(String name);

    // Найти изображение по userId (если вы хотите искать изображения пользователя)
    List<ImageModel> findByUserId(Long userId);

    // Найти изображение по postId (если изображения привязаны к постам)
    List<ImageModel> findByPostId(Long postId);

    // Найти изображение по ID
    Optional<ImageModel> findById(Long id);
}

