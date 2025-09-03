package org.fastcampus.jober.template.controller;

import org.fastcampus.jober.template.dto.response.TemplateDetailResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.service.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateController 테스트")
class TemplateControllerTest {

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private TemplateController templateController;

    private TemplateTitleResponseDto titleDto1;
    private TemplateTitleResponseDto titleDto2;
    private TemplateDetailResponseDto detailDto;

    @BeforeEach
    void setUp() {
        titleDto1 = TemplateTitleResponseDto.builder()
                .title("템플릿1")
                .build();

        titleDto2 = TemplateTitleResponseDto.builder()
                .title("템플릿2")
                .build();

        detailDto = TemplateDetailResponseDto.builder()
                .id(1L)
                .spaceId(100L)
                .title("테스트 템플릿")
                .status("APPROVED_MOCK")
                .kakaoTemplateId(12345L)
                .extractedVariables("{\"name\": \"value\"}")
                .sessionId("session123")
                .finalTemplate("최종 템플릿 내용")
                .htmlPreview("<html>미리보기</html>")
                .parameterizedTemplate("파라미터화된 템플릿")
                .totalAttempts(3)
                .isSaved(true)
                .isAccepted(false)
                .build();
    }

    @Test
    @DisplayName("특정 spaceId의 템플릿 제목들을 조회할 수 있다")
    void getTemplateTitlesBySpaceId_WithValidSpaceId_ReturnsTitles() {
        // given
        Long spaceId = 100L;
        List<TemplateTitleResponseDto> expectedTitles = Arrays.asList(titleDto1, titleDto2);
        when(templateService.getTitlesBySpaceId(spaceId)).thenReturn(expectedTitles);

        // when
        ResponseEntity<List<TemplateTitleResponseDto>> response = templateController.getTemplateTitlesBySpaceId(spaceId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("템플릿1");
        assertThat(response.getBody().get(1).getTitle()).isEqualTo("템플릿2");
    }

    @Test
    @DisplayName("특정 spaceId와 templateId의 템플릿 상세 정보를 조회할 수 있다 (completedAt 제외)")
    void getTemplateDetailBySpaceIdAndTemplateId_WithValidIds_ReturnsDetail() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        when(templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId)).thenReturn(detailDto);

        // when
        ResponseEntity<TemplateDetailResponseDto> response = templateController.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getSpaceId()).isEqualTo(100L);
        assertThat(response.getBody().getTitle()).isEqualTo("테스트 템플릿");
        assertThat(response.getBody().getStatus()).isEqualTo("APPROVED_MOCK");
        assertThat(response.getBody().getKakaoTemplateId()).isEqualTo(12345L);
        assertThat(response.getBody().getExtractedVariables()).isEqualTo("{\"name\": \"value\"}");
        assertThat(response.getBody().getSessionId()).isEqualTo("session123");
        assertThat(response.getBody().getFinalTemplate()).isEqualTo("최종 템플릿 내용");
        assertThat(response.getBody().getHtmlPreview()).isEqualTo("<html>미리보기</html>");
        assertThat(response.getBody().getParameterizedTemplate()).isEqualTo("파라미터화된 템플릿");
        assertThat(response.getBody().getTotalAttempts()).isEqualTo(3);
        assertThat(response.getBody().getIsSaved()).isTrue();
        assertThat(response.getBody().getIsAccepted()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 템플릿을 조회하면 404를 반환한다")
    void getTemplateDetailBySpaceIdAndTemplateId_WithNonExistentTemplate_Returns404() {
        // given
        Long spaceId = 999L;
        Long templateId = 999L;
        when(templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId)).thenReturn(null);

        // when
        ResponseEntity<TemplateDetailResponseDto> response = templateController.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("유효하지 않은 DTO를 조회하면 404를 반환한다")
    void getTemplateDetailBySpaceIdAndTemplateId_WithInvalidDto_Returns404() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        TemplateDetailResponseDto invalidDto = TemplateDetailResponseDto.builder()
                .id(null) // 유효하지 않은 DTO
                .spaceId(100L)
                .title("테스트 템플릿")
                .build();
        when(templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId)).thenReturn(invalidDto);

        // when
        ResponseEntity<TemplateDetailResponseDto> response = templateController.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("completedAt 필드가 DTO에 포함되지 않는다")
    void getTemplateDetailBySpaceIdAndTemplateId_ExcludesCompletedAt() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        when(templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId)).thenReturn(detailDto);

        // when
        ResponseEntity<TemplateDetailResponseDto> response = templateController.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        // completedAt 필드가 DTO에 정의되어 있지 않음을 확인
        // TemplateDetailResponseDto 클래스에 completedAt 필드가 없어야 함
    }

    @Test
    @DisplayName("빈 제목을 가진 DTO는 유효하지 않다")
    void getTemplateDetailBySpaceIdAndTemplateId_WithEmptyTitle_Returns404() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        TemplateDetailResponseDto invalidDto = TemplateDetailResponseDto.builder()
                .id(1L)
                .spaceId(100L)
                .title("") // 빈 제목
                .build();
        when(templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId)).thenReturn(invalidDto);

        // when
        ResponseEntity<TemplateDetailResponseDto> response = templateController.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
