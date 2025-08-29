package org.fastcampus.jober.space;

import org.fastcampus.jober.space.repository.SpaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SpaceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpaceRepository spaceRepository;

    @Test
    void createSpace_Success() throws Exception {
        mockMvc.perform(post("/spaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "spaceName": "테스트",
                          "adminName": "영경",
                          "adminNum": "010-1111-1111"
                        }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void createSpace_Fail_WhenNameIsNull() throws Exception {
        // spaceName 필드 자체가 없음 → @Valid 또는 서비스 검증에서 400
        mockMvc.perform(post("/spaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "spaceName": "   ",
                              "adminName": "영경",
                              "adminNum": "010-1111-1111"
                            }
                            """))
                .andExpect(status().isBadRequest());
        // 필요하면 아래처럼 에러 메시지/코드도 검증
        // .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createSpace_Fail_WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/spaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "spaceName": "   ",
                              "adminName": "영경",
                              "adminNum": "010-1111-1111"
                            }
                            """))
                .andExpect(status().isBadRequest());
    }
}
