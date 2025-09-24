package org.fastcampus.jober.space.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.mail.MessagingException;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.entity.*;
import org.fastcampus.jober.space.repository.InviteStatusRepository;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.fastcampus.jober.user.entity.Users;
import org.fastcampus.jober.user.repository.UserRepository;
import org.fastcampus.jober.util.CustomMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.fastcampus.jober.space.dto.request.SpaceMemberAddRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceMemberResponseDto;
import org.fastcampus.jober.space.mapper.SpaceMemberMapper;
import org.fastcampus.jober.space.repository.SpaceMemberRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpaceMemberService {
  private final SpaceMemberRepository spaceMemberRepository;
  private final SpaceMemberMapper spaceMemberMapper;
  private final SpaceRepository spaceRepository;
  private final UserRepository userRepository;
  private final CustomMailSender customMailSender;
  private final InviteStatusRepository inviteStatusRepository;


  /**
 * 메일 발송 & 수락
  1. 워크스페이스에서 이메일로 초대 메일 발송 (단, 멤버 초대는 스페이스 관리자만 가능)
  2. 스페이스 멤버인지 검사 후 중복 아닐 시, 초대 테이블에 추가
  3. 이메일로 초대 링크 전송
    3-1) 가입한 회원 → 수락버튼 클릭 -> 스페이스 멤버에 추가
    3-2) 비회원 -> 회원가입 링크 전송 -> 가입 시 스페이스 멤버에 추가
 */
  @Transactional
  public void inviteSpaceMember(Long spaceId, List<SpaceMemberAddRequestDto> dtos, CustomUserDetails principal) throws MessagingException {
    Space existingSpace = spaceRepository.findByIdOrThrow(spaceId);

    // 관리자인지 검증
    existingSpace.validateAdminUser(principal.getUserId());

    // 사용자 조회
    for (SpaceMemberAddRequestDto dto : dtos) {
      Optional<Users> userOpt = userRepository.findByEmail(dto.getEmail());

      if (userOpt.isPresent()) {
        Users user = userOpt.get(); // Optional 안에 있는 Users 꺼냄 ....예외처리?
        // 회원 -> 중복초대 체크
        if (spaceMemberRepository.findBySpaceIdAndUserId(spaceId, user.getUserId()).isPresent()) {
          throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 멤버인 회원입니다");
        }
        sendInviteEmailToUser(spaceId, dto, dto.getEmail());
      } else sendInviteEmailToSingUp(spaceId, dto);

      InviteStatus inviteMember = InviteStatus.builder()
              .authority(dto.getAuthority())
              .tag(dto.getTag())
              .email(dto.getEmail())
              .status(InviteStatusType.PENDING)
              .spaceId(spaceId)
              .expireDate(LocalDateTime.now().plusDays(10))
              .build();
      inviteStatusRepository.save(inviteMember);
    }
  }

  private void sendInviteEmailToUser(Long spaceId, SpaceMemberAddRequestDto dto, String email)
          throws MessagingException {
    String url = "https://www.jober-1team.com/spaceMembers/" + spaceId + "/accept?email=" + email;
    customMailSender.sendMail(
            dto.getEmail(),
            url,
            "[Jober] 스페이스 멤버 초대",
            "mail/invite",
            "초대 수락 링크"
    );
  }

  private void sendInviteEmailToSingUp(Long spaceId, SpaceMemberAddRequestDto dto) throws MessagingException {
    String url = "https://www.jober-1team.com/register"; // 여기도 회원가입 구현 후 수정해야 함
    customMailSender.sendMail(
            dto.getEmail(),
            url,
            "[Jober] 스페이스 멤버 초대",
            "mail/signup-invite",
            "회원가입 후 참여하기"
    );
  }

  /** 초대 메일 수락 */
  @Transactional
  public void acceptInvitationByEmail(Long spaceId, String email) {
    Users user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "가입되지 않은 회원입니다."));

    // InviteStatus에서 초대 정보 찾기
    Optional<InviteStatus> pendingMemberOpt = inviteStatusRepository.findByEmailAndSpaceId(email, spaceId);
    InviteStatus pendingMember = pendingMemberOpt.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 초대입니다."));

    if (pendingMember.getStatus() == InviteStatusType.ACCEPTED) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 초대가 완료된 회원입니다.");
    } else if (pendingMember.getStatus() == InviteStatusType.DECLINED) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "초대를 거절한 회원입니다.");
    }
/** 위에랑 아래 중에 뭐가 더 나은지 궁금
    // 2. 해당 스페이스의 PENDING 상태 초대 찾기
    InviteStatus pendingMember = inviteStatusRepository
            .findByEmailAndSpaceIdAndStatus(email, spaceId, InviteStatusType.PENDING)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "유효하지 않은 초대입니다."));

    // 3. 이미 스페이스 멤버인지 확인 (중복 방지)
    if (spaceMemberRepository.findBySpaceIdAndUserId(spaceId, user.getUserId()).isPresent()) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 스페이스 멤버입니다.");
    }
    */

    pendingMember.updateStatus(InviteStatusType.ACCEPTED);

    Space space = spaceRepository.findByIdOrThrow(spaceId);
    SpaceMember spaceMember = pendingMember.toSpaceMember(space, user);
    spaceMemberRepository.save(spaceMember);
  }

  public List<SpaceMemberResponseDto> getSpaceMembers(Long spaceId) {
    List<SpaceMember> spaceMembers = spaceMemberRepository.findBySpaceId(spaceId);
    return spaceMemberMapper.toResponseDtoList(spaceMembers);
  }
}
