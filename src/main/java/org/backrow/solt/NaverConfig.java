package org.backrow.solt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/naver.properties")
@ConfigurationProperties(prefix="ncp")
@Getter
@Setter
@ToString
public class NaverConfig {
  private String endPoint; // ncp.endPoint 프로퍼티를 받는 필드
  private String regionName; // ncp.regionName 프로퍼티를 받는 필드
  private String accessKey; // ncp.accessKey 프로퍼티를 받는 필드
  private String secretKey; // ncp.secretKey 프로퍼티를 받는 필드
}
