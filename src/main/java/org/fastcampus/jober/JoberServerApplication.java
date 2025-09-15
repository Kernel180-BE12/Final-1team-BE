package org.fastcampus.jober;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JoberServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(JoberServerApplication.class, args);
  }
}
