package org.fastcampus.jober.user.service;

import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.fastcampus.jober.user.dto.request.RegisterRequestDto;
import org.fastcampus.jober.user.dto.request.UpdateRequestDto;
import org.fastcampus.jober.user.dto.response.UserInfoResponseDto;
import org.fastcampus.jober.user.entity.Users;
import org.fastcampus.jober.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long getUserId(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow()
                .getUserId();
    }

    public void register(RegisterRequestDto req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        userRepository.save(req.toEntity());
    }

    /**
     * 사용자 정보 조회
     * @param principal 현재 로그인된 사용자 정보
     * @return 사용자 정보
     */
    public UserInfoResponseDto getUserInfo(CustomUserDetails principal) {
        Users user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserInfoResponseDto.fromEntity(user);
    }

    /**
     * 사용자 정보 수정 (현재 로그인된 사용자)
     * @param req 수정할 사용자 정보
     * @param principal 현재 로그인된 사용자 정보
     * @return 변경사항이 있었는지 여부
     */
    @Transactional
    public boolean update(UpdateRequestDto req, CustomUserDetails principal) {
        Users user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 변경사항이 있는지 먼저 확인
        if (!req.hasChanges(user)) {
            return false; // 변경사항이 없으면 저장하지 않음
        }
        
        // DTO를 통해 엔티티 업데이트 (변경된 필드만)
        boolean hasChanges = req.updateEntity(user);
        
        // 변경사항이 있을 때만 저장
        if (hasChanges) {
            userRepository.save(user);
        }
        
        return hasChanges;
    }

    /**
     * 사용자명 중복 확인
     * @param username 확인할 사용자명
     * @return 중복 여부
     */
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 이메일 중복 확인
     * @param email 확인할 이메일
     * @return 중복 여부
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
