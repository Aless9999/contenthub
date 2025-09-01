package org.macnigor.contenthub.entity;

import com.fasterxml.jackson.annotation.*;
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
    private String title;

    private String caption;
    private String location;

    private Integer likes = 0;

    @ElementCollection
    @CollectionTable(name = "post_likes_user", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "username")
    private Set<String> likesUser = new HashSet<>();
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // ✅ Автор поста

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageModel> images = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createDate;

    @PrePersist
    protected void onCreated() {
        this.createDate = LocalDateTime.now();
    }
}
