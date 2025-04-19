package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

public class UserServiceImplTest {

    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void init(){
        FakeMailSender fakeMailSender = new FakeMailSender();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        this.userServiceImpl = UserServiceImpl
                .builder()
                .uuidHolder(new TestUuidHolder("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab"))
                .userRepository(fakeUserRepository)
                .clockHolder(new TestClockHolder(1678530673958L))
                .certificationService(new CertificationService(fakeMailSender))
                .build();
        fakeUserRepository.save(User.builder()
                        .id(1L)
                        .email("gkswlcjs2@naver.com")
                        .address("서울시 강남구")
                        .nickname("gkswlcjs2")
                        .status(UserStatus.ACTIVE)
                        .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                        .lastLoginAt(0L)
                .build());

        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("gkswlcjs3@naver.com")
                .address("서울시 강남구")
                .nickname("gkswlcjs3")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .lastLoginAt(0L)
                .build());
    }

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

        //when
        User result = userServiceImpl.create(userCreate);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");
    }

    @Test
    void userUpdateDto_를_이용하여_유저를_수정할_수_있다(){
        //given
        UserUpdate userUpdate = UserUpdate
                .builder()
                .address("인천")
                .nickname("gkswlcjs2-n")
                .build();

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
        User user = userServiceImpl.getById(1L);
        assertThat(user.getLastLoginAt()).isGreaterThan(0L);
        assertThat(user.getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_시킬_수_있다(){
        //given
        //when
        userServiceImpl.verifyEmail(2, "aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");

        //then
        User user = userServiceImpl.getById(2);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
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
