package com.com6103.email.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

/**
 * Configuration for the static resource
 */
@Configuration
public class WebResourceConfig extends WebMvcConfigurationSupport {

    @Value("${audio.handler}")
    String audioHandler;
    @Value("${audio.locations}")
    String audioLocations;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // The resource path for javascript
        registry.addResourceHandler("/javascript/**")
                .addResourceLocations("classpath:/static/javascript/");
        // The resource path for stylesheet
        registry.addResourceHandler("/stylesheet/**")
                .addResourceLocations("classpath:/static/stylesheet/");
        // The resource path for audio file
        registry.addResourceHandler(audioHandler)
                .addResourceLocations(audioLocations);
        super.addResourceHandlers(registry);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
