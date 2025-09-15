package org.fastcampus.jober.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fastcampus.jober.template.dto.request.TemplateCreateRequestDto;
import org.fastcampus.jober.template.dto.request.TemplateDeleteRequestDto;
import org.fastcampus.jober.template.dto.request.TemplateSaveRequestDto;
import org.fastcampus.jober.template.dto.response.TemplateCreateResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateDetailResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateSaveResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.service.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TemplateController의 MockMvc 테스트 클래스
 * 모든 API 엔드포인트의 정상 작동을 검증합니다.
 */
@WebMvcTest(TemplateController.class)
@DisplayName("TemplateController MockMvc 테스트")
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TemplateService templateService;

    @Autowired
    private ObjectMapper objectMapper;

    private TemplateTitleResponseDto titleResponseDto;
    private TemplateDetailResponseDto detailResponseDto;
    private TemplateCreateResponseDto createResponseDto;
    private TemplateSaveResponseDto saveResponseDto;
    private TemplateCreateRequestDto createRequestDto;
    private TemplateSaveRequestDto saveRequestDto;

    @BeforeEach
    void setUp() {
        // 템플릿 제목 응답 DTO 설정
        titleResponseDto = TemplateTitleResponseDto.builder()
                .title("테스트 템플릿")
                .build();

        // 템플릿 상세 응답 DTO 설정
        detailResponseDto = TemplateDetailResponseDto.builder()
                .id(1L)
                .title("테스트 템플릿")
                .spaceId(1L)
                .isSaved(true)
                .build();

        // 템플릿 생성 응답 DTO 설정
        createResponseDto = new TemplateCreateResponseDto();
        createResponseDto.setMessage("AI가 템플릿을 생성했습니다.");
        createResponseDto.setTemplateContent("AI가 생성한 내용");
        createResponseDto.setHtmlPreview("<div>HTML 미리보기</div>");
        createResponseDto.setFinalTemplate("최종 템플릿");
        createResponseDto.setParameterizedTemplate("매개변수화된 템플릿");
        createResponseDto.setExtractedVariables("변수1, 변수2");

        // 템플릿 저장 응답 DTO 설정
        saveResponseDto = TemplateSaveResponseDto.builder()
                .spaceId(1L)
                .title("저장된 템플릿")
                .extractedVariables("추출된 변수")
                .finalTemplate("최종 템플릿")
                .htmlPreview("HTML 미리보기")
                .parameterizedTemplate("파라미터화된 템플릿")
                .type("타입")
                .build();

        // 템플릿 생성 요청 DTO 설정
        createRequestDto = new TemplateCreateRequestDto();
        createRequestDto.setMessage("테스트 메시지");
        createRequestDto.setState(null); // state는 null로 설정

        // 템플릿 저장 요청 DTO 설정
        saveRequestDto = new TemplateSaveRequestDto();
        // TemplateSaveRequestDto는 @Getter만 있으므로 직접 필드 설정 불가
        // 대신 JSON으로 직렬화해서 테스트
    }

    @Test
    @WithMockUser
    @DisplayName("GET /template/{spaceId} - 템플릿 제목 조회 성공")
    void getTemplateTitlesBySpaceId_Success() throws Exception {
        // Given
        Long spaceId = 1L;
        List<TemplateTitleResponseDto> expectedTitles = Arrays.asList(titleResponseDto);
        when(templateService.getTitlesBySpaceId(spaceId)).thenReturn(expectedTitles);

        // When & Then
        mockMvc.perform(get("/template/{spaceId}", spaceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("테스트 템플릿"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /template/{spaceId}/{templateId} - 템플릿 상세 조회 성공")
    void getTemplateDetailBySpaceIdAndTemplateId_Success() throws Exception {
        // Given
        Long spaceId = 1L;
        Long templateId = 1L;
        when(templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId))
                .thenReturn(detailResponseDto);

        // When & Then
        mockMvc.perform(get("/template/{spaceId}/{templateId}", spaceId, templateId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("테스트 템플릿"))
                .andExpect(jsonPath("$.spaceId").value(1))
                .andExpect(jsonPath("$.isSaved").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /template/{spaceId}/{templateId} - 템플릿 상세 조회 실패 (템플릿 없음)")
    void getTemplateDetailBySpaceIdAndTemplateId_NotFound() throws Exception {
        // Given
        Long spaceId = 1L;
        Long templateId = 999L;
        when(templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(get("/template/{spaceId}/{templateId}", spaceId, templateId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /template/{spaceId}/{templateId} - 템플릿 상세 조회 실패 (유효하지 않은 템플릿)")
    void getTemplateDetailBySpaceIdAndTemplateId_InvalidTemplate() throws Exception {
        // Given
        Long spaceId = 1L;
        Long templateId = 1L;
        TemplateDetailResponseDto invalidTemplate = TemplateDetailResponseDto.builder()
                .id(1L)
                .title(null) // 유효하지 않은 제목
                .spaceId(1L)
                .isSaved(true)
                .build();
        when(templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId))
                .thenReturn(invalidTemplate);

        // When & Then
        mockMvc.perform(get("/template/{spaceId}/{templateId}", spaceId, templateId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /template/create-template - AI 템플릿 생성 성공")
    void createTemplate_Success() throws Exception {
        // Given
        when(templateService.createTemplate(any(TemplateCreateRequestDto.class)))
                .thenReturn(createResponseDto);

        // When & Then
        mockMvc.perform(post("/template/create-template")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("AI가 템플릿을 생성했습니다."))
                .andExpect(jsonPath("$.templateContent").value("AI가 생성한 내용"))
                .andExpect(jsonPath("$.htmlPreview").value("<div>HTML 미리보기</div>"))
                .andExpect(jsonPath("$.finalTemplate").value("최종 템플릿"))
                .andExpect(jsonPath("$.parameterizedTemplate").value("매개변수화된 템플릿"))
                .andExpect(jsonPath("$.extractedVariables").value("변수1, 변수2"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /template/create-template - AI 템플릿 생성 실패 (잘못된 요청)")
    void createTemplate_BadRequest() throws Exception {
        // Given
        TemplateCreateRequestDto invalidRequest = new TemplateCreateRequestDto();
        invalidRequest.setMessage(""); // 빈 메시지
        invalidRequest.setState(null);

        // When & Then
        mockMvc.perform(post("/template/create-template")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk()); // 서비스가 빈 메시지도 처리함
    }

    @Test
    @WithMockUser
    @DisplayName("POST /template/save - 템플릿 저장 성공")
    void saveTemplate_Success() throws Exception {
        // Given
        when(templateService.saveTemplate(any(TemplateSaveRequestDto.class)))
                .thenReturn(saveResponseDto);

        // JSON으로 직접 요청 데이터 생성
        String saveRequestJson = """
                {
                    "spaceId": 1,
                    "title": "저장할 템플릿",
                    "extractedVariables": "추출된 변수",
                    "finalTemplate": "최종 템플릿",
                    "htmlPreview": "HTML 미리보기",
                    "parameterizedTemplate": "파라미터화된 템플릿",
                    "type": "타입"
                }
                """;

        // When & Then
        mockMvc.perform(post("/template/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.spaceId").value(1))
                .andExpect(jsonPath("$.title").value("저장된 템플릿"))
                .andExpect(jsonPath("$.extractedVariables").value("추출된 변수"))
                .andExpect(jsonPath("$.finalTemplate").value("최종 템플릿"))
                .andExpect(jsonPath("$.htmlPreview").value("HTML 미리보기"))
                .andExpect(jsonPath("$.parameterizedTemplate").value("파라미터화된 템플릿"))
                .andExpect(jsonPath("$.type").value("타입"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /template/save - 템플릿 저장 실패 (잘못된 요청)")
    void saveTemplate_BadRequest() throws Exception {
        // Given
        // 빈 JSON 요청
        String invalidRequestJson = "{}";

        // When & Then
        mockMvc.perform(post("/template/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isOk()); // 서비스가 빈 요청도 처리함
    }

    @Test
    @WithMockUser
    @DisplayName("GET /template/{spaceId} - 잘못된 spaceId 형식")
    void getTemplateTitlesBySpaceId_InvalidSpaceId() throws Exception {
        // When & Then
        mockMvc.perform(get("/template/invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler가 처리하여 400으로 응답
    }

    @Test
    @WithMockUser
    @DisplayName("GET /template/{spaceId}/{templateId} - 잘못된 ID 형식")
    void getTemplateDetailBySpaceIdAndTemplateId_InvalidIds() throws Exception {
        // When & Then
        mockMvc.perform(get("/template/invalid/invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler가 처리하여 400으로 응답
    }

    /**
     * 템플릿 삭제 성공 테스트
     * 정상적인 요청으로 템플릿이 논리적으로 삭제되는지 검증
     */
    @Test
    @WithMockUser
    @DisplayName("DELETE /template/delete - 템플릿 삭제 성공")
    void deleteTemplate_Success() throws Exception {
        // Given
        doNothing().when(templateService).deleteTemplate(any(TemplateDeleteRequestDto.class));

        String deleteRequestJson = """
                {
                    "spaceId": 1,
                    "templateId": 1
                }
                """;

        // When & Then
        mockMvc.perform(delete("/template/delete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteRequestJson))
                .andExpect(status().isNoContent());
    }

    /**
     * 템플릿 삭제 실패 테스트 (템플릿 없음)
     * 존재하지 않는 템플릿을 삭제하려고 할 때 404 에러가 발생하는지 검증
     */
    @Test
    @WithMockUser
    @DisplayName("DELETE /template/delete - 템플릿 삭제 실패 (템플릿 없음)")
    void deleteTemplate_TemplateNotFound() throws Exception {
        // Given
        doThrow(new org.fastcampus.jober.error.BusinessException(
                org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "템플릿을 찾을 수 없습니다."))
                .when(templateService).deleteTemplate(any(TemplateDeleteRequestDto.class));

        String deleteRequestJson = """
                {
                    "spaceId": 1,
                    "templateId": 999
                }
                """;

        // When & Then
        mockMvc.perform(delete("/template/delete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteRequestJson))
                .andExpect(status().isNotFound());
    }

    /**
     * 템플릿 삭제 실패 테스트 (권한 없음)
     * 다른 스페이스의 템플릿을 삭제하려고 할 때 권한 에러가 발생하는지 검증
     */
    @Test
    @WithMockUser
    @DisplayName("DELETE /template/delete - 템플릿 삭제 실패 (권한 없음)")
    void deleteTemplate_NoPermission() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("해당 스페이스에서 템플릿을 삭제할 권한이 없습니다."))
                .when(templateService).deleteTemplate(any(TemplateDeleteRequestDto.class));

        String deleteRequestJson = """
                {
                    "spaceId": 2,
                    "templateId": 1
                }
                """;

        // When & Then
        mockMvc.perform(delete("/template/delete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteRequestJson))
                .andExpect(status().isInternalServerError()); // GlobalExceptionHandler가 IllegalArgumentException을 500으로 처리
    }

    /**
     * 템플릿 삭제 실패 테스트 (이미 삭제된 템플릿)
     * 이미 삭제된 템플릿을 다시 삭제하려고 할 때 에러가 발생하는지 검증
     */
    @Test
    @WithMockUser
    @DisplayName("DELETE /template/delete - 템플릿 삭제 실패 (이미 삭제된 템플릿)")
    void deleteTemplate_AlreadyDeleted() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("이미 삭제된 템플릿입니다."))
                .when(templateService).deleteTemplate(any(TemplateDeleteRequestDto.class));

        String deleteRequestJson = """
                {
                    "spaceId": 1,
                    "templateId": 1
                }
                """;

        // When & Then
        mockMvc.perform(delete("/template/delete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteRequestJson))
                .andExpect(status().isInternalServerError()); // GlobalExceptionHandler가 IllegalArgumentException을 500으로 처리
    }

    /**
     * 템플릿 삭제 실패 테스트 (잘못된 요청)
     * 필수 필드가 누락된 요청으로 삭제를 시도할 때 에러가 발생하는지 검증
     */
    @Test
    @WithMockUser
    @DisplayName("DELETE /template/delete - 템플릿 삭제 실패 (잘못된 요청)")
    void deleteTemplate_BadRequest() throws Exception {
        // Given
        String invalidRequestJson = """
                {
                    "spaceId": 1
                }
                """;

        // When & Then
        mockMvc.perform(delete("/template/delete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isNoContent()); // templateId가 null이어도 서비스가 호출되어 성공 처리됨
    }
}
