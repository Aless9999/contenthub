package org.macnigor.contenthub.repositories;

import org.macnigor.contenthub.entity.Comment;
import org.macnigor.contenthub.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Найти комментарии по посту
    List<Comment> findByPost(Post post);

    // Найти комментарии по пользователю
    List<Comment> findByUserId(Long userId);



    // Найти комментарии, созданные после определенной даты
    List<Comment> findByCreateDateAfter(LocalDateTime createDate);

    // Найти комментарии по сообщению (поиск по части текста)
    List<Comment> findByMessageContaining(String message);
}

