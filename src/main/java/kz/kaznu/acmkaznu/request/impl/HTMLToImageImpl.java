package kz.kaznu.acmkaznu.request.impl;

import kz.kaznu.acmkaznu.request.HTMLToImage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HTMLToImageImpl implements HTMLToImage {

    private final static String URL = "url";
    @Value("${htmltoimage.userid}")
    private String userId;

    @Value("${htmltoimage.apikey}")
    private String APIKey;

    @Value("${htmltoimage.url}")
    private String HTMLtoImageUrl;
    private final RestTemplate restTemplate;

    @SneakyThrows
    @Override
    @Async
    public CompletableFuture<InputStream> generateLineChartImage(String HTML) {
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(userId, APIKey));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("html", HTML);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        CompletableFuture<String> completableFuture = CompletableFuture.completedFuture(restTemplate
                .exchange(HTMLtoImageUrl, HttpMethod.POST, request, String.class).getBody());
        String lineChartImageUrl = new JSONObject(completableFuture.get()).getString(URL);

        CompletableFuture<Resource> responseEntity = CompletableFuture
                .completedFuture(restTemplate.exchange(lineChartImageUrl, HttpMethod.GET, null, Resource.class).getBody());
        return CompletableFuture.completedFuture(Objects.requireNonNull(responseEntity.get()).getInputStream());
    }
}
