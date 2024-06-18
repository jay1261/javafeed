package com.sparta.javafeed.controller;

import com.sparta.javafeed.dto.NewsfeedRequestDto;
import com.sparta.javafeed.service.NewsfeedService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = NewsfeedController.class)
class NewsfeedControllerTest extends ControllerTest {

    @MockBean
    NewsfeedService newsfeedService;

    @Test
    void 게시물_등록_성공() throws Exception {
        //given
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("title", "description", null);

        // dto -> json string
        String json = objectMapper.writeValueAsString(requestDto);

        //when then
        mvc.perform(post("/posts")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
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
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("title", "description", null);

        String json = objectMapper.writeValueAsString(requestDto);

        //when then
        mvc.perform(put("/posts/1")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 게시글_삭제_성공() throws Exception {
        //given

        //when then
        mvc.perform(delete("/posts/1"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}