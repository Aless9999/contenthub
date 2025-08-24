package org.macnigor.contenthub.services;

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
    }

