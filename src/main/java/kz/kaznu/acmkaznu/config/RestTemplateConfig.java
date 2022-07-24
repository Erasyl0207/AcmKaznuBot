package kz.kaznu.acmkaznu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {
    private final static long CONNECTION_TIME_OUT = 60000 * 3 * 3;
    private final static long READ_TIME_OUT = 60000 * 3 * 3;
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(CONNECTION_TIME_OUT))
                .setReadTimeout(Duration.ofMillis(READ_TIME_OUT))
                .build();
    }
}
