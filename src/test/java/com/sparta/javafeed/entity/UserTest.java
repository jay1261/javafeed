package com.sparta.javafeed.entity;

import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.dto.UserInfoRequestDto;
import com.sparta.javafeed.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


class UserTest {
    User user;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUser(){
        SignupRequestDto requestDto = new SignupRequestDto("user111111", "1q2w3e4r!@#$", "tester", "test@mail.com");
        String encoded = passwordEncoder.encode(requestDto.getPassword());

        user = new User(requestDto, encoded);
    }

    @Test
    void updateUserStatus_성공(){
        //given
        LocalDateTime userStatusModifiedAt = user.getUserStatusModifiedAt();
        UserStatus deactivate = UserStatus.DEACTIVATE;
        //when
        user.updateUserStatus(deactivate);
        //then
        assertNotEquals(userStatusModifiedAt, user.getUserStatusModifiedAt());
        assertEquals(deactivate, user.getUserStatus());
    }

    @Test
    void saveRefreshToken_성공(){
        // given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTExMTExIiwiYXV0aCI6IlVTRVIiLCJleHAiOjE3MTk2Mzg5ODEsImlhdCI6MTcxODQyOTM4MX0.jMfGMxoKM7-IdoBd1HaiyROgq991UW8Ts6g6zKcF5Dk";

        //when
        user.setRefreshToken(refreshToken);

        // then
        assertEquals(refreshToken, user.getRefreshToken());
    }

    @Test
    void checkRefreshToken_성공(){
        //given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTExMTExIiwiYXV0aCI6IlVTRVIiLCJleHAiOjE3MTk2Mzg5ODEsImlhdCI6MTcxODQyOTM4MX0.jMfGMxoKM7-IdoBd1HaiyROgq991UW8Ts6g6zKcF5Dk";
        user.setRefreshToken(refreshToken);

        //when then
        assertTrue(user.checkRefreshToken(refreshToken));
    }

    @Test
    void checkRefreshToken_실패_토큰없음(){
        //given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTExMTExIiwiYXV0aCI6IlVTRVIiLCJleHAiOjE3MTk2Mzg5ODEsImlhdCI6MTcxODQyOTM4MX0.jMfGMxoKM7-IdoBd1HaiyROgq991UW8Ts6g6zKcF5Dk";

        //when then
        assertFalse(user.checkRefreshToken(refreshToken));
    }

    @Test
    void checkRefreshToken_실패_다른토큰(){
        //given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTExMTExIiwiYXV0aCI6IlVTRVIiLCJleHAiOjE3MTk2Mzg5ODEsImlhdCI6MTcxODQyOTM4MX0.jMfGMxoKM7-IdoBd1HaiyROgq991UW8Ts6g6zKcF5Dk";
        user.setRefreshToken(refreshToken);
        refreshToken = "eyJhbGciOiJIUzI1NiJ8.eyJzdWIiOiJ1c2VyMTExMTExIiwiYXV0aCI6IlVTRVIiLCJleHAiOjE3MTk2Mzg5ODEsImlhdCI6MTcxODQyOTM4MX0.jMfGMxoKM7-IdoBd1HaiyROgq991UW8Ts6g6zKcF5Dk";

        //when then
        assertFalse(user.checkRefreshToken(refreshToken));
    }

    @Test
    void updateUserInfo_성공(){
        //given
        UserInfoRequestDto requestDto = new UserInfoRequestDto("newName", "new intro");
        //when
        user.updateUserInfo(requestDto);
        //then
        assertEquals(requestDto.getName(), user.getName());
        assertEquals(requestDto.getIntro(), user.getIntro());
    }

    @Test
    void updatePassword_성공(){
        //given
        String newPassword = "newPassword";
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        //when
        user.updatePassword(encodedNewPassword);

        //then
        assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));
    }

}