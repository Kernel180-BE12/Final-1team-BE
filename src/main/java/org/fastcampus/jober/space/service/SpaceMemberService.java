package org.fastcampus.jober.space.service;

import java.util.List;
import java.util.Optional;

import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.fastcampus.jober.user.entity.Users;
import org.fastcampus.jober.user.repository.UserRepository;
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
  private final SpaceRepository spaceRepository;
  private final UserRepository userRepository;


/**
 * 메일 발송 & 수락
  1. 워크스페이스에서 이메일로 초대 메일 발송 (단, 멤버 초대는 스페이스 관리자만 가능)
  2. 초대받은 사람이 메일의 ‘수락’ 버튼 클릭
* 사용자 조회
  3. 이메일 기준으로 사용자 존재 여부 확인
  - 존재(기존 회원) → 멤버 테이블에 추가 → 스페이스 페이지 또는 “수락 완료” 화면
  * 없음(비회원) → 회원가입 페이지로 이동
* 비회원 가입 후 처리
  회원가입 완료 시, 바로 스페이스 멤버 테이블에도 추가 (자동 수락)
 */
  public void addSpaceMember(Long spaceId, List<SpaceMemberAddRequestDto> dtos, CustomUserDetails principal) {
    Space space = spaceRepository.findByIdOrThrow(spaceId);

    // 관리자인지 검증

    // 이메일 발송(토근 포함)

    // 사용자 조회
    for (SpaceMemberAddRequestDto dto : dtos) {
      Optional<Users> users = userRepository.findByEmail(dto.getEmail());

      // 회원 -> 중복초대 체크
      if (users != null && users.isPresent()) {
        // 중복이면 에러 던지기
        userRepository.findByEmailAndSpaceId(dto.getEmail(), spaceId)
                .orElse
       // 중복 아니면 멤버 테이블에 추가
      } dto.add

      // 비회원 -> 로그인 화면 이동 -> 멤버 테이블에 추가
    }
  }

  public List<SpaceMemberResponseDto> getSpaceMembers(Long spaceId) {
    List<SpaceMember> spaceMembers = spaceMemberRepository.findBySpaceId(spaceId);
    return spaceMemberMapper.toResponseDtoList(spaceMembers);
  }
}
