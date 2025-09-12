package org.fastcampus.jober.user.service;

import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
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
        // 입력값 형식 검증
        if (!req.username().matches("^[a-z0-9]{5,15}$")) {
            throw new BusinessException(ErrorCode.INVALID_USERNAME);
        }
        if (!req.password().matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}$")) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        if (!req.email().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }
        
        // 중복 검증
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
        // 입력값 형식 검증 (null이 아닌 경우에만)
        if (req.getName() != null && !req.getName().matches("^[a-z0-9]{5,15}$")) {
            throw new BusinessException(ErrorCode.INVALID_USERNAME);
        }
        if (req.getEmail() != null && !req.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }
        
        Users user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // DTO를 통해 엔티티 업데이트 (@Transactional로 자동 저장)
        boolean hasChanges = user.updateUserInfo(req);
        
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
