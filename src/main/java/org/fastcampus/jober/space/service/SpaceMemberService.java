package org.fastcampus.jober.space.service;

import java.util.List;
import java.util.Optional;

import jakarta.mail.MessagingException;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.entity.*;
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

@Service
@RequiredArgsConstructor
public class SpaceMemberService {
  private final SpaceMemberRepository spaceMemberRepository;
  private final SpaceMemberMapper spaceMemberMapper;
  private final SpaceRepository spaceRepository;
  private final UserRepository userRepository;
  private final CustomMailSender customMailSender;


  /**
 * 메일 발송 & 수락
  1. 워크스페이스에서 이메일로 초대 메일 발송 (단, 멤버 초대는 스페이스 관리자만 가능)
  2. 스페이스 멤버인지 검사 후 중복 아닐 시, 초대 테이블에 추가
  3. 이메일로 초대 링크 전송
    3-1) 가입한 회원 → 수락버튼 클릭 -> 스페이스 멤버에 추가
    3-2) 비회원 -> 회원가입 링크 전송 -> 가입 시 스페이스 멤버에 추가
 */
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
//        InviteStatus inviteMember = InviteStatus.builder()
//                .userEmail(dto.getEmail())
//                .status(InviteStatusType.PENDING)
//                .build();
//        inviteStatusRepository.save(inviteMember);
        SpaceMember pendingMember = SpaceMember.builder()
                .space(existingSpace)
                .email(dto.getEmail())
                .authority(dto.getAuthority())
                .user(user)
                .tag(dto.getTag())
                .build();
        spaceMemberRepository.save(pendingMember);

        // 이메일 발송(토근 추후 추가 예정)
        sendInviteEmailToUser(spaceId, dto);
        }

      // 비회원 이메일 발송 (토큰 추후 추가 예정)
      sendInviteEmailToSingUp(spaceId, dto);
      }
  }

  private void sendInviteEmailToUser(Long spaceId, SpaceMemberAddRequestDto dto)
          throws MessagingException {
    String url = "https://www.jober-1team.com/spaces/" + spaceId;
    customMailSender.sendMail(
            dto.getEmail(),
            url,
            "[Jober] 스페이스 멤버 초대",
            "mail/invite",
            "초대 수락 링크"
    );
  }

  private void sendInviteEmailToSingUp(Long spaceId, SpaceMemberAddRequestDto dto) throws MessagingException {
    String url = "https://www.jober-1team.com/register" + spaceId;
    customMailSender.sendMail(
            dto.getEmail(),
            url,
            "[Jober] 스페이스 멤버 초대",
            "mail/signup-invite",
            "회원가입 후 참여하기"
    );
  }

  /** 초대 메일 수락 */
  public String acceptInvitationByEmail(Long spaceId, String email) {
    Users user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "가입되지 않은 회원입니다."));

    // InviteStatus에서 초대 정보 찾기
    SpaceMember pendingMember = spaceMemberRepository.findByUserEmailAndSpaceIdAndStatus(email, spaceId, InviteStatusType.PENDING)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "유효하지 않은 초대입니다."));

    pendingMember.assignUser(user);
    pendingMember.acceptInvite(); // 수락상태로 변경
    spaceMemberRepository.save(pendingMember);

    return "https://www.jober-1team.com/spaces/" + spaceId;
  }

  public List<SpaceMemberResponseDto> getSpaceMembers(Long spaceId) {
    List<SpaceMember> spaceMembers = spaceMemberRepository.findBySpaceId(spaceId);
    return spaceMemberMapper.toResponseDtoList(spaceMembers);
  }
}
