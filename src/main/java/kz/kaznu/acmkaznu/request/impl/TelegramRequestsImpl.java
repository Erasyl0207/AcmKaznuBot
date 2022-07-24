package kz.kaznu.acmkaznu.request.impl;

import kz.kaznu.acmkaznu.request.TelegramRequests;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramRequestsImpl implements TelegramRequests {
    private final static String RESULT = "result";
    private final static String OK = "ok";
    private final static String ID = "id";
    private final static String USER = "user";
    @Value("${telegram.bot.token}")
    private String token;
    @Value("${telegram.api.chat.administrators}")
    private String getAdminsUrl;
    private final RestTemplate restTemplate;

    @SneakyThrows
    @Override
    public Optional<List<String>> getAdministrators(String chatId) {
        String url = String.format(getAdminsUrl, token, chatId);
        log.info("Chat id {} | url {}", chatId, url);
        CompletableFuture<String> completableFuture = CompletableFuture.completedFuture(restTemplate.
                exchange(url, HttpMethod.GET, null, String.class).getBody());
        JSONObject response = new JSONObject(completableFuture.get());
        if (!response.getBoolean(OK)) {
            return Optional.empty();
        }
        return Optional.of(parseJson(response.getJSONArray(RESULT)));
    }

    private List<String> parseJson(JSONArray result) {
        List<String> users = new ArrayList<>();
        for (Object o : result) {
            JSONObject user = ((JSONObject) o).getJSONObject(USER);
            users.add(String.valueOf(user.getLong(ID)));
        }
        return users;
    }
}
