package com.example.demo.medium.common;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/post-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class PostServiceImplTest {
    @Autowired
    private PostServiceImpl postServiceImpl;

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
        assertThat(result.getCreatedAt()).isGreaterThan(0);
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
        assertThat(result.getModifiedAt()).isGreaterThan(0);
    }
}
