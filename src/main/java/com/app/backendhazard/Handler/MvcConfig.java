package com.app.backendhazard.Handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final String BASE_URL = "upload";

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        exposeDir(registry);
    }

    private void exposeDir(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(BASE_URL);
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + BASE_URL + "/**").addResourceLocations("file:/"+ uploadPath + "/");
    }
}
