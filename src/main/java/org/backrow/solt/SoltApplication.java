package org.backrow.solt;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableAspectJAutoProxy
@SpringBootApplication
public class SoltApplication {

    Dotenv dotenv = Dotenv.load();

    // 환경 변수를 가져와서 사용할 수 있습니다.
    String googleMapsApiKey = dotenv.get("GOOGLE_MAPS_API_KEY");
    String clovaApiKey = dotenv.get("CLOVA_API_KEY");

    public static void main(String[] args) {
        SpringApplication.run(SoltApplication.class, args);
    }
}