package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.port.UserService;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Test
    void 사용자는_특정_유저의_정보를_개인정보는_소거된채_전달_받을_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(User.builder()
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<UserResponse> result = testContainer.userController.getUserById(1);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("gkswlcjs2@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("gkswlcjs2");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100L);
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_특정_유저의_정보를_개인정보는_소된채_전달_받을_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(User.builder()
                .build());

        // then
        assertThatThrownBy(()->{
            ResponseEntity<UserResponse> result = testContainer.userController.getUserById(1);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 사용자는_인증_코드로_계정을_활성화_시킬_수_있다(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<Void> result = testContainer.userController.verifyEmail(1, "aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(302));
        assertThat(testContainer.userController.getUserById(1L).getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


    @Test
    void 사용자는_인증_코드가_일치하지_않을_경우_권한_없음_에러를_내려준다(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .lastLoginAt(100L)
                .build());

        // when
        assertThatThrownBy(()->{
            ResponseEntity<Void> result = testContainer.userController.verifyEmail(1, "aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaac");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }

    @Test
    void 사용자는_내_정보를_불러올_때_개인정보인_주소도_갖고_올_수_있다(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(new ClockHolder() {
                    @Override
                    public long millis() {
                        return 1678530673958L;
                    }
                })
                .build();

        testContainer.userRepository.save(User.builder()
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.getMyInfo("gkswlcjs2@naver.com");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("gkswlcjs2@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("gkswlcjs2");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(1678530673958L);
        assertThat(result.getBody().getAddress()).isEqualTo("서울시 강남구");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);

    }


    @Test
    void 사용자는_내_정보를_불러올_수정할_수_있다(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(User.builder()
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.updateMyInfo("gkswlcjs2@naver.com",
                UserUpdate.builder()
                        .address("서울시")
                        .nickname("gkswlcjs2-r")
                        .build());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("gkswlcjs2@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("gkswlcjs2-r");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100L);
        assertThat(result.getBody().getAddress()).isEqualTo("서울시");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
