package com.example.demo.medium.common;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
    @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @MockBean
    private JavaMailSender mailSender;


    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다(){
        //given
        String email = "gkswlcjs2@naver.com";

        //when
        User result = userServiceImpl.getByEmail(email);

        //then
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void getByEmail은_PENDING_상태인_유저를_찾아올_수_없다(){
        //given
        String email = "gkswlcjs3@naver.com";

        //when
        //then
        assertThatThrownBy(()->{
            User result = userServiceImpl.getByEmail(email);
        }
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById은_ACTIVE_상태인_유저를_찾아올_수_있다(){
        //given
        String email = "gkswlcjs2@naver.com";

        //when
        User result = userServiceImpl.getById(1L);

        //then
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void getById는_PENDING_상태인_유저를_찾아올_수_없다(){
        //given
        //when
        //then
        assertThatThrownBy(()->{
                    User result = userServiceImpl.getById(2);
                }
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreateDto_를_이용하여_유저를_생성할_수_있다(){
        //given
        UserCreate userCreate = UserCreate
                .builder()
                .email("gkswlcjs2@kakao.com")
                .address("서울시 강남구")
                .nickname("gkswlcjs2-k")
                .build();
        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));


        //when
        User result = userServiceImpl.create(userCreate);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        // assertThat(result.getCertificationCode()).isEqualTo("T_T");
    }

    @Test
    void userUpdateDto_를_이용하여_유저를_수정할_수_있다(){
        //given
        UserUpdate userUpdate = UserUpdate
                .builder()
                .address("인천")
                .nickname("gkswlcjs2-n")
                .build();
        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));


        //when
        userServiceImpl.update(1, userUpdate);

        //then
        User result = userServiceImpl.getById(1L);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAddress()).isEqualTo("인천");
        assertThat(result.getNickname()).isEqualTo("gkswlcjs2-n");
    }

    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다(){
        //given
        //when
        userServiceImpl.login(1L);

        //then
        User userEntity = userServiceImpl.getById(1L);
        assertThat(userEntity.getLastLoginAt()).isGreaterThan(0L);
//        assertThat(userEntity.getLastLoginAt()).isEqualTo(""T_T); //FIXME
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_시킬_수_있다(){
        //given
        //when
        userServiceImpl.verifyEmail(2, "aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");

        //then
        User userEntity = userServiceImpl.getById(2);
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증_코드를_받으면_에러를_던진다(){
        //given
        //when
        //then
        assertThatThrownBy(()->{
            userServiceImpl.verifyEmail(2, "aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaac");
        }
        ).isInstanceOf(CertificationCodeNotMatchedException.class);
    }



}
