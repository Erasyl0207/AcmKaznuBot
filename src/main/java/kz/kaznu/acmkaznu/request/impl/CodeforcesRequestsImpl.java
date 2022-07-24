package kz.kaznu.acmkaznu.request.impl;

import kz.kaznu.acmkaznu.model.UserCodeforces;
import kz.kaznu.acmkaznu.request.CodeforcesRequests;
import lombok.RequiredArgsConstructor;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class CodeforcesRequestsImpl implements CodeforcesRequests {
    private final static String STATUS = "status";
    private final static String COMMENT = "comment";
    private final static String OK = "OK";
    private final static String LAST_ONLINE_TIME_SECONDS = "lastOnlineTimeSeconds";
    private final static String RATING = "rating";
    private final static String HANDLE = "handle";
    private final static String RANK = "rank";
    private final static String MAX_RATING = "maxRating";
    private final static String MAX_RANK = "maxRank";
    private final static String CREATION_TIME_SECONDS = "creationTimeSeconds";
    private final static String VERDICT = "verdict";
    private final static String ERROR_REQUEST = "Вызовите команду правильно!";

    @Value("${codeforces.user.info.url}")
    private String urlUserInfo;

    @Value("${codeforces.user.submissions.url}")
    private String urlUserSubmissions;

    private final RestTemplate restTemplate;

    @SneakyThrows
    @Override
    public String usersIsValid(String users) {
        log.info("Users {}", users);
        if (users.isEmpty()) {
            return ERROR_REQUEST;
        }
        String url = String.format(urlUserInfo, users);
        try {
            CompletableFuture<String> completableFuture = CompletableFuture.completedFuture(restTemplate
                    .exchange(url, HttpMethod.GET, null, String.class).getBody());
            JSONObject response = new JSONObject(completableFuture.get());
            if (response.getString(STATUS).equals(OK)) {
                return OK;
            }
        } catch (HttpClientErrorException e) {
            JSONObject response = new JSONObject(Objects.requireNonNull(e.getMessage()).substring(7));
            return String.format("Ошибка запроса \nКомментарий %s", response.getString(COMMENT));
        }
        return ERROR_REQUEST;
    }

    @SneakyThrows
    public UserCodeforces getUserInfo(String handle) {
        String url = String.format(urlUserInfo, handle);
        CompletableFuture<String> completableFuture = CompletableFuture.completedFuture(restTemplate
                .exchange(url, HttpMethod.GET, null, String.class).getBody());
        JSONObject result = (JSONObject) new JSONObject(completableFuture.get()).getJSONArray("result").get(0);
        log.info(completableFuture.get());
        UserCodeforces userCodeforces = new UserCodeforces();
        userCodeforces.setHandle(result.getString(HANDLE));
        userCodeforces.setMaxRank(result.getString(MAX_RANK));
        userCodeforces.setRank(result.getString(RANK));
        userCodeforces.setMaxRating(result.getInt(MAX_RATING));
        userCodeforces.setRating(result.getInt(RATING));
        userCodeforces.setLastOnlineTimeSeconds(result.getInt(LAST_ONLINE_TIME_SECONDS));
        return userCodeforces;
    }

    @SneakyThrows
    @Override
    public HashMap<String, Integer> getUserSubmissions(String handle) {
        LocalDate lastMonth = LocalDate.now().minusDays(30);
        HashMap<String, Integer> userSubmissionsInDay = new HashMap<>();
        String url = String.format(urlUserSubmissions, handle);
        CompletableFuture<String> completableFuture = CompletableFuture.completedFuture(restTemplate
                .exchange(url, HttpMethod.GET, null, String.class).getBody());
        JSONArray result = new JSONObject(completableFuture.get()).getJSONArray("result");

        for (Object o : result) {
            JSONObject submission = (JSONObject) o;
            LocalDate submissionCreatedDate = Instant.ofEpochSecond(submission.getInt(CREATION_TIME_SECONDS))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (submissionCreatedDate.isBefore(lastMonth)) {
                break;
            }
            if (submission.getString(VERDICT).equals(OK)) {
                int countAcceptedSubmissions = userSubmissionsInDay.getOrDefault(String.valueOf(submissionCreatedDate), 0);
                userSubmissionsInDay.put(String.valueOf(submissionCreatedDate), countAcceptedSubmissions + 1);
            }
        }
        return userSubmissionsInDay;
    }
}
