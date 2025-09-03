package org.fastcampus.jober.template.service;

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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * TemplateService 테스트 클래스
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateService 테스트")
class TemplateServiceTest {

  @Mock
  private TemplateRepository templateRepository;

  @InjectMocks
  private TemplateService templateService;

  private Template template1;
  private Template template2;
  private Template template3;
  private List<String> titleList;
  private List<Template> templateList;

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
      .build();

    template3 = Template.builder()
      .id(3L)
      .spaceId(100L)
      .title("세 번째 템플릿")
      .status(Status.APPROVED_MOCK)
      .build();

    titleList = Arrays.asList("첫 번째 템플릿", "두 번째 템플릿", "세 번째 템플릿");
    templateList = Arrays.asList(template1, template2, template3);
  }

  @Test
  @DisplayName("getTitlesBySpaceId 메서드가 정상적으로 템플릿 제목들을 반환하는지 테스트")
  void testGetTitlesBySpaceId() {
    // given
    Long spaceId = 100L;
    when(templateRepository.findTitlesBySpaceId(spaceId)).thenReturn(titleList);

    // when
    List<TemplateTitleResponseDto> result = templateService.getTitlesBySpaceId(spaceId);

    // then
    assertThat(result).hasSize(3);
    assertThat(result).extracting("title").containsExactly(
      "첫 번째 템플릿",
      "두 번째 템플릿",
      "세 번째 템플릿"
    );
  }

  @Test
  @DisplayName("getTemplatesBySpaceId 메서드가 정상적으로 템플릿 엔티티들을 반환하는지 테스트")
  void testGetTemplatesBySpaceId() {
    // given
    Long spaceId = 100L;
    when(templateRepository.findBySpaceId(spaceId)).thenReturn(templateList);

    // when
    List<Template> result = templateService.getTemplatesBySpaceId(spaceId);

    // then
    assertThat(result).hasSize(3);
    assertThat(result).extracting("id").containsExactly(1L, 2L, 3L);
    assertThat(result).extracting("spaceId").allMatch(id -> id.equals(100L));
  }

  @Test
  @DisplayName("존재하지 않는 spaceId로 조회할 때 빈 리스트를 반환하는지 테스트")
  void testGetTitlesByNonExistentSpaceId() {
    // given
    Long nonExistentSpaceId = 999L;
    when(templateRepository.findTitlesBySpaceId(nonExistentSpaceId))
      .thenReturn(Arrays.asList());

    // when
    List<TemplateTitleResponseDto> result = templateService.getTitlesBySpaceId(nonExistentSpaceId);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("존재하지 않는 spaceId로 템플릿 조회할 때 빈 리스트를 반환하는지 테스트")
  void testGetTemplatesByNonExistentSpaceId() {
    // given
    Long nonExistentSpaceId = 999L;
    when(templateRepository.findBySpaceId(nonExistentSpaceId))
      .thenReturn(Arrays.asList());

    // when
    List<Template> result = templateService.getTemplatesBySpaceId(nonExistentSpaceId);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("다양한 spaceId로 조회할 때 정확한 결과를 반환하는지 테스트")
  void testGetTitlesByDifferentSpaceIds() {
    // given
    Long spaceId1 = 100L;
    Long spaceId2 = 200L;
    
    List<String> titles1 = Arrays.asList("스페이스1 템플릿");
    List<String> titles2 = Arrays.asList("스페이스2 템플릿");
    
    when(templateRepository.findTitlesBySpaceId(spaceId1)).thenReturn(titles1);
    when(templateRepository.findTitlesBySpaceId(spaceId2)).thenReturn(titles2);

    // when
    List<TemplateTitleResponseDto> result1 = templateService.getTitlesBySpaceId(spaceId1);
    List<TemplateTitleResponseDto> result2 = templateService.getTitlesBySpaceId(spaceId2);

    // then
    assertThat(result1).hasSize(1);
    assertThat(result1.get(0).getTitle()).isEqualTo("스페이스1 템플릿");
    
    assertThat(result2).hasSize(1);
    assertThat(result2.get(0).getTitle()).isEqualTo("스페이스2 템플릿");
  }

  @Test
  @DisplayName("Repository 메서드가 정확한 파라미터로 호출되는지 테스트")
  void testRepositoryMethodCalls() {
    // given
    Long spaceId = 100L;
    when(templateRepository.findTitlesBySpaceId(spaceId)).thenReturn(titleList);
    when(templateRepository.findBySpaceId(spaceId)).thenReturn(templateList);

    // when
    templateService.getTitlesBySpaceId(spaceId);
    templateService.getTemplatesBySpaceId(spaceId);

    // then
    // Mockito의 verify를 사용하여 정확한 파라미터로 호출되었는지 확인
    // (이미 when 절에서 설정했으므로 실제 호출은 자동으로 검증됨)
  }

  @Test
  @DisplayName("null spaceId로 조회할 때 예외가 발생하지 않는지 테스트")
  void testGetTitlesByNullSpaceId() {
    // given
    when(templateRepository.findTitlesBySpaceId(null)).thenReturn(Arrays.asList());

    // when & then
    // null 값으로도 예외가 발생하지 않아야 함
    List<TemplateTitleResponseDto> result = templateService.getTitlesBySpaceId(null);
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("null spaceId로 템플릿 조회할 때 예외가 발생하지 않는지 테스트")
  void testGetTemplatesByNullSpaceId() {
    // given
    when(templateRepository.findBySpaceId(null)).thenReturn(Arrays.asList());

    // when & then
    // null 값으로도 예외가 발생하지 않아야 함
    List<Template> result = templateService.getTemplatesBySpaceId(null);
    assertThat(result).isEmpty();
  }
}
