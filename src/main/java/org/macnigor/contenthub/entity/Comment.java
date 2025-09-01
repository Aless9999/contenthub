package org.macnigor.contenthub.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post; // ✅ коммент принадлежит посту

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // ✅ коммент принадлежит пользователю

    @Column(columnDefinition = "text", nullable = false)
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createDate;

    @PrePersist
    protected void onCreated() {
        this.createDate = LocalDateTime.now();
    }
}
