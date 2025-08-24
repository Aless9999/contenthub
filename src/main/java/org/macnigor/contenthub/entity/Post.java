package org.macnigor.contenthub.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Title of the post

    private String caption; // Caption or short description for the post

    private String location; // Location (optional) where the post was created or relevant to

    private Integer likes = 0; // Number of likes for the post (default value set to 0)

    @ElementCollection // Used for storing simple collection types (like Set)
    @CollectionTable(name = "post_likes_user", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "username") // Column will store the usernames of users who liked the post
    private Set<String> likesUser = new HashSet<>(); // Set of users who liked the post

    @ManyToOne(fetch = FetchType.LAZY) // Many posts can belong to one user (one-to-many relation with User)
    private User user; // The user who created this post

    @OneToMany(mappedBy = "post", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // Comments on the post

    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Formatting the date as string for JSON output
    private LocalDateTime createDate; // Date the post was created

    @PrePersist
    protected void onCreated() {
        this.createDate = LocalDateTime.now(); // Set the creation date before the post is saved
    }
}
