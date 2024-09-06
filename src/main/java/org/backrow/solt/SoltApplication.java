package org.backrow.solt;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class SoltApplication {

    public static void main(String[] args) {

        System.out.println("Hello SD");
        SpringApplication.run(SoltApplication.class, args);
        log.info("TEST1 @@@@@eateteaetewat@@@@@@======eafefae======@");
    }

}
