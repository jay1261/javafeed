package com.sparta.javafeed.dto;

import com.sparta.javafeed.entity.Newsfeed;
import com.sparta.javafeed.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedResponseDtoTest {

    @Test
    void toDto(){
        //given
        User user = new User();
        user.setId(1L);
        user.setName("tester");
        user.setAccountId("tester1");
        Newsfeed newsfeed = new Newsfeed("title", "description", user);
        //when
        NewsfeedResponseDto dto = NewsfeedResponseDto.toDto(newsfeed);
        //then
        assertEquals(newsfeed.getTitle(),dto.getTitle());
        assertEquals(newsfeed.getDescription(),dto.getDescription());
        assertEquals(user.getAccountId(),dto.getAccountId());
    }
}