package org.fastcampus.jober.space.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.mail.MessagingException;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.InviteResult;
import org.fastcampus.jober.space.dto.InviteStatus;
import org.fastcampus.jober.space.dto.request.MemberUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.MemberUpdateResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceMemberListResponseDto;
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
  public InviteResult inviteSpaceMember(Long spaceId, List<SpaceMemberAddRequestDto> dtos, CustomUserDetails principal) throws MessagingException {
    Space existingSpace = spaceRepository.findByIdOrThrow(spaceId);

    // 관리자인지 검증
    existingSpace.validateAdminUser(principal.getUserId());

    List<String> successEmails = new ArrayList<>();
    List<String> duplicateEmails = new ArrayList<>();


    // 사용자 조회
    for (SpaceMemberAddRequestDto dto : dtos) {
      Optional<Users> userOpt = userRepository.findByEmail(dto.getEmail());

      if (userOpt.isPresent()) {
        Users user = userOpt.get(); // Optional 안에 있는 Users 꺼냄 ....예외처리?
        // 회원 -> 중복초대 체크
        if (spaceMemberRepository.findBySpaceIdAndUserId(spaceId, user.getUserId()).isPresent()) {
          duplicateEmails.add(dto.getEmail());
          continue;
        }
        sendInviteEmailToUser(spaceId, dto);
      } else sendInviteEmailToSingUp(spaceId, dto);
      successEmails.add(dto.getEmail());

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
    return new InviteResult(successEmails, duplicateEmails);
  }

  private void sendInviteEmailToUser(Long spaceId, SpaceMemberAddRequestDto dto)
          throws MessagingException {
    String url = "https://www.jober-1team.com/invite-member/?spaceId=" + spaceId + "&email=" + dto.getEmail();

    customMailSender.sendMail(
            dto.getEmail(),
            url,
            "[Jober] 스페이스 멤버 초대",
            "mail/invite",
            "초대 수락 링크"
    );
  }

  private void sendInviteEmailToSingUp(Long spaceId, SpaceMemberAddRequestDto dto) throws MessagingException {
    String url = "https://www.jober-1team.com/space-member-register/?spaceId=" + spaceId + "&email=" + dto.getEmail();

    customMailSender.sendMail(
            dto.getEmail(),
            url,
            "[Jober] 스페이스 멤버 초대",
            "mail/signup-invite",
            "회원가입 후 참여하기"
    );
  }

  @Transactional
  public void acceptInvitationByEmail(Long spaceId, String email) {
    Users user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "가입되지 않은 회원입니다."));

    processSpaceInvitation(spaceId, email, user);
  }
  
  @Transactional
  public void processSpaceInvitation(Long spaceId, String email, Users user) {
    Optional<InviteStatus> pendingMemberOpt = inviteStatusRepository
            .findByEmailAndSpaceIdAndStatus(email, spaceId, InviteStatusType.PENDING);
    InviteStatus pendingMember = pendingMemberOpt.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 초대입니다."));

    if (spaceMemberRepository.findBySpaceIdAndUserId(spaceId, user.getUserId()).isPresent()) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 스페이스 멤버입니다.");
    }

    if (pendingMember.getExpireDate().isBefore(LocalDateTime.now())) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "만료된 초대입니다.");
    }

    pendingMember.updateStatus(InviteStatusType.ACCEPTED);
    Space space = spaceRepository.findByIdOrThrow(spaceId);
    SpaceMember spaceMember = pendingMember.toSpaceMember(space, user);
    spaceMemberRepository.save(spaceMember);
  }

  public List<SpaceMemberListResponseDto> getSpaceMembers(Long spaceId) {
    List<SpaceMember> spaceMembers = spaceMemberRepository.findBySpaceId(spaceId);
    return spaceMemberMapper.toMemberResponseDtoList(spaceMembers);
  }

  @Transactional
  public void deleteSpaceMember(List<Long> memberIds, Long spaceId, CustomUserDetails principal) {
    Space existingSpace = spaceRepository.findByIdOrThrow(spaceId);
    existingSpace.validateAdminUser(principal.getUserId());

    List<SpaceMember> members = spaceMemberRepository.findAllByIdInAndSpaceId(memberIds, spaceId);

    if (members.size() != memberIds.size()) {
      throw new BusinessException(ErrorCode.NOT_FOUND, "해당하는 멤버가 없습니다.");}

    members.forEach(SpaceMember::softDelete);

    // 3. 명시적 저장 (혹시 모를 Dirty Checking 문제 방지)
    spaceMemberRepository.saveAll(members);
  }


  @Transactional
  public MemberUpdateResponseDto updateMember(Long memberId, Long spaceId, MemberUpdateRequestDto dto, CustomUserDetails principal) {
    Space existingSpace = spaceRepository.findByIdOrThrow(spaceId);
    existingSpace.validateAdminUser(principal.getUserId());
    SpaceMember member = spaceMemberRepository.findById(memberId)
            .orElseThrow(()-> new BusinessException(ErrorCode.NOT_FOUND, "멤버를 찾을 수 없습니다."));
    member.updateMember(dto);
    return spaceMemberMapper.toMemberUpdateResponseDto(member);
  }

  public List<SpaceMemberListResponseDto> getMemberByTag(Long spaceId, String tag, Long userId) {

    spaceRepository.findByIdOrThrow(spaceId);

    spaceMemberRepository.findBySpaceIdAndUserId(spaceId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN, "해당 스페이스 멤버만 조회할 수 있습니다."));

    List<SpaceMember> members = spaceMemberRepository.findBySpaceIdAndTag(spaceId, tag);

    return spaceMemberMapper.toMemberResponseDtoList(members);

  }

}
