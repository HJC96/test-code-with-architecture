package com.example.demo.medium.common;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.infrastructure.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = true)
@Sql("/sql/user-repository-test-data.sql")
@TestPropertySource("classpath:test-application.properties")
public class UserJpaRepositoryTest {
    @Autowired
    private com.example.demo.user.infrastructure.UserJpaRepository UserJpaRepository;

/*    @Test
    void UserRepository_가_제대로_연결되었다(){
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("gkswlcjs2@naver.com");
        userEntity.setAddress("서울시 강남구");
        userEntity.setNickname("gkswlcjs2");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setCertificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        // when
        UserEntity result = userRepository.save(userEntity);

        // then
        assertThat(result.getId()).isNotNull();
    }*/

    @Test
    void findByIdAndStatus_로_유저_데이터를_찾아올_수_있다(){
        // given
        // when
        Optional<UserEntity> result = UserJpaRepository.findByIdAndStatus(1, UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByIdAndStatus_는_데이터가_없으면_Optional_empty_를_내려준다(){
        // given
        // when
        Optional<UserEntity> result = UserJpaRepository.findByIdAndStatus(1, UserStatus.PENDING);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void findByEmailAndStatus_로_유저_데이터를_찾아올_수_있다(){
        // given
        // when
        Optional<UserEntity> result = UserJpaRepository.findByEmailAndStatus("gkswlcjs2@naver.com", UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByEmailAndStatus_는_데이터가_없으면_Optional_empty_를_내려준다(){
        // given
        // when
        Optional<UserEntity> result = UserJpaRepository.findByEmailAndStatus("gkswlcjs2@naver.com", UserStatus.PENDING);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

}
