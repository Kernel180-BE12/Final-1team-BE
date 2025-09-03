package org.fastcampus.jober.template.dto.response;

import org.fastcampus.jober.template.entity.Template;
import org.fastcampus.jober.template.entity.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TemplateTitleResponseDto 테스트 클래스
 */
@DisplayName("TemplateTitleResponseDto 테스트")
class TemplateTitleResponseDtoTest {

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
      .build();

    template2 = Template.builder()
      .id(2L)
      .spaceId(100L)
      .title("두 번째 템플릿")
      .status(Status.SUBMITTED_MOCK)
      .build();

    template3 = Template.builder()
      .id(3L)
      .spaceId(200L)
      .title("세 번째 템플릿")
      .status(Status.APPROVED_MOCK)
      .build();
  }

  @Test
  @DisplayName("Template 엔티티로부터 DTO를 정상적으로 생성하는지 테스트")
  void testFromTemplate() {
    // when
    TemplateTitleResponseDto dto = TemplateTitleResponseDto.from(template1);

    // then
    assertThat(dto.getTitle()).isEqualTo("첫 번째 템플릿");
  }

  @Test
  @DisplayName("Template 엔티티 리스트로부터 DTO 리스트를 정상적으로 생성하는지 테스트")
  void testFromTemplateList() {
    // given
    List<Template> templates = Arrays.asList(template1, template2, template3);

    // when
    List<TemplateTitleResponseDto> dtos = TemplateTitleResponseDto.fromList(templates);

    // then
    assertThat(dtos).hasSize(3);
    assertThat(dtos).extracting("title").containsExactly(
      "첫 번째 템플릿",
      "두 번째 템플릿",
      "세 번째 템플릿"
    );
  }

  @Test
  @DisplayName("제목 문자열로부터 DTO를 정상적으로 생성하는지 테스트")
  void testFromTitle() {
    // given
    String title = "테스트 제목";

    // when
    TemplateTitleResponseDto dto = TemplateTitleResponseDto.fromTitle(title);

    // then
    assertThat(dto.getTitle()).isEqualTo("테스트 제목");
  }

  @Test
  @DisplayName("제목 문자열 리스트로부터 DTO 리스트를 정상적으로 생성하는지 테스트")
  void testFromTitleList() {
    // given
    List<String> titles = Arrays.asList("제목1", "제목2", "제목3");

    // when
    List<TemplateTitleResponseDto> dtos = TemplateTitleResponseDto.fromTitleList(titles);

    // then
    assertThat(dtos).hasSize(3);
    assertThat(dtos).extracting("title").containsExactly("제목1", "제목2", "제목3");
  }

  @Test
  @DisplayName("빈 템플릿 리스트로부터 빈 DTO 리스트를 생성하는지 테스트")
  void testFromEmptyTemplateList() {
    // given
    List<Template> emptyTemplates = Arrays.asList();

    // when
    List<TemplateTitleResponseDto> dtos = TemplateTitleResponseDto.fromList(emptyTemplates);

    // then
    assertThat(dtos).isEmpty();
  }

  @Test
  @DisplayName("빈 제목 리스트로부터 빈 DTO 리스트를 생성하는지 테스트")
  void testFromEmptyTitleList() {
    // given
    List<String> emptyTitles = Arrays.asList();

    // when
    List<TemplateTitleResponseDto> dtos = TemplateTitleResponseDto.fromTitleList(emptyTitles);

    // then
    assertThat(dtos).isEmpty();
  }

  @Test
  @DisplayName("null 제목으로 DTO를 생성할 때 정상 동작하는지 테스트")
  void testFromNullTitle() {
    // when
    TemplateTitleResponseDto dto = TemplateTitleResponseDto.fromTitle(null);

    // then
    assertThat(dto.getTitle()).isNull();
  }

  @Test
  @DisplayName("빈 문자열 제목으로 DTO를 생성할 때 정상 동작하는지 테스트")
  void testFromEmptyTitle() {
    // when
    TemplateTitleResponseDto dto = TemplateTitleResponseDto.fromTitle("");

    // then
    assertThat(dto.getTitle()).isEmpty();
  }

  @Test
  @DisplayName("DTO 빌더 패턴이 정상 동작하는지 테스트")
  void testDtoBuilder() {
    // when
    TemplateTitleResponseDto dto = TemplateTitleResponseDto.builder()
      .title("빌더로 생성한 제목")
      .build();

    // then
    assertThat(dto.getTitle()).isEqualTo("빌더로 생성한 제목");
  }
}
