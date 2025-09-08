package org.fastcampus.jober.config;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles
@Transactional
@TestPropertySource
public @interface IntegrationTestWithProperties {
    
    /**
     * 테스트에서 사용할 추가 프로퍼티들
     */
    @AliasFor(annotation = TestPropertySource.class, attribute = "properties")
    String[] properties() default {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver", 
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "logging.level.org.springframework.web=DEBUG"
    };
    
    /**
     * 활성화할 프로파일
     */
    @AliasFor(annotation = ActiveProfiles.class, attribute = "profiles")
    String[] profiles() default {"test"};
    
    /**
     * 웹 환경 설정
     */
    @AliasFor(annotation = SpringBootTest.class, attribute = "webEnvironment")
    SpringBootTest.WebEnvironment webEnvironment() default SpringBootTest.WebEnvironment.RANDOM_PORT;
}