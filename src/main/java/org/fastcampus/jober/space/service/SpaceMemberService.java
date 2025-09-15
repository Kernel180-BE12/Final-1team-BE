package org.fastcampus.jober.space.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.fastcampus.jober.space.dto.request.SpaceMemberAddRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceMemberResponseDto;
import org.fastcampus.jober.space.entity.SpaceMember;
import org.fastcampus.jober.space.mapper.SpaceMemberMapper;
import org.fastcampus.jober.space.repository.SpaceMemberRepository;

@Service
@RequiredArgsConstructor
public class SpaceMemberService {
  private final SpaceMemberRepository spaceMemberRepository;
  private final SpaceMemberMapper spaceMemberMapper;

  public void addSpaceMember(SpaceMemberAddRequestDto dto) {
    /**
     * 한명씩 / 여러명 초대하기 1. 한명인경우 1-1 이름 검색 후 목록에서 선택 1-2 연락처에 없는 회원일 경우 생성 1) 이름, 전화번호/메일, 태그, 권한 설정 후
     * 초대하기
     *
     * <p>2. 여러명인 경우 2-1. 이름과 번호/메일 리스트로 적기 - 행 추가 가능 2-2 <1-1>과 동일한 로직으로 진행
     */
  }

  public List<SpaceMemberResponseDto> getSpaceMembers(Long spaceId) {
    List<SpaceMember> spaceMembers = spaceMemberRepository.findBySpaceId(spaceId);
    return spaceMemberMapper.toResponseDtoList(spaceMembers);
  }
}
