package org.fastcampus.jober.config;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true",
    "logging.level.org.springframework.web=DEBUG"
})
public @interface IntegrationTest {
    
    /**
     * 추가 테스트 프로퍼티를 지정할 수 있습니다.
     * 예: @IntegrationTest(properties = {"custom.property=value"})
     */
    String[] properties() default {};
    
    /**
     * 활성화할 프로파일을 지정할 수 있습니다.
     * 기본값은 "test"이지만 추가로 지정 가능합니다.
     */
    String[] profiles() default {};
}