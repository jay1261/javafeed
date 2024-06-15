package com.sparta.javafeed.entity;

import com.sparta.javafeed.dto.NewsfeedRequestDto;
import com.sparta.javafeed.enums.UserRole;
import com.sparta.javafeed.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedTest {

    Newsfeed newsfeed;

    @BeforeEach
    void setNewsFeed(){
        User user = new User();
        user.setId(1L);
        user.setName("tester1");
        user.setUserRole(UserRole.USER);
        newsfeed = new Newsfeed("title", "description", user);
    }

    @Test
    void update_성공(){
        //given
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("update title", "update description", null);
        //when
        newsfeed.update(requestDto);
        //then
        assertEquals(requestDto.getTitle(), newsfeed.getTitle());
        assertEquals(requestDto.getDescription(), newsfeed.getDescription());
    }

    @Test
    void userValidate_성공(){
        //given
        User requestUser = new User();
        requestUser.setId(1L);
        requestUser.setName("tester1");
        requestUser.setUserRole(UserRole.USER);
        //when
        //then
        newsfeed.userValidate(requestUser);
    }

    @Test
    void userValidate_실패_다른유저(){
        //given
        User requestUser = new User();
        requestUser.setId(1L);
        requestUser.setName("tester2");
        requestUser.setUserRole(UserRole.USER);
        //when
        //then
        assertThrows(CustomException.class, () -> newsfeed.userValidate(requestUser));
    }

}