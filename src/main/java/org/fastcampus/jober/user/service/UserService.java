package org.fastcampus.jober.user.service;

import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.user.dto.request.RegisterRequestDto;
import org.fastcampus.jober.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long getUserId(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow()
                .getId();
    }

    public void register(RegisterRequestDto req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        userRepository.save(req.toEntity());
    }
}
