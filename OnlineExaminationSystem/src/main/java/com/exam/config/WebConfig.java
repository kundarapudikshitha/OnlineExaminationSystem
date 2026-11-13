
package com.exam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminSessionInterceptor adminInterceptor;
    private final StudentSessionInterceptor studentInterceptor;

    public WebConfig(
            AdminSessionInterceptor adminInterceptor,
            StudentSessionInterceptor studentInterceptor) {

        this.adminInterceptor = adminInterceptor;
        this.studentInterceptor = studentInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Protect all admin routes except login
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");

        // Protect student routes
        registry.addInterceptor(studentInterceptor)
                .addPathPatterns(
                        "/dashboard",
                        "/available-exams",
                        "/exam-instructions/**",
                        "/start-exam/**",
                        "/submit-exam",
                        "/submit-exam-expired/**",
                        "/my-results",
                        "/logout"
                );
    }
}
