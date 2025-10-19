package org.macnigor.contenthub.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.images.url}")
    private String imageUrl;

    @Value("${app.images.path}")
    private String imagePath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String currentImagePath = (imagePath.startsWith("/opt")) ? imagePath : "file:" + imagePath;
        registry.addResourceHandler(imageUrl)

                .addResourceLocations(currentImagePath);
    }
}
