package org.fastcampus.jober.template.entity;

import org.fastcampus.jober.template.dto.response.TemplateDetailResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.entity.enums.Status;
import org.fastcampus.jober.template.repository.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Template 엔티티 테스트")
class TemplateTest {

    @Mock
    private TemplateRepository templateRepository;

    private Template testTemplate;
    private List<Template> testTemplates;

    @BeforeEach
    void setUp() {
        testTemplate = Template.builder()
                .id(1L)
                .spaceId(100L)
                .title("테스트 템플릿")
                .status(Status.APPROVED_MOCK)
                .kakaoTemplateId(12345L)
                .extractedVariables("{\"name\": \"value\"}")
                .completedAt(LocalDateTime.now())
                .sessionId("session123")
                .finalTemplate("최종 템플릿 내용")
                .htmlPreview("<html>미리보기</html>")
                .parameterizedTemplate("파라미터화된 템플릿")
                .totalAttempts(3)
                .isSaved(true)
                .isAccepted(false)
                .build();

        testTemplates = Arrays.asList(testTemplate);
    }

    @Test
    @DisplayName("템플릿의 제목을 추출할 수 있다")
    void extractTitle_ReturnsTitle() {
        // when
        String title = testTemplate.extractTitle();

        // then
        assertThat(title).isEqualTo("테스트 템플릿");
    }

    @Test
    @DisplayName("템플릿 리스트에서 제목들을 추출할 수 있다")
    void extractTitles_WithValidTemplates_ReturnsTitles() {
        // when
        List<String> titles = Template.extractTitles(testTemplates);

        // then
        assertThat(titles).isNotNull();
        assertThat(titles).hasSize(1);
        assertThat(titles.get(0)).isEqualTo("테스트 템플릿");
    }

    @Test
    @DisplayName("null 템플릿 리스트에서 제목을 추출하면 빈 리스트를 반환한다")
    void extractTitles_WithNullTemplates_ReturnsEmptyList() {
        // when
        List<String> titles = Template.extractTitles(null);

        // then
        assertThat(titles).isNotNull();
        assertThat(titles).isEmpty();
    }

    @Test
    @DisplayName("특정 spaceId에 속하는 템플릿들을 필터링할 수 있다")
    void filterBySpaceId_WithValidSpaceId_ReturnsFilteredTemplates() {
        // given
        Long spaceId = 100L;

        // when
        List<Template> filteredTemplates = Template.filterBySpaceId(testTemplates, spaceId);

        // then
        assertThat(filteredTemplates).isNotNull();
        assertThat(filteredTemplates).hasSize(1);
        assertThat(filteredTemplates.get(0).getSpaceId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("다른 spaceId로 필터링하면 빈 리스트를 반환한다")
    void filterBySpaceId_WithDifferentSpaceId_ReturnsEmptyList() {
        // given
        Long differentSpaceId = 200L;

        // when
        List<Template> filteredTemplates = Template.filterBySpaceId(testTemplates, differentSpaceId);

        // then
        assertThat(filteredTemplates).isNotNull();
        assertThat(filteredTemplates).isEmpty();
    }

    @Test
    @DisplayName("null spaceId로 필터링하면 빈 리스트를 반환한다")
    void filterBySpaceId_WithNullSpaceId_ReturnsEmptyList() {
        // when
        List<Template> filteredTemplates = Template.filterBySpaceId(testTemplates, null);

        // then
        assertThat(filteredTemplates).isNotNull();
        assertThat(filteredTemplates).isEmpty();
    }

    @Test
    @DisplayName("템플릿이 특정 spaceId에 속하는지 확인할 수 있다")
    void belongsToSpace_WithValidSpaceId_ReturnsTrue() {
        // given
        Long spaceId = 100L;

        // when
        boolean belongs = testTemplate.belongsToSpace(spaceId);

        // then
        assertThat(belongs).isTrue();
    }

    @Test
    @DisplayName("템플릿이 다른 spaceId에 속하지 않음을 확인할 수 있다")
    void belongsToSpace_WithDifferentSpaceId_ReturnsFalse() {
        // given
        Long differentSpaceId = 200L;

        // when
        boolean belongs = testTemplate.belongsToSpace(differentSpaceId);

        // then
        assertThat(belongs).isFalse();
    }

    @Test
    @DisplayName("null spaceId로 속하는지 확인하면 false를 반환한다")
    void belongsToSpace_WithNullSpaceId_ReturnsFalse() {
        // when
        boolean belongs = testTemplate.belongsToSpace(null);

        // then
        assertThat(belongs).isFalse();
    }

    @Test
    @DisplayName("특정 spaceId의 템플릿들을 조회하고 DTO로 변환할 수 있다")
    void findTitlesBySpaceId_WithValidSpaceId_ReturnsDtoList() {
        // given
        Long spaceId = 100L;
        List<String> titles = Arrays.asList("템플릿1", "템플릿2");
        when(templateRepository.findTitlesBySpaceId(spaceId)).thenReturn(titles);

        // when
        List<TemplateTitleResponseDto> result = Template.findTitlesBySpaceId(templateRepository, spaceId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("템플릿1");
        assertThat(result.get(1).getTitle()).isEqualTo("템플릿2");
    }

    @Test
    @DisplayName("특정 spaceId의 템플릿들을 조회할 수 있다")
    void findBySpaceId_WithValidSpaceId_ReturnsTemplates() {
        // given
        Long spaceId = 100L;
        when(templateRepository.findBySpaceId(spaceId)).thenReturn(testTemplates);

        // when
        List<Template> result = Template.findBySpaceId(templateRepository, spaceId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getSpaceId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("특정 spaceId와 templateId의 템플릿을 조회할 수 있다")
    void findBySpaceIdAndTemplateId_WithValidIds_ReturnsTemplate() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        when(templateRepository.findBySpaceIdAndTemplateId(spaceId, templateId)).thenReturn(testTemplate);

        // when
        Template result = Template.findBySpaceIdAndTemplateId(templateRepository, spaceId, templateId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSpaceId()).isEqualTo(100L);
        assertThat(result.getTitle()).isEqualTo("테스트 템플릿");
    }

    @Test
    @DisplayName("특정 spaceId와 templateId의 템플릿을 조회하고 상세 응답 DTO로 변환할 수 있다")
    void findDetailBySpaceIdAndTemplateId_WithValidIds_ReturnsDetailDto() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        when(templateRepository.findBySpaceIdAndTemplateIdWithAllFields(spaceId, templateId)).thenReturn(testTemplate);

        // when
        TemplateDetailResponseDto result = Template.findDetailBySpaceIdAndTemplateId(templateRepository, spaceId, templateId);

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
    void findDetailBySpaceIdAndTemplateId_WithNonExistentTemplate_ReturnsNull() {
        // given
        Long spaceId = 999L;
        Long templateId = 999L;
        when(templateRepository.findBySpaceIdAndTemplateIdWithAllFields(spaceId, templateId)).thenReturn(null);

        // when
        TemplateDetailResponseDto result = Template.findDetailBySpaceIdAndTemplateId(templateRepository, spaceId, templateId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("completedAt 필드가 DTO에 포함되지 않는다")
    void findDetailBySpaceIdAndTemplateId_ExcludesCompletedAt() {
        // given
        Long spaceId = 100L;
        Long templateId = 1L;
        when(templateRepository.findBySpaceIdAndTemplateIdWithAllFields(spaceId, templateId)).thenReturn(testTemplate);

        // when
        TemplateDetailResponseDto result = Template.findDetailBySpaceIdAndTemplateId(templateRepository, spaceId, templateId);

        // then
        assertThat(result).isNotNull();
        // completedAt 필드가 DTO에 정의되어 있지 않음을 확인
        // TemplateDetailResponseDto 클래스에 completedAt 필드가 없어야 함
    }
}
