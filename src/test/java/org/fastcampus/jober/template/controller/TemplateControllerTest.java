//package org.fastcampus.jober.template.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import org.fastcampus.jober.template.dto.request.TemplateCreateRequestDto;
//import org.fastcampus.jober.template.dto.request.TemplateDeleteRequestDto;
//import org.fastcampus.jober.template.dto.request.TemplateSaveRequestDto;
//import org.fastcampus.jober.template.dto.response.TemplateCreateResponseDto;
//import org.fastcampus.jober.template.dto.response.TemplateDetailResponseDto;
//import org.fastcampus.jober.template.dto.response.TemplateSaveResponseDto;
//import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
//import org.fastcampus.jober.template.service.TemplateService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.MediaType;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//@WebMvcTest(TemplateController.class)
//@DisplayName("TemplateController MockMvc 테스트")
//class TemplateControllerTest {
//
//    @Autowired private MockMvc mockMvc;
//
//    @MockitoBean private TemplateService templateService;
//
//    @Autowired private ObjectMapper objectMapper;
//
//    private TemplateTitleResponseDto titleResponseDto;
//    private TemplateDetailResponseDto detailResponseDto;
//    private TemplateCreateResponseDto createResponseDto;
//    private TemplateSaveResponseDto saveResponseDto;
//    private TemplateCreateRequestDto createRequestDto;
//
//    // CSRF 비활성화를 위한 테스트용 SecurityConfig
//    @TestConfiguration
//    static class TestSecurityConfig {
//        @Bean
//        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//            http.csrf(AbstractHttpConfigurer::disable);
//            return http.build();
//        }
//    }
//
//    @BeforeEach
//    void setUp() {
//        titleResponseDto = TemplateTitleResponseDto.builder().title("테스트 템플릿").build();
//
//        detailResponseDto =
//                TemplateDetailResponseDto.builder()
//                        .id(1L)
//                        .title("테스트 템플릿")
//                        .spaceId(1L)
//                        .isSaved(true)
//                        .build();
//
//        // --- ▼▼▼ 여기가 수정되었습니다 ▼▼▼ ---
//        // 새로운 TemplateCreateResponseDto 구조에 맞게 Mock 객체 설정
//        createResponseDto = new TemplateCreateResponseDto();
//        createResponseDto.setSuccess(true);
//        createResponseDto.setResponse("AI가 템플릿을 생성했습니다.");
//        createResponseDto.setTemplate("AI가 생성한 내용");
//        createResponseDto.setOptions(List.of("예", "아니오"));
//        createResponseDto.setHasImage(false);
//        createResponseDto.setStructuredTemplate(Map.of("title", "생성된 템플릿 제목"));
//        createResponseDto.setEditableVariables(Map.of("variable", "value"));
//        // --- ▲▲▲ 여기까지 수정 ▲▲▲ ---
//
//        // TemplateSaveResponseDto는 빌더 패턴을 사용하므로, 필드명을 일관성 있게 수정합니다.
//        saveResponseDto =
//                TemplateSaveResponseDto.builder()
//                        .spaceId(1L)
//                        .title("저장된 템플릿")
//                        .description("설명")
//                        .type("타입")
//                        .build();
//
//        createRequestDto = new TemplateCreateRequestDto();
//        createRequestDto.setMessage("테스트 메시지");
//        createRequestDto.setState(null);
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("GET /template/{spaceId} - 템플릿 제목 조회 성공")
//    void getTemplateTitlesBySpaceId_Success() throws Exception {
//        Long spaceId = 1L;
//        List<TemplateTitleResponseDto> expectedTitles = Collections.singletonList(titleResponseDto);
//        when(templateService.getTitlesBySpaceId(spaceId)).thenReturn(expectedTitles);
//
//        mockMvc
//                .perform(get("/template/{spaceId}", spaceId).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].title").value("테스트 템플릿"));
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("GET /template/{spaceId}/{templateId} - 템플릿 상세 조회 성공")
//    void getTemplateDetailBySpaceIdAndTemplateId_Success() throws Exception {
//        Long spaceId = 1L;
//        Long templateId = 1L;
//        when(templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId))
//                .thenReturn(detailResponseDto);
//
//        mockMvc
//                .perform(
//                        get("/template/{spaceId}/{templateId}", spaceId, templateId)
//                                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.title").value("테스트 템플릿"))
//                .andExpect(jsonPath("$.spaceId").value(1))
//                .andExpect(jsonPath("$.isSaved").value(true));
//    }
//
//    // ... 다른 GET, DELETE 테스트는 변경할 필요가 없으므로 생략 ...
//
//    @Test
//    @WithMockUser
//    @DisplayName("POST /template/create-template - AI 템플릿 생성 성공")
//    void createTemplate_Success() throws Exception {
//        when(templateService.createTemplate(any(TemplateCreateRequestDto.class)))
//                .thenReturn(createResponseDto);
//
//        // --- ▼▼▼ 여기가 수정되었습니다 ▼▼▼ ---
//        // 새로운 DTO 구조에 맞게 JSON Path 검증 수정
//        mockMvc
//                .perform(
//                        post("/template/create-template")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(createRequestDto)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.response").value("AI가 템플릿을 생성했습니다."))
//                .andExpect(jsonPath("$.template").value("AI가 생성한 내용"))
//                .andExpect(jsonPath("$.options[0]").value("예"))
//                .andExpect(jsonPath("$.structured_template.title").value("생성된 템플릿 제목"));
//        // --- ▲▲▲ 여기까지 수정 ▲▲▲ ---
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("POST /template/save - 템플릿 저장 성공")
//    void saveTemplate_Success() throws Exception {
//        when(templateService.saveTemplate(any(TemplateSaveRequestDto.class)))
//                .thenReturn(saveResponseDto);
//
//        // --- ▼▼▼ 여기가 수정되었습니다 ▼▼▼ ---
//        // 일관성을 위해 TemplateSaveRequestDto에 맞게 요청 JSON 수정
//        String saveRequestJson =
//                """
//                        {
//                            "spaceId": 1,
//                            "title": "저장할 템플릿",
//                            "description": "설명",
//                            "type": "타입"
//                        }
//                        """;
//
//        mockMvc
//                .perform(
//                        post("/template/save")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(saveRequestJson))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.spaceId").value(1))
//                .andExpect(jsonPath("$.title").value("저장된 템플릿"));
//        // --- ▲▲▲ 여기까지 수정 ▲▲▲ ---
//    }
//
//    // ... 나머지 테스트는 생략 ...
//
//    /** 템플릿 삭제 성공 테스트 정상적인 요청으로 템플릿이 논리적으로 삭제되는지 검증 */
//    @Test
//    @WithMockUser
//    @DisplayName("DELETE /template/delete - 템플릿 삭제 성공")
//    void deleteTemplate_Success() throws Exception {
//        // Given
//        doNothing().when(templateService).deleteTemplate(any(TemplateDeleteRequestDto.class));
//
//        String deleteRequestJson =
//                """
//                        {
//                            "spaceId": 1,
//                            "templateId": 1
//                        }
//                        """;
//
//        // When & Then
//        mockMvc
//                .perform(
//                        delete("/template/delete")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(deleteRequestJson))
//                .andExpect(status().isNoContent());
//    }
//
//}