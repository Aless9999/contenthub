package org.macnigor.contenthub.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ImageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;


    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // ✅ изображение принадлежит пользователю

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post; // ✅ и посту (опционально, если картинка в посте)
}

