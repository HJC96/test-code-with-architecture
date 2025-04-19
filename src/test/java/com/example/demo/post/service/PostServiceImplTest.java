package com.example.demo.post.service;

import com.example.demo.mock.*;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostServiceImplTest {
    private PostServiceImpl postServiceImpl;

    @BeforeEach
    void init(){
        FakePostRepository fakePostRepository = new FakePostRepository();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        this.postServiceImpl = PostServiceImpl
                .builder()
                .postRepository(fakePostRepository)
                .userRepository(fakeUserRepository)
                .clockHolder(new TestClockHolder(1678530673958L))
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("gkswlcjs2@naver.com")
                .address("서울시 강남구")
                .nickname("gkswlcjs2")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .lastLoginAt(0L)
                .build();
        fakeUserRepository.save(user1);

        User user2 = User.builder()
                .id(2L)
                .email("gkswlcjs3@naver.com")
                .address("서울시 강남구")
                .nickname("gkswlcjs3")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .lastLoginAt(0L)
                .build();
        fakeUserRepository.save(user2);

        fakePostRepository.save(Post.builder()
                .id(1L)
                .content("helloworld")
                .createdAt(1678530673958L)
                .modifiedAt(0L)
                .writer(user1)
                .build()
        );
    }

    @Test
    void getById는_존재하는_게시물을_내려준다() {
        //given
        //when
        Post result = postServiceImpl.getById(1L);
        //then
        assertThat(result.getContent()).isEqualTo("helloworld");
        assertThat(result.getWriter().getEmail()).isEqualTo("gkswlcjs2@naver.com");
    }
    @Test
    void postCreateDto_를_이용하여_게시물을_생성할_수_있다(){
        //given
        PostCreate postCreate = PostCreate
                .builder()
                .writerId(1L)
                .content("foobar")
                .build();

        //when
        Post result = postServiceImpl.create(postCreate);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContent()).isEqualTo("foobar");
        assertThat(result.getCreatedAt()).isEqualTo(1678530673958L);
    }

    @Test
    void postUpdateDto_를_이용하여_게시물을_수정할_수_있다(){
        //given
        PostUpdate postUpdate = PostUpdate
                .builder()
                .content("hello world :)")
                .build();
        //whe n
        postServiceImpl.update(1L, postUpdate);

        //then
        Post result = postServiceImpl.getById(1L);
        assertThat(result.getContent()).isEqualTo("hello world :)");
        assertThat(result.getModifiedAt()).isEqualTo(1678530673958L);
    }
}
