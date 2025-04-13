package com.example.demo.user.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository UserJpaRepository;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 사용자는_특정_유저의_정보를_개인정보는_소거된채_전달_받을_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("gkswlcjs2@naver.com"))
                .andExpect(jsonPath("$.nickname").value("gkswlcjs2"))
                .andExpect(jsonPath("$.address").doesNotExist())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void 사용자는_존재하지_않는_유저의_아이디로_api_호출할_경우_404_응답을_받는다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/1234234234"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID 1234234234를 찾을 수 없습니다."));
    }

    @Test
    void 사용자는_인증_코드로_계정을_활성화_시킬_수_있다() throws Exception{
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/2/verify")
                .queryParam("certificationCode", "aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab"))
                .andExpect(status().isFound()); // 302로 리다이렉트 되므로 isFound

        UserEntity userEntity = UserJpaRepository.findById(2L).get();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_인증_코드가_일치하지_않을_경우_권한_없음_에러를_내려준다() throws Exception{
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaac"))
                .andExpect(status().isForbidden());
    }

    @Test
    void 사용자는_내_정보를_불러올_때_개인정보인_주소도_갖고_올_수_있다() throws Exception{
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/me")
                .header("EMAIL", "gkswlcjs2@naver.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("gkswlcjs2@naver.com"))
                .andExpect(jsonPath("$.address").value("서울시 강남구"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void 사용자는_내_정보를_불러올_수정할_수_있다() throws Exception{
        // given
        UserUpdate userUpdate = UserUpdate
                .builder()
                .nickname("gkswlcjs2-n")
                .address("서울시 성북구")
                .build();
        // when
        // then
        mockMvc.perform(put("/api/users/me")
                        .header("EMAIL", "gkswlcjs2@naver.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("gkswlcjs2@naver.com"))
                .andExpect(jsonPath("$.nickname").value("gkswlcjs2-n"))
                .andExpect(jsonPath("$.address").value("서울시 성북구"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
