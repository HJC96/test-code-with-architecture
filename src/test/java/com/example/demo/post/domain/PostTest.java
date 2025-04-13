package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTest {

    @Test
    public void PostCreate으로_게시물을_만들_수_있다(){
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1L)
                .content("helloworld")
                .build();

        User writer = User.builder()
                .email("gkswlcjs2@naver.com")
                .nickname("gkswlcjs2")
                .address("서울시 강남구")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .build();


        // when
        Post post = Post.from(writer, postCreate);

        //then
        assertThat(post.getContent()).isEqualTo("helloworld");
        assertThat(post.getWriter().getEmail()).isEqualTo("gkswlcjs2@naver.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("gkswlcjs2");
        assertThat(post.getWriter().getAddress()).isEqualTo("서울시 강남구");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(post.getWriter().getCertificationCode()).isEqualTo("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");

    }
}
