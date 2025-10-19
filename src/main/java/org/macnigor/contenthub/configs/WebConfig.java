package org.macnigor.contenthub.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.images.url}")
    private String imageUrl;

    @Value("${app.images.path}")
    private String imageDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + imageDir;
        registry.addResourceHandler(imageUrl)
                .addResourceLocations(location);

        log.info("📂 Настройка статических ресурсов: {} → {}", imageUrl, location);
    }
}


