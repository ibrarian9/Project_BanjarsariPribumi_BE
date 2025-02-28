package com.app.backendhazard.Handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private final FolderImageApp folderImageApp;

    public MvcConfig(FolderImageApp folderImageApp) {
        this.folderImageApp = folderImageApp;
    }

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {

        String BASE_URL = folderImageApp.getFolderPath() + "ReportPic/";

        registry.addResourceHandler("/ReportPic/**")
                .addResourceLocations("file:" + BASE_URL + "/");
    }
}
