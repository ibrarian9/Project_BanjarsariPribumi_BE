package com.app.backendhazard.Handler;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class AppInit implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        final AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        final ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", new DispatcherServlet(appContext));
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
        File uploadDir = new File(System.getProperty("java.io.tmpdir"));
        MultipartConfigElement configElement = new MultipartConfigElement(uploadDir.getAbsolutePath(), 10000, 10000 * 2, 10000 / 2);
        registration.setMultipartConfig(configElement);

    }
}
