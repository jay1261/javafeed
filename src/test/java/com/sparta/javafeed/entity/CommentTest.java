package com.sparta.javafeed.entity;

import com.sparta.javafeed.enums.UserRole;
import com.sparta.javafeed.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    Comment comment;

    @BeforeEach
    void setComment() {
        User user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.USER);
        Newsfeed newsfeed = new Newsfeed("title", "description", user);
        this.comment = new Comment(user, newsfeed, "comment description");
    }

    @Test
    void validate_성공(){
        // given
        User requestUser = new User();
        requestUser.setId(1L);
        requestUser.setUserRole(UserRole.USER);

        // when then
        comment.validate(requestUser);
    }

    @Test
    void validate_실패_다른유저(){
        // given
        User requestUser = new User();
        requestUser.setId(2L);
        requestUser.setUserRole(UserRole.USER);

        //when then
        assertThrows(CustomException.class, () -> comment.validate(requestUser));
    }

    @Test
    void update_성공(){
        //given
        String content = "업데이트 description";
        //when
        comment.update(content);
        //then
        assertEquals(content, comment.getDescription());
    }

    @Test
    void increaseLikeCnt_성공(){
        // given
        Long like = 1L;
        // when
        comment.increaseLikeCnt();
        // then
        assertEquals(like, comment.getLikeCnt());
    }

    @Test
    void decreaseLikeCnt_성공(){
        // given
        comment.increaseLikeCnt();
        Long like = 0L;
        // when
        comment.decreaseLikeCnt();
        // then
        assertEquals(like, comment.getLikeCnt());
    }
}