package org.backrow.solt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoltApplication {

    public static void main(String[] args) {

        System.out.println("Hello SD");
        SpringApplication.run(SoltApplication.class, args);
    }

}
