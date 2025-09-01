package org.macnigor.contenthub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.Data;

@Data
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
    @JsonIgnoreProperties
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // ✅ изображение принадлежит пользователю
    @JsonIgnoreProperties
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post; // ✅ и посту (опционально, если картинка в посте)
}

