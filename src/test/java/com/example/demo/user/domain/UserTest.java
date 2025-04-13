package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {

    @Test
    public void User는_UserCreate_객체로_생성할_수_있다(){
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .build();
        // when
        User user = User.from(userCreate, new TestUuidHolder("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab"));

        // then
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("gkswlcjs2@naver.com");
        assertThat(user.getNickname()).isEqualTo("gkswlcjs2");
        assertThat(user.getAddress()).isEqualTo("서울시 강남구");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");
    }

    @Test
    public void User는_UserUpdate_객체로_업데이트할_수_있다(){
        // given
        User user = User.builder()
                .id(1L)
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .build();

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("gkswlcjs2-k")
                .address("서울시 성북구")
                .build();


        // when
        user = user.update(userUpdate);

        // then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("gkswlcjs2@naver.com");
        assertThat(user.getNickname()).isEqualTo("gkswlcjs2-k");
        assertThat(user.getAddress()).isEqualTo("서울시 성북구");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCertificationCode()).isEqualTo("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");
        assertThat(user.getLastLoginAt()).isEqualTo(100L);

    }

    @Test
    public void User는_로그인을_할_수_있고_로그인시_마지막_로그인_시간이_변경된다(){
        // given
        User user = User.builder()
                .id(1L)
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .build();

        // when
        user = user.login(new TestClockHolder(1678530673958L));

        // then
        assertThat(user.getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @Test
    public void User는_유효한_인증_코드로_계정을_활성화_할_수_있다(){
        // given
        User user = User.builder()
                .id(1L)
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .build();

        // when
        user = user.certificate("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");

        // then
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void User는_잘못된_인증_코드로_계정을_활성화_하려하면_에러를_던진다(){
        // given
        User user = User.builder()
                .id(1L)
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .build();

        // when
        // then
        assertThatThrownBy(()->{
            user.certificate("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
