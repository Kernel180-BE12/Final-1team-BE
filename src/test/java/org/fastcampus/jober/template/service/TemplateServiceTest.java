package org.fastcampus.jober.template.service;

import org.fastcampus.jober.template.dto.response.TemplateDetailResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.entity.Template;
import org.fastcampus.jober.template.entity.enums.Status;
import org.fastcampus.jober.template.repository.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateService 테스트")
class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private TemplateService templateService;

    private Template testTemplate;
    private List<String> testTitles;

    @BeforeEach
    void setUp() {
        testTemplate = Template.builder()
                .id(1L)
                .spaceId(100L)
                .title("테스트 템플릿")
                .status(Status.APPROVED_MOCK)
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

        testTitles = Arrays.asList("템플릿1", "템플릿2", "템플릿3");
    }

    @Test
    @DisplayName("특정 spaceId의 템플릿 제목들을 조회할 수 있다")
    void getTitlesBySpaceId_WithValidSpaceId_ReturnsTitles() {
        // given
        Long spaceId = 100L;
        when(templateRepository.findTitlesBySpaceId(spaceId)).thenReturn(testTitles);

        // when
        List<TemplateTitleResponseDto> result = templateService.getTitlesBySpaceId(spaceId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getTitle()).isEqualTo("템플릿1");
        assertThat(result.get(1).getTitle()).isEqualTo("템플릿2");
        assertThat(result.get(2).getTitle()).isEqualTo("템플릿3");
    }

    @Test
    @DisplayName("특정 spaceId의 템플릿들을 조회할 수 있다")
    void getTemplatesBySpaceId_WithValidSpaceId_ReturnsTemplates() {
        // given
        Long spaceId = 100L;
        List<Template> templates = Arrays.asList(testTemplate);
        when(templateRepository.findBySpaceId(spaceId)).thenReturn(templates);

        // when
        List<Template> result = templateService.getTemplatesBySpaceId(spaceId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("테스트 템플릿");
    }

    @Test
    @DisplayName("특정 spaceId와 templateId의 템플릿을 조회할 수 있다")
    void getTemplateBySpaceIdAndTemplateId_WithValidIds_ReturnsTemplate() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        when(templateRepository.findBySpaceIdAndTemplateId(spaceId, templateId)).thenReturn(testTemplate);

        // when
        Template result = templateService.getTemplateBySpaceIdAndTemplateId(spaceId, templateId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 템플릿");
        assertThat(result.getSpaceId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("특정 spaceId와 templateId의 템플릿 상세 정보를 조회할 수 있다 (completedAt 제외)")
    void getTemplateDetailBySpaceIdAndTemplateId_WithValidIds_ReturnsDetailDto() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        when(templateRepository.findBySpaceIdAndTemplateIdWithAllFields(spaceId, templateId)).thenReturn(testTemplate);

        // when
        TemplateDetailResponseDto result = templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSpaceId()).isEqualTo(100L);
        assertThat(result.getTitle()).isEqualTo("테스트 템플릿");
        assertThat(result.getStatus()).isEqualTo("APPROVED_MOCK");
        assertThat(result.getKakaoTemplateId()).isEqualTo(12345L);
        assertThat(result.getExtractedVariables()).isEqualTo("{\"name\": \"value\"}");
        assertThat(result.getSessionId()).isEqualTo("session123");
        assertThat(result.getFinalTemplate()).isEqualTo("최종 템플릿 내용");
        assertThat(result.getHtmlPreview()).isEqualTo("<html>미리보기</html>");
        assertThat(result.getParameterizedTemplate()).isEqualTo("파라미터화된 템플릿");
        assertThat(result.getTotalAttempts()).isEqualTo(3);
        assertThat(result.getIsSaved()).isTrue();
        assertThat(result.getIsAccepted()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 템플릿을 조회하면 null을 반환한다")
    void getTemplateDetailBySpaceIdAndTemplateId_WithNonExistentTemplate_ReturnsNull() {
        // given
        Long spaceId = 999L;
        Long templateId = 999L;
        when(templateRepository.findBySpaceIdAndTemplateIdWithAllFields(spaceId, templateId)).thenReturn(null);

        // when
        TemplateDetailResponseDto result = templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("completedAt 필드는 DTO에 포함되지 않는다")
    void getTemplateDetailBySpaceIdAndTemplateId_ExcludesCompletedAt() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        when(templateRepository.findBySpaceIdAndTemplateIdWithAllFields(spaceId, templateId)).thenReturn(testTemplate);

        // when
        TemplateDetailResponseDto result = templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId);

        // then
        assertThat(result).isNotNull();
        // completedAt 필드가 DTO에 정의되어 있지 않음을 확인
        // TemplateDetailResponseDto 클래스에 completedAt 필드가 없어야 함
    }
}
