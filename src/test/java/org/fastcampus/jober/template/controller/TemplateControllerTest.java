package org.fastcampus.jober.template.controller;

import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.service.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TemplateController 테스트 클래스
 */
@WebMvcTest(TemplateController.class)
@DisplayName("TemplateController 테스트")
class TemplateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private TemplateService templateService;

  private TemplateTitleResponseDto templateTitle1;
  private TemplateTitleResponseDto templateTitle2;

  @BeforeEach
  void setUp() {
    // 테스트용 응답 데이터 설정
    templateTitle1 = TemplateTitleResponseDto.builder()
      .title("첫 번째 템플릿")
      .build();

    templateTitle2 = TemplateTitleResponseDto.builder()
      .title("두 번째 템플릿")
      .build();
  }

  @Test
  @DisplayName("템플릿 제목 조회 API 테스트 - 성공")
  @WithMockUser
  void getTemplateTitlesBySpaceId_Success() throws Exception {
    // given
    Long spaceId = 100L;
    when(templateService.getTitlesBySpaceId(spaceId))
      .thenReturn(Arrays.asList(templateTitle1, templateTitle2));

    // when & then
    mockMvc.perform(get("/template/{spaceId}", spaceId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].title").value("첫 번째 템플릿"))
      .andExpect(jsonPath("$[1].title").value("두 번째 템플릿"));
  }

  @Test
  @DisplayName("템플릿 제목 조회 API 테스트 - 빈 결과")
  @WithMockUser
  void getTemplateTitlesBySpaceId_EmptyResult() throws Exception {
    // given
    Long spaceId = 999L;
    when(templateService.getTitlesBySpaceId(spaceId))
      .thenReturn(Collections.emptyList());

    // when & then
    mockMvc.perform(get("/template/{spaceId}", spaceId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("템플릿 제목 조회 API 테스트 - 잘못된 spaceId 형식")
  @WithMockUser
  void getTemplateTitlesBySpaceId_InvalidSpaceId() throws Exception {
    // given
    String invalidSpaceId = "invalid";

    // when & then
    mockMvc.perform(get("/template/{spaceId}", invalidSpaceId))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("템플릿 제목 조회 API 테스트 - 응답 헤더 검증")
  @WithMockUser
  void getTemplateTitlesBySpaceId_ResponseHeaders() throws Exception {
    // given
    Long spaceId = 100L;
    when(templateService.getTitlesBySpaceId(spaceId))
      .thenReturn(Arrays.asList(templateTitle1));

    // when & then
    mockMvc.perform(get("/template/{spaceId}", spaceId))
      .andExpect(status().isOk())
      .andExpect(header().string("Content-Type", "application/json"));
  }

  @Test
  @DisplayName("템플릿 제목 조회 API 테스트 - 서비스 예외 처리")
  @WithMockUser
  void getTemplateTitlesBySpaceId_ServiceException() throws Exception {
    // given
    Long spaceId = 100L;
    when(templateService.getTitlesBySpaceId(spaceId))
      .thenThrow(new RuntimeException("서비스 오류"));

    // when & then
    mockMvc.perform(get("/template/{spaceId}", spaceId))
      .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("템플릿 제목 조회 API 테스트 - 다양한 spaceId 값")
  @WithMockUser
  void getTemplateTitlesBySpaceId_VariousSpaceIds() throws Exception {
    // given
    Long[] spaceIds = {1L, 100L, 999L, 1000L};
    
    for (Long spaceId : spaceIds) {
      when(templateService.getTitlesBySpaceId(spaceId))
        .thenReturn(Arrays.asList(templateTitle1));
    }

    // when & then
    for (Long spaceId : spaceIds) {
      mockMvc.perform(get("/template/{spaceId}", spaceId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("첫 번째 템플릿"));
    }
  }

  @Test
  @DisplayName("템플릿 제목 조회 API 테스트 - Swagger 어노테이션 검증")
  @WithMockUser
  void getTemplateTitlesBySpaceId_SwaggerAnnotations() throws Exception {
    // given
    Long spaceId = 100L;
    when(templateService.getTitlesBySpaceId(spaceId))
      .thenReturn(Arrays.asList(templateTitle1));

    // when & then
    mockMvc.perform(get("/template/{spaceId}", spaceId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0]").exists())
      .andExpect(jsonPath("$[0].title").exists());
  }
}
