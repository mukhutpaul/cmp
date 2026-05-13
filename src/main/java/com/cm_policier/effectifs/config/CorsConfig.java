package com.cm_policier.effectifs.config;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        // CorsConfiguration config = new CorsConfiguration();

        // // ✅ Frontend autorisé
        // config.addAllowedOrigin("http://localhost:3000");
        // config.addAllowedOrigin("http://10.138.215.185:3000");

        // // ✅ Headers autorisés
        // config.addAllowedHeader("*");

        // // ✅ Méthodes autorisées
        // config.addAllowedMethod("*");

        // // ✅ JWT / cookies
        // config.setAllowCredentials(true);

        // UrlBasedCorsConfigurationSource source =
        //         new UrlBasedCorsConfigurationSource();

        // source.registerCorsConfiguration(
        //         "/**",
        //         config
        // );

        CorsConfiguration config = new CorsConfiguration();

        // ✅ Autoriser tous les frontends du réseau
        config.setAllowedOriginPatterns(List.of("*"));

        // ✅ Headers
        config.setAllowedHeaders(List.of("*"));

        // ✅ Méthodes
        config.setAllowedMethods(List.of("*"));

        // ✅ JWT / cookies
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);


        return new CorsFilter(source);
    }
}