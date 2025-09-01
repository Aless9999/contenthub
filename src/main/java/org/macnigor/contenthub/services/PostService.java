package org.macnigor.contenthub.services;

import org.macnigor.contenthub.dto.CommentDto;
import org.macnigor.contenthub.dto.ImageDto;
import org.macnigor.contenthub.dto.PostDto;
import org.macnigor.contenthub.entity.ImageModel;
import org.macnigor.contenthub.entity.Post;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.repositories.ImageRepository;
import org.macnigor.contenthub.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {


    private final PostRepository postRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public PostService(PostRepository postRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public void createPost(PostDto postDto, User user) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setCaption(postDto.getCaption());
        post.setLocation(postDto.getLocation());
        post.setUser(user);

        // Сохраняем сам пост
        postRepository.save(post);

        }

    public List<Post> getAllPostsForUser(Long userId) {
        return getAllPosts().stream()
                .filter(post -> post.getUser().getId().equals(userId))
                .collect(Collectors.toUnmodifiableList());
    }

    public Post findById(Long postId) {
        return postRepository.findPostById(postId).orElseThrow(()->new RuntimeException("Post with id="+postId +"not found"));
    }

    public Post getPostById(Long id) {
        return findById(id);
    }

    public List<PostDto> getAllPostDtos() {
        return postRepository.findAll().stream()
                .map(post -> {
                    PostDto dto = new PostDto();
                    dto.setId(post.getId());
                    dto.setTitle(post.getTitle());
                    dto.setCaption(post.getCaption());
                    dto.setLocation(post.getLocation());

                    dto.setImages(
                            post.getImages().stream()
                                    .map(img -> {
                                        ImageDto i = new ImageDto();
                                        i.setId(img.getId());
                                        return i;
                                    })
                                    .toList()
                    );

                    dto.setComments(
                            post.getComments().stream()
                                    .map(c -> {
                                        CommentDto cd = new CommentDto();
                                        cd.setUsername(c.getUser().getUsername());
                                        cd.setMessage(c.getMessage());
                                        return cd;
                                    })
                                    .toList()
                    );

                    return dto;
                })
                .toList();
    }

}

