package com.cinemaone.platform.config;

import com.cinemaone.platform.web.PathLocaleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PathLocaleInterceptor())
            .addPathPatterns("/en/**", "/fr/**", "/ar/**", "/en", "/fr", "/ar", "/watch/**");
    }
}
