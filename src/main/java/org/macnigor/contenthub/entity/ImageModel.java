package org.macnigor.contenthub.entity;

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
    private String name; // Name of the image (e.g., file name)
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageSize; // Size of the image as a byte array (you can also store size as a Long if needed)

    private Long userId;
    private Long postId;

}
