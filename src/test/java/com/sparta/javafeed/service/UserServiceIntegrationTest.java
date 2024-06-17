package com.sparta.javafeed.service;

import com.sparta.javafeed.dto.*;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.enums.UserStatus;
import com.sparta.javafeed.jwt.JwtUtil;
import com.sparta.javafeed.repository.ProfileRepository;
import com.sparta.javafeed.repository.UserRepository;
import com.sparta.javafeed.util.S3Util;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 서버의 PORT 를 랜덤으로 설정합니다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다. (각각의 테스트들이 필드를 공유함)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceIntegrationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    S3Util s3Util;

    @Autowired
    UserService userService;

    // 테스트 공통으로 사용할 유저 Account
    String testUserAccountId = "testUser7";

    @Test
    @Order(1)
    void 회원가입(){
        // given
        SignupRequestDto requestDto = new SignupRequestDto(testUserAccountId, "1q2w3e4r!@#$", "tester", testUserAccountId +"@gmail.com");

        //when
        SignupResponseDto signupResponseDto = userService.signupUser(requestDto);

        // then
        assertEquals(signupResponseDto.getName(),requestDto.getName());
        assertEquals(signupResponseDto.getAccountId(),requestDto.getAccountId());
        assertEquals(signupResponseDto.getEmail(),requestDto.getEmail());
    }

    @Test
    @Order(2)
    void 회원정보_조회(){
        //given
        // when
        UserInfoResponseDto user = userService.getUser(testUserAccountId);
        // given
        assertEquals(testUserAccountId, user.getAccountId());
    }

    @Test
    @Order(3)
    void 회원정보_수정(){
        // given
        UserInfoRequestDto requestDto = new UserInfoRequestDto("newName", "new intro");
        // when
        userService.updateUser(requestDto, testUserAccountId);
        User byAccountId = userService.findByAccountId(testUserAccountId);

        // then
        assertEquals(requestDto.getName(), byAccountId.getName());
        assertEquals(requestDto.getIntro(), byAccountId.getIntro());
    }

    @Test
    @Order(4)
    void 비밀번호_변경(){
        //given
        PasswordUpdateDto reqeustDto = new PasswordUpdateDto("1q2w3e4r!@#$", "1q2w3e4r!!!!");

        // when
        userService.updatePassword(reqeustDto, testUserAccountId);
        User byAccountId = userService.findByAccountId(testUserAccountId);

        //then
        assertTrue(passwordEncoder.matches(reqeustDto.getNewPassword(), byAccountId.getPassword()));
    }

    @Test
    @Order(5)
    void 이메일_전송(){
        //given
        User byAccountId = userService.findByAccountId(testUserAccountId);
        LocalDateTime now = LocalDateTime.now();
        // when
        userService.updateUserEmailSent(byAccountId.getEmail(), now);
        byAccountId = userService.findByAccountId(testUserAccountId);

        //then
        assertEquals(now,byAccountId.getEmailSentAt());
    }

    @Test
    @Order(6)
    void 회원_인증상태_변경(){
        User byAccountId = userService.findByAccountId(testUserAccountId);
        EmailVerifyCheckRequestDto requestDto = new EmailVerifyCheckRequestDto(byAccountId.getEmail(), "authNum");

        //when
        userService.updateUserStatus(requestDto);
        byAccountId = userService.findByAccountId(testUserAccountId);

        //then
        assertEquals(UserStatus.ACTIVE, byAccountId.getUserStatus());
    }

    @Test
    @Order(7)
    void 회원탈퇴() {

        // given
        PasswordReqeustDto reqeustDto = new PasswordReqeustDto("1q2w3e4r!!!!");

        //when
        userService.deactiveUser(reqeustDto, testUserAccountId);
        User byAccountId = userService.findByAccountId(testUserAccountId);

        //then
        assertEquals(UserStatus.DEACTIVATE, byAccountId.getUserStatus());
    }

}
