package com.example.Loadbalancer.config;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Promenite putanju ako je potrebno
                .allowedOrigins("http://localhost:5173") // Dozvolite vaš frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Dozvolite metode
                .allowCredentials(true); // Ako je potrebno

        registry.addMapping("/ws/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowCredentials(true);
                
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:images/");

        registry.addResourceHandler("/compressedImages/**")
                .addResourceLocations("file:compressedImages/");
    }
}
