package com.sparta.javafeed.controller;

import com.sparta.javafeed.dto.CommentRequestDto;
import com.sparta.javafeed.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest extends ControllerTest {

    @MockBean
    CommentService commentService;

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