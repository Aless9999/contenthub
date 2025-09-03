package org.macnigor.contenthub.repositories;

import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Найти пост по заголовку
    Optional<Post> findPostByTitle(String title);

    // Найти пост по автору
    Optional<Post> findPostByUser(User user);

    // Найти пост по его ID
    Optional<Post> findPostById(Long id);

    // Найти пост по локации
    List<Post> findPostsByLocation(String location);

    // Найти все посты, которые были опубликованы после определенной даты
    List<Post> findPostsByCreateDateAfter(LocalDateTime createDate);

    Optional<Post> findByUserId(Long userId);
}
