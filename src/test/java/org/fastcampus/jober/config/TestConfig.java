package org.fastcampus.jober.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
@Profile("test")
public class TestConfig {
    
    /**
     * 테스트용 패스워드 인코더 - 빠른 테스트를 위해 강도를 낮춤
     */
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4); // 기본값 10보다 낮은 강도로 테스트 속도 향상
    }
    
    /**
     * 테스트용 설정들을 여기에 추가할 수 있습니다.
     * 예: Mock 서비스, 테스트용 빈 등
     */
}