package kz.kaznu.acmkaznu.request;

import kz.kaznu.acmkaznu.model.UserCodeforces;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface TelegramRequests {

    Optional<List<String>> getAdministrators(String chatId);

}
