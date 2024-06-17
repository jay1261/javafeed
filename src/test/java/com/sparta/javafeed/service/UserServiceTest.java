package com.sparta.javafeed.service;

import com.sparta.javafeed.dto.*;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.enums.ErrorType;
import com.sparta.javafeed.enums.UserStatus;
import com.sparta.javafeed.exception.CustomException;
import com.sparta.javafeed.jwt.JwtUtil;
import com.sparta.javafeed.repository.ProfileRepository;
import com.sparta.javafeed.repository.UserRepository;
import com.sparta.javafeed.util.S3Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    S3Util s3Util;

    UserService userService;

    @BeforeEach
    void setUserService(){
        userService = new UserService(userRepository, profileRepository, jwtUtil, passwordEncoder, s3Util);
    }

    User getTestUser(){
        SignupRequestDto requestDto = new SignupRequestDto("user111111", "1q2w3e4r!@#$", "tester", "test@mail.com");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("1q2w3e4r!@#$");

        return new User(requestDto, encodedPassword);
    }


    @Nested
    @DisplayName("회원가입")
    class 회원가입 {
        @Test
        void 회원가입_성공() {
            //given
            SignupRequestDto requestDto = new SignupRequestDto("user111111", "1q2w3e4r!@#$", "tester", "test@mail.com");
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode("1q2w3e4r!@#$");

            given(userRepository.findByAccountId(requestDto.getAccountId())).willReturn(Optional.empty());
            given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.empty());
            given(passwordEncoder.encode(requestDto.getPassword())).willReturn(encodedPassword);

            // when
            SignupResponseDto signupResponseDto = userService.signupUser(requestDto);

            // then
            assertEquals(requestDto.getAccountId(), signupResponseDto.getAccountId());
            assertEquals(encodedPassword, signupResponseDto.getPassword());
            assertEquals(requestDto.getEmail(), signupResponseDto.getEmail());
            assertEquals(requestDto.getName(), signupResponseDto.getName());
        }

        @Test
        void 회원가입_실패_id중복() {
            //given
            SignupRequestDto requestDto = new SignupRequestDto("user111111", "1q2w3e4r!@#$", "tester", "test@mail.com");
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode("1q2w3e4r!@#$");

            given(userRepository.findByAccountId(requestDto.getAccountId())).willReturn(Optional.of(new User()));

            //when
            CustomException result = assertThrows(CustomException.class, () -> userService.signupUser(requestDto));

            //then
            assertEquals(ErrorType.DUPLICATE_ACCOUNT_ID, result.getErrorType());
            assertEquals(ErrorType.DUPLICATE_ACCOUNT_ID.getMessage(), result.getErrorType().getMessage());
        }

        @Test
        void 회원가입_실패_email중복() {
            //given
            SignupRequestDto requestDto = new SignupRequestDto("user111111", "1q2w3e4r!@#$", "tester", "test@mail.com");
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode("1q2w3e4r!@#$");

            given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(new User()));

            //when
            CustomException result = assertThrows(CustomException.class, () -> userService.signupUser(requestDto));

            //then
            assertEquals(ErrorType.DUPLICATE_EMAIL, result.getErrorType());
            assertEquals(ErrorType.DUPLICATE_EMAIL.getMessage(), result.getErrorType().getMessage());
        }
    }

    @Nested
    class 회원탈퇴 {
        @Test
        void 회원탈퇴_성공() {
            // given
            User testUser = getTestUser();
            PasswordReqeustDto requestDto = new PasswordReqeustDto("1q2w3e4r!@#$");

            given(userRepository.findByAccountId(testUser.getAccountId())).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(requestDto.getPassword(), testUser.getPassword())).willReturn(true);

            //when
            userService.deactiveUser(requestDto, testUser.getAccountId());

            //then
            assertEquals(UserStatus.DEACTIVATE, testUser.getUserStatus());
        }

        @Test
        void 회원탈퇴_실패_이미_탈퇴한_회원() {
            // given
            User testUser = getTestUser();
            testUser.setUserStatus(UserStatus.DEACTIVATE);
            PasswordReqeustDto requestDto = new PasswordReqeustDto("1q2w3e4r!@#$");

            given(userRepository.findByAccountId(testUser.getAccountId())).willReturn(Optional.of(testUser));

            //when
            CustomException customException = assertThrows(CustomException.class, () -> userService.deactiveUser(requestDto, testUser.getAccountId()));

            //then
            assertEquals(ErrorType.DEACTIVATE_USER, customException.getErrorType());
        }

        @Test
        void 회원탈퇴_실패_다른비밀번호() {
            // given
            User testUser = getTestUser();
            PasswordReqeustDto requestDto = new PasswordReqeustDto("1q2w3e4r!!!!");

            given(userRepository.findByAccountId(testUser.getAccountId())).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(requestDto.getPassword(), testUser.getPassword())).willReturn(false);

            //when
            CustomException customException = assertThrows(CustomException.class, () -> userService.deactiveUser(requestDto, testUser.getAccountId()));

            //then
            assertEquals(ErrorType.INVALID_PASSWORD, customException.getErrorType());
            assertEquals(ErrorType.INVALID_PASSWORD.getMessage(), customException.getErrorType().getMessage());
        }
    }
    @Test
    void 회원정보_수정_성공(){
        // given
        User testUser = getTestUser();
        UserInfoRequestDto requestDto = new UserInfoRequestDto("new name", "user info update");

        given(userRepository.findByAccountId(testUser.getAccountId())).willReturn(Optional.of(testUser));
        //when
        userService.updateUser(requestDto, testUser.getAccountId());

        // then
        assertEquals(requestDto.getName(), testUser.getName());
        assertEquals(requestDto.getIntro(), testUser.getIntro());
    }

    @Nested
    class 비밀번호_변경 {
        @Test
        void 비밀번호_변경_성공() {
            // given
            User testUser = getTestUser();

            String currentPassword = "1q2w3e4r!@#$";
            String newPassword = "update!@#$";
            PasswordEncoder encoder = new BCryptPasswordEncoder();

            PasswordUpdateDto passwordUpdateDto = new PasswordUpdateDto(currentPassword, newPassword);

            String encodedNewPassword = encoder.encode(newPassword);

            given(userRepository.findByAccountId(testUser.getAccountId())).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(), testUser.getPassword())).willReturn(true);
            given(passwordEncoder.matches(passwordUpdateDto.getNewPassword(), testUser.getPassword())).willReturn(false);
            given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

            //when
            userService.updatePassword(passwordUpdateDto, testUser.getAccountId());

            // given
            assertEquals(encodedNewPassword, testUser.getPassword());
        }

        @Test
        void 비밀번호_변경_실패_틀린비밀번호() {
            // given
            User testUser = getTestUser();

            String currentPassword = "1q2w3e4r!@#$";
            String newPassword = "update!@#$";
            PasswordEncoder encoder = new BCryptPasswordEncoder();

            PasswordUpdateDto passwordUpdateDto = new PasswordUpdateDto(currentPassword, newPassword);

            String encodedNewPassword = encoder.encode(newPassword);

            given(userRepository.findByAccountId(testUser.getAccountId())).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(), testUser.getPassword())).willReturn(false);

            //when
            CustomException customException = assertThrows(CustomException.class, () -> userService.updatePassword(passwordUpdateDto, testUser.getAccountId()));

            // given
            assertEquals(ErrorType.INVALID_PASSWORD, customException.getErrorType());
        }

        @Test
        void 비밀번호_변경_실패_비밀번호중복() {
            // given
            User testUser = getTestUser();

            String currentPassword = "1q2w3e4r!@#$";
            String newPassword = "update!@#$";
            PasswordEncoder encoder = new BCryptPasswordEncoder();

            PasswordUpdateDto passwordUpdateDto = new PasswordUpdateDto(currentPassword, newPassword);

            String encodedNewPassword = encoder.encode(newPassword);

            given(userRepository.findByAccountId(testUser.getAccountId())).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(), testUser.getPassword())).willReturn(true);
            given(passwordEncoder.matches(passwordUpdateDto.getNewPassword(), testUser.getPassword())).willReturn(true);

            //when
            CustomException customException = assertThrows(CustomException.class, () -> userService.updatePassword(passwordUpdateDto, testUser.getAccountId()));

            // given
            assertEquals(ErrorType.DUPLICATE_PASSWORD, customException.getErrorType());
        }
    }
}