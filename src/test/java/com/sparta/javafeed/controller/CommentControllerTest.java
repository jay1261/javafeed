package com.sparta.javafeed.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.javafeed.config.SecurityConfig;
import com.sparta.javafeed.dto.CommentRequestDto;
import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.enums.UserRole;
import com.sparta.javafeed.security.UserDetailsImpl;
import com.sparta.javafeed.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {
    // MockMvc 사용
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    CommentService commentService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        SignupRequestDto requestDto = new SignupRequestDto("user111111", "1q2w3e4r!@#$", "tester", "test@mail.com");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("1q2w3e4r!@#$");
        User user = new User(requestDto, encodedPassword);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // 시큐리티 authentication 에 UserDetails 객체 추가
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(), userDetails.getAuthorities()));
    }


    @Test
    void 댓글_등록_성공() throws Exception {
        //given
        CommentRequestDto requestDto = new CommentRequestDto("description");

        String data = objectMapper.writeValueAsString(requestDto);

        //when then
        mvc.perform(post("/posts/1/comments")
                        .content(data)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 댓글_조회_성공() throws Exception {
        //when then
        mvc.perform(get("/posts/1/comments"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 댓글_수정_성공() throws Exception {
        // given
        CommentRequestDto requestDto = new CommentRequestDto("update description");

        String data = objectMapper.writeValueAsString(requestDto);

        //when then
        mvc.perform(put("/posts/1/comments/1")
                        .content(data)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 댓글_삭제_성공() throws Exception {
        //given

        //when then
        mvc.perform(delete("/posts/1/comments/1")
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}