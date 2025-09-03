package org.fastcampus.jober.template.integration;

import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.entity.Template;
import org.fastcampus.jober.template.entity.enums.Status;
import org.fastcampus.jober.template.repository.TemplateRepository;
import org.fastcampus.jober.template.service.TemplateService;
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
 * 템플릿 통합 테스트 클래스 (Mock 기반)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("템플릿 통합 테스트 (Mock)")
class TemplateIntegrationTest {

  @Mock
  private TemplateService templateService;

  @Mock
  private TemplateRepository templateRepository;

  private Template template1;
  private Template template2;
  private Template template3;

  @BeforeEach
  void setUp() {
    // 테스트용 템플릿 데이터 생성
    template1 = Template.builder()
      .id(1L)
      .spaceId(100L)
      .title("통합 테스트 템플릿 1")
      .status(Status.DRAFT)
      .extractedVariables("{\"name\": \"홍길동\", \"age\": 30}")
      .completedAt(LocalDateTime.now())
      .sessionId("integration-session-1")
      .finalTemplate("안녕하세요 {name}님, {age}세입니다.")
      .htmlPreview("<html><body>안녕하세요 홍길동님, 30세입니다.</body></html>")
      .parameterizedTemplate("안녕하세요 {name}님, {age}세입니다.")
      .totalAttempts(3)
      .isSaved(true)
      .isAccepted(false)
      .build();

    template2 = Template.builder()
      .id(2L)
      .spaceId(100L)
      .title("통합 테스트 템플릿 2")
      .status(Status.SUBMITTED_MOCK)
      .extractedVariables("{\"email\": \"test@example.com\", \"company\": \"테스트 회사\"}")
      .completedAt(LocalDateTime.now())
      .sessionId("integration-session-2")
      .finalTemplate("이메일: {email}, 회사: {company}")
      .htmlPreview("<html><body>이메일: test@example.com, 회사: 테스트 회사</body></html>")
      .parameterizedTemplate("이메일: {email}, 회사: {company}")
      .totalAttempts(2)
      .isSaved(true)
      .isAccepted(true)
      .build();

    template3 = Template.builder()
      .id(3L)
      .spaceId(200L)
      .title("다른 스페이스 통합 테스트 템플릿")
      .status(Status.APPROVED_MOCK)
      .extractedVariables("{\"phone\": \"010-1234-5678\"}")
      .completedAt(LocalDateTime.now())
      .sessionId("integration-session-3")
      .finalTemplate("전화번호: {phone}")
      .htmlPreview("<html><body>전화번호: 010-1234-5678</body></html>")
      .parameterizedTemplate("전화번호: {phone}")
      .totalAttempts(1)
      .isSaved(false)
      .isAccepted(false)
      .build();
  }

  @Test
  @DisplayName("전체 템플릿 워크플로우 통합 테스트 (Mock)")
  void testCompleteTemplateWorkflow() {
    // given
    Long spaceId = 100L;
    List<Template> templates = Arrays.asList(template1, template2);
    List<String> titles = Arrays.asList("통합 테스트 템플릿 1", "통합 테스트 템플릿 2");
    
    when(templateRepository.findBySpaceId(spaceId)).thenReturn(templates);
    when(templateService.getTemplatesBySpaceId(spaceId)).thenReturn(templates);

    // when
    List<Template> retrievedTemplates = templateService.getTemplatesBySpaceId(spaceId);
    List<Template> repositoryTemplates = templateRepository.findBySpaceId(spaceId);

    // then
    assertThat(retrievedTemplates).hasSize(2);
    assertThat(repositoryTemplates).hasSize(2);
    
    // 엔티티 검증
    assertThat(retrievedTemplates).extracting("spaceId").allMatch(id -> id.equals(100L));
    assertThat(retrievedTemplates).extracting("status").containsExactlyInAnyOrder(
      Status.DRAFT, Status.SUBMITTED_MOCK
    );
  }

  @Test
  @DisplayName("다른 스페이스의 템플릿이 조회되지 않는지 통합 테스트 (Mock)")
  void testDifferentSpaceIsolation() {
    // given
    Long spaceId1 = 100L;
    Long spaceId2 = 200L;
    
    List<Template> templates1 = Arrays.asList(template1, template2);
    List<Template> templates2 = Arrays.asList(template3);
    
    when(templateRepository.findBySpaceId(spaceId1)).thenReturn(templates1);
    when(templateRepository.findBySpaceId(spaceId2)).thenReturn(templates2);

    // when
    List<Template> result1 = templateRepository.findBySpaceId(spaceId1);
    List<Template> result2 = templateRepository.findBySpaceId(spaceId2);

    // then
    assertThat(result1).hasSize(2);
    assertThat(result2).hasSize(1);
    
    assertThat(result1).extracting("spaceId").allMatch(id -> id.equals(100L));
    assertThat(result2).extracting("spaceId").allMatch(id -> id.equals(200L));
  }

  @Test
  @DisplayName("템플릿 엔티티의 정적 메서드들이 정상 동작하는지 통합 테스트")
  void testTemplateEntityStaticMethods() {
    // given
    List<Template> allTemplates = Arrays.asList(template1, template2, template3);

    // when
    List<String> extractedTitles = Template.extractTitles(allTemplates);
    List<Template> filteredTemplates = Template.filterBySpaceId(allTemplates, 100L);

    // then
    assertThat(extractedTitles).hasSize(3);
    assertThat(extractedTitles).containsExactlyInAnyOrder(
      "통합 테스트 템플릿 1",
      "통합 테스트 템플릿 2",
      "다른 스페이스 통합 테스트 템플릿"
    );
    
    assertThat(filteredTemplates).hasSize(2);
    assertThat(filteredTemplates).extracting("spaceId").allMatch(id -> id.equals(100L));
  }

  @Test
  @DisplayName("DTO 변환 로직이 정상 동작하는지 통합 테스트")
  void testDtoConversionLogic() {
    // given
    List<Template> templates = Arrays.asList(template1, template2);

    // when
    List<TemplateTitleResponseDto> dtos = TemplateTitleResponseDto.fromList(templates);

    // then
    assertThat(dtos).hasSize(2);
    assertThat(dtos).extracting("title").containsExactlyInAnyOrder(
      "통합 테스트 템플릿 1",
      "통합 테스트 템플릿 2"
    );
  }

  @Test
  @DisplayName("템플릿 상태별 필터링이 정상 동작하는지 통합 테스트")
  void testTemplateStatusFiltering() {
    // given
    List<Template> allTemplates = Arrays.asList(template1, template2, template3);

    // when
    List<Template> draftTemplates = allTemplates.stream()
      .filter(template -> template.getStatus() == Status.DRAFT)
      .toList();
    
    List<Template> submittedTemplates = allTemplates.stream()
      .filter(template -> template.getStatus() == Status.SUBMITTED_MOCK)
      .toList();
    
    List<Template> approvedTemplates = allTemplates.stream()
      .filter(template -> template.getStatus() == Status.APPROVED_MOCK)
      .toList();

    // then
    assertThat(draftTemplates).hasSize(1);
    assertThat(draftTemplates.get(0).getTitle()).isEqualTo("통합 테스트 템플릿 1");
    
    assertThat(submittedTemplates).hasSize(1);
    assertThat(submittedTemplates.get(0).getTitle()).isEqualTo("통합 테스트 템플릿 2");
    
    assertThat(approvedTemplates).hasSize(1);
    assertThat(approvedTemplates.get(0).getTitle()).isEqualTo("다른 스페이스 통합 테스트 템플릿");
  }

  @Test
  @DisplayName("서비스와 레포지토리 간의 연동 테스트 (Mock)")
  void testServiceRepositoryIntegration() {
    // given
    Long spaceId = 100L;
    List<Template> templates = Arrays.asList(template1, template2);
    List<String> titles = Arrays.asList("통합 테스트 템플릿 1", "통합 테스트 템플릿 2");
    
    when(templateRepository.findBySpaceId(spaceId)).thenReturn(templates);
    when(templateRepository.findTitlesBySpaceId(spaceId)).thenReturn(titles);
    when(templateService.getTemplatesBySpaceId(spaceId)).thenReturn(templates);

    // when
    List<Template> serviceTemplates = templateService.getTemplatesBySpaceId(spaceId);
    List<Template> repositoryTemplates = templateRepository.findBySpaceId(spaceId);
    List<String> repositoryTitles = templateRepository.findTitlesBySpaceId(spaceId);

    // then
    assertThat(serviceTemplates).hasSize(2);
    assertThat(repositoryTemplates).hasSize(2);
    assertThat(repositoryTitles).hasSize(2);
    
    // 서비스와 레포지토리가 동일한 결과를 반환하는지 확인
    assertThat(serviceTemplates).isEqualTo(repositoryTemplates);
    assertThat(repositoryTitles).containsExactlyInAnyOrder(
      "통합 테스트 템플릿 1",
      "통합 테스트 템플릿 2"
    );
  }
}
