package org.fastcampus.jober;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.profiles.active=test"})
@AutoConfigureWebMvc
@Transactional
public @interface IntegrationTest {

}
