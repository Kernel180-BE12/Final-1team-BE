package org.fastcampus.jober.template.dto.response;

import org.fastcampus.jober.template.entity.Template;
import org.fastcampus.jober.template.entity.enums.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TemplateDetailResponseDto 테스트")
class TemplateDetailResponseDtoTest {

    @Test
    @DisplayName("Template 엔티티로부터 DTO를 생성할 수 있다")
    void from_WithValidTemplate_ReturnsDto() {
        // given
        Template template = Template.builder()
                .id(1L)
                .spaceId(100L)
                .title("테스트 템플릿")
                .status(Status.APPROVED_MOCK)
                .extractedVariables("{\"name\": \"value\"}")
                .sessionId("session123")
                .finalTemplate("최종 템플릿 내용")
                .htmlPreview("<html>미리보기</html>")
                .parameterizedTemplate("파라미터화된 템플릿")
                .totalAttempts(3)
                .isSaved(true)
                .isAccepted(false)
                .build();

        // when
        TemplateDetailResponseDto dto = TemplateDetailResponseDto.from(template);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getSpaceId()).isEqualTo(100L);
        assertThat(dto.getTitle()).isEqualTo("테스트 템플릿");
        assertThat(dto.getStatus()).isEqualTo("APPROVED_MOCK");
        assertThat(dto.getExtractedVariables()).isEqualTo("{\"name\": \"value\"}");
        assertThat(dto.getSessionId()).isEqualTo("session123");
        assertThat(dto.getFinalTemplate()).isEqualTo("최종 템플릿 내용");
        assertThat(dto.getHtmlPreview()).isEqualTo("<html>미리보기</html>");
        assertThat(dto.getParameterizedTemplate()).isEqualTo("파라미터화된 템플릿");
        assertThat(dto.getTotalAttempts()).isEqualTo(3);
        assertThat(dto.getIsSaved()).isTrue();
        assertThat(dto.getIsAccepted()).isFalse();
    }

    @Test
    @DisplayName("null Template으로부터 DTO를 생성하면 null을 반환한다")
    void from_WithNullTemplate_ReturnsNull() {
        // when
        TemplateDetailResponseDto dto = TemplateDetailResponseDto.from(null);

        // then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("status가 null인 Template으로부터 DTO를 생성할 수 있다")
    void from_WithNullStatus_ReturnsDtoWithNullStatus() {
        // given
        Template template = Template.builder()
                .id(1L)
                .spaceId(100L)
                .title("테스트 템플릿")
                .status(null)
                .build();

        // when
        TemplateDetailResponseDto dto = TemplateDetailResponseDto.from(template);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getStatus()).isNull();
    }

    @Test
    @DisplayName("DTO가 유효한지 검증할 수 있다")
    void isValid_WithValidDto_ReturnsTrue() {
        // given
        TemplateDetailResponseDto dto = TemplateDetailResponseDto.builder()
                .id(1L)
                .spaceId(100L)
                .title("테스트 템플릿")
                .build();

        // when & then
        assertThat(dto.isValid()).isTrue();
    }

    @Test
    @DisplayName("필수 필드가 없는 DTO는 유효하지 않다")
    void isValid_WithInvalidDto_ReturnsFalse() {
        // given
        TemplateDetailResponseDto dtoWithoutId = TemplateDetailResponseDto.builder()
                .spaceId(100L)
                .title("테스트 템플릿")
                .build();

        TemplateDetailResponseDto dtoWithoutSpaceId = TemplateDetailResponseDto.builder()
                .id(1L)
                .title("테스트 템플릿")
                .build();

        TemplateDetailResponseDto dtoWithoutTitle = TemplateDetailResponseDto.builder()
                .id(1L)
                .spaceId(100L)
                .build();

        TemplateDetailResponseDto dtoWithEmptyTitle = TemplateDetailResponseDto.builder()
                .id(1L)
                .spaceId(100L)
                .title("")
                .build();

        // when & then
        assertThat(dtoWithoutId.isValid()).isFalse();
        assertThat(dtoWithoutSpaceId.isValid()).isFalse();
        assertThat(dtoWithoutTitle.isValid()).isFalse();
        assertThat(dtoWithEmptyTitle.isValid()).isFalse();
    }

    @Test
    @DisplayName("안전한 제목을 반환할 수 있다")
    void getSafeTitle_WithValidTitle_ReturnsTitle() {
        // given
        TemplateDetailResponseDto dto = TemplateDetailResponseDto.builder()
                .title("테스트 템플릿")
                .build();

        // when & then
        assertThat(dto.getSafeTitle()).isEqualTo("테스트 템플릿");
    }

    @Test
    @DisplayName("제목이 null인 경우 빈 문자열을 반환한다")
    void getSafeTitle_WithNullTitle_ReturnsEmptyString() {
        // given
        TemplateDetailResponseDto dto = TemplateDetailResponseDto.builder()
                .title(null)
                .build();

        // when & then
        assertThat(dto.getSafeTitle()).isEqualTo("");
    }
}
