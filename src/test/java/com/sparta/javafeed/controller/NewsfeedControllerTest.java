package com.sparta.javafeed.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.javafeed.config.SecurityConfig;
import com.sparta.javafeed.dto.NewsfeedRequestDto;
import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.enums.UserRole;
import com.sparta.javafeed.jwt.JwtAuthenticationFilter;
import com.sparta.javafeed.jwt.JwtAuthorizationFilter;
import com.sparta.javafeed.jwt.JwtExceptionFilter;
import com.sparta.javafeed.security.UserDetailsImpl;
import com.sparta.javafeed.service.NewsfeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    // MockMvc 사용
    private MockMvc mvc;
    // 가짜 인증을 위한 Principal 필요
    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    NewsfeedService newsfeedService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter())) // 테스트용으로 만든 가짜 필터를 넣어줌
                .build();
    }

    private void mockUserSetup() {
        // 컨트롤러에서 @AuthenticationPrincipal에서 유저를 뽑아오기 위해 가짜 유저를 만들어서 Principal에  넣어준다
        // Mock 테스트 유져 생성
        SignupRequestDto requestDto = new SignupRequestDto("user111111", "1q2w3e4r!@#$", "tester", "test@mail.com");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encoded = passwordEncoder.encode(requestDto.getPassword());

        User testUser = new User(requestDto, encoded);
        testUser.setUserRole(UserRole.USER);

        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    void 게시물_등록_성공() throws Exception {
        //given
        this.mockUserSetup();
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("title", "description", null);

        // dto -> json string
        String json = objectMapper.writeValueAsString(requestDto);

        //when then
        mvc.perform(post("/posts")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 게시물_조회_성공() throws Exception{
        //given
        String page = "1";
        String searchStartDate = "";
        String searchEndDate = "";

        //when then
        mvc.perform(get("/posts")
                        .param("page", page)
                        .param("searchStartDate", searchStartDate)
                        .param("searchEndDate", searchEndDate)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 게시글_수정_성공() throws Exception {
        //given
        this.mockUserSetup();
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("title", "description", null);

        String json = objectMapper.writeValueAsString(requestDto);

        //when then
        mvc.perform(put("/posts/1")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 게시글_삭제_성공() throws Exception {
        //given
        this.mockUserSetup();

        //when then
        mvc.perform(delete("/posts/1")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}