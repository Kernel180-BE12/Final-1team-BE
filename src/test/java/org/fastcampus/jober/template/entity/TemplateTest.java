package org.fastcampus.jober.template.entity;

import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.entity.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Template 엔티티 테스트 클래스
 */
@DisplayName("Template 엔티티 테스트")
class TemplateTest {

  private Template template1;
  private Template template2;
  private Template template3;

  @BeforeEach
  void setUp() {
    // 테스트용 템플릿 데이터 생성
    template1 = Template.builder()
      .id(1L)
      .spaceId(100L)
      .title("첫 번째 템플릿")
      .status(Status.DRAFT)
      .kakaoTemplateId(12345L)
      .extractedVariables("{\"name\": \"홍길동\"}")
      .completedAt(LocalDateTime.now())
      .sessionId("session123")
      .finalTemplate("최종 템플릿 내용")
      .htmlPreview("<html>미리보기</html>")
      .parameterizedTemplate("안녕하세요 {name}님")
      .totalAttempts(3)
      .isSaved(true)
      .isAccepted(false)
      .build();

    template2 = Template.builder()
      .id(2L)
      .spaceId(100L)
      .title("두 번째 템플릿")
      .status(Status.SUBMITTED_MOCK)
      .spaceId(100L)
      .build();

    template3 = Template.builder()
      .id(3L)
      .spaceId(200L)
      .title("다른 스페이스 템플릿")
      .status(Status.APPROVED_MOCK)
      .spaceId(200L)
      .build();
  }

  @Test
  @DisplayName("Template 빌더 패턴으로 정상 생성되는지 테스트")
  void testTemplateBuilder() {
    // given & when
    Template template = Template.builder()
      .id(1L)
      .spaceId(100L)
      .title("테스트 템플릿")
      .status(Status.DRAFT)
      .build();

    // then
    assertThat(template.getId()).isEqualTo(1L);
    assertThat(template.getSpaceId()).isEqualTo(100L);
    assertThat(template.getTitle()).isEqualTo("테스트 템플릿");
    assertThat(template.getStatus()).isEqualTo(Status.DRAFT);
  }

  @Test
  @DisplayName("extractTitle 메서드가 정상적으로 제목을 반환하는지 테스트")
  void testExtractTitle() {
    // when
    String title = template1.extractTitle();

    // then
    assertThat(title).isEqualTo("첫 번째 템플릿");
  }

  @Test
  @DisplayName("extractTitles 정적 메서드가 템플릿 리스트에서 제목들만 추출하는지 테스트")
  void testExtractTitles() {
    // given
    List<Template> templates = Arrays.asList(template1, template2, template3);

    // when
    List<String> titles = Template.extractTitles(templates);

    // then
    assertThat(titles).hasSize(3);
    assertThat(titles).containsExactly(
      "첫 번째 템플릿",
      "두 번째 템플릿",
      "다른 스페이스 템플릿"
    );
  }

  @Test
  @DisplayName("filterBySpaceId 정적 메서드가 특정 스페이스의 템플릿만 필터링하는지 테스트")
  void testFilterBySpaceId() {
    // given
    List<Template> templates = Arrays.asList(template1, template2, template3);

    // when
    List<Template> filteredTemplates = Template.filterBySpaceId(templates, 100L);

    // then
    assertThat(filteredTemplates).hasSize(2);
    assertThat(filteredTemplates).allMatch(template -> template.getSpaceId().equals(100L));
    assertThat(filteredTemplates).extracting("id").containsExactly(1L, 2L);
  }

  @Test
  @DisplayName("belongsToSpace 메서드가 템플릿이 특정 스페이스에 속하는지 정확히 판단하는지 테스트")
  void testBelongsToSpace() {
    // when & then
    assertThat(template1.belongsToSpace(100L)).isTrue();
    assertThat(template1.belongsToSpace(200L)).isFalse();
    assertThat(template3.belongsToSpace(200L)).isTrue();
    assertThat(template3.belongsToSpace(100L)).isFalse();
  }

  @Test
  @DisplayName("빈 템플릿 리스트에 대한 메서드들이 정상 동작하는지 테스트")
  void testWithEmptyList() {
    // given
    List<Template> emptyTemplates = Arrays.asList();

    // when
    List<String> emptyTitles = Template.extractTitles(emptyTemplates);
    List<Template> emptyFiltered = Template.filterBySpaceId(emptyTemplates, 100L);

    // then
    assertThat(emptyTitles).isEmpty();
    assertThat(emptyFiltered).isEmpty();
  }

  @Test
  @DisplayName("null 값이 포함된 템플릿 리스트에 대한 안전성 테스트")
  void testWithNullValues() {
    // given
    List<Template> templatesWithNull = Arrays.asList(template1, null, template2);

    // when & then
    // null 값이 포함된 경우에도 예외가 발생하지 않아야 함
    assertThat(Template.extractTitles(templatesWithNull)).hasSize(2);
  }
}
