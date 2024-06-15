package com.sparta.javafeed.controller;

import com.sparta.javafeed.config.SecurityConfig;
import com.sparta.javafeed.jwt.JwtAuthenticationFilter;
import com.sparta.javafeed.jwt.JwtAuthorizationFilter;
import com.sparta.javafeed.jwt.JwtExceptionFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(
        controllers = NewsfeedController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
class NewsfeedControllerTest {

}