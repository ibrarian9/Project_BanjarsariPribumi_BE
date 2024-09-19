package com.app.backendhazard.Handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        exposeDir(registry);
    }

    private void exposeDir(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("upload");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + "upload" + "/**").addResourceLocations("file:/"+ uploadPath + "/");
    }
}
