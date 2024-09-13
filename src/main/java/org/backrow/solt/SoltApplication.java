package org.backrow.solt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SoltApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoltApplication.class, args);
    }
}