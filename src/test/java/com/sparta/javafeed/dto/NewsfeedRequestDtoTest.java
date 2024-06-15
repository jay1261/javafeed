package com.sparta.javafeed.dto;

import com.sparta.javafeed.entity.Newsfeed;
import com.sparta.javafeed.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedRequestDtoTest {

    @Test
    void toEntity(){
        //given
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto("title", "description", null);
        User user = new User();
        user.setId(1L);
        user.setName("tester");

        //when
        Newsfeed entity = newsfeedRequestDto.toEntity(user);

        //then
        assertEquals(user, entity.getUser());
        assertEquals(newsfeedRequestDto.getTitle(), entity.getTitle());
        assertEquals(newsfeedRequestDto.getDescription(), entity.getDescription());
    }
}