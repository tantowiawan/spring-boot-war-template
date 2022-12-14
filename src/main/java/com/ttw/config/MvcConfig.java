package com.ttw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;


@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("assets/**", "web/**")
                .addResourceLocations("file:web/", "file:web/assets/", "file:assets/")
                .setCachePeriod(3600 * 24 * 30)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}
