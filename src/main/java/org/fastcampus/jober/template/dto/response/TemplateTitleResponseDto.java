package org.fastcampus.jober.template.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.template.entity.Template;

/** 템플릿 제목 응답 DTO */
@Schema(description = "템플릿 제목 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateTitleResponseDto {

  @Schema(description = "템플릿 제목", example = "카카오톡 알림 템플릿", maxLength = 120)
  private String title; // 템플릿 제목

  /**
   * 제목 문자열로부터 DTO를 생성합니다.
   *
   * @param template 템플릿 제목
   * @return TemplateTitleResponseDto
   */
  public static TemplateTitleResponseDto from(Template template) {
    return TemplateTitleResponseDto.builder().title(template.getTitle()).build();
  }

  /**
   * 제목 문자열 리스트로부터 DTO 리스트를 생성합니다.
   *
   * @param titles 템플릿 제목 리스트
   * @return TemplateTitleResponseDto 리스트
   */
  public static List<TemplateTitleResponseDto> fromList(List<Template> titles) {
    return titles.stream().map(TemplateTitleResponseDto::from).collect(Collectors.toList());
  }
}
