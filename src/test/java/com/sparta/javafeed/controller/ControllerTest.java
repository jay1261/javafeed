package com.sparta.javafeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest
public class ControllerTest {
    // MockMvc 사용
    protected MockMvc mvc;
    @Autowired
    protected WebApplicationContext context;
    @Autowired
    protected ObjectMapper objectMapper;


    User getTestUser(){
        SignupRequestDto requestDto = new SignupRequestDto("user111111", "1q2w3e4r!@#$", "tester", "test@mail.com");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("1q2w3e4r!@#$");

        return new User(requestDto, encodedPassword);
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        User user = getTestUser();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // 시큐리티 authentication 에 UserDetails 객체 추가
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(), userDetails.getAuthorities()));
    }
}
