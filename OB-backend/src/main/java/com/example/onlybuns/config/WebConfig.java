package com.example.onlybuns.config;
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
                
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Konfiguriši putanju za slike
        registry.addResourceHandler("/images/**") // Putanja koju koristiš u frontend-u
                .addResourceLocations("file:images/"); // Prava putanja na serveru
    }
}
