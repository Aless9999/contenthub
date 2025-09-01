package org.macnigor.contenthub.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.Data;
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

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageSize;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // ✅ изображение принадлежит пользователю

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post; // ✅ и посту (опционально, если картинка в посте)
}

