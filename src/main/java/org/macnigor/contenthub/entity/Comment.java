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

    @ManyToOne(fetch = FetchType.EAGER) // Many comments belong to one post
    private Post post; // The post that this comment belongs to

    @Column(nullable = false)
    private String username; // The username of the person who made the comment

    @Column(nullable = false)
    private Long userId; // The ID of the user who made the comment (you may want to link it to the User table in a real-world app)

    @Column(columnDefinition = "text", nullable = false)
    private String message; // The content of the comment

    @Column(updatable = false) // The creation date shouldn't be updated once set
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Formatting the date for JSON output
    private LocalDateTime createDate; // The date the comment was created

    @PrePersist
    protected void onCreated() {
        this.createDate = LocalDateTime.now(); // Automatically set the create date before persisting
    }
}
