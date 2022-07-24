package kz.kaznu.acmkaznu.handlers.impl;

import kz.kaznu.acmkaznu.entity.Chat;
import kz.kaznu.acmkaznu.entity.Participant;
import kz.kaznu.acmkaznu.handlers.UpdateMonitoringUsers;
import kz.kaznu.acmkaznu.repository.ChatRepository;
import kz.kaznu.acmkaznu.request.CodeforcesRequests;
import kz.kaznu.acmkaznu.request.TelegramRequests;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateMonitoringUsersImpl implements UpdateMonitoringUsers {
    private final static String OK = "OK";
    private final ChatRepository chatRepository;

    private final TelegramRequests telegramRequests;

    private final CodeforcesRequests codeforcesRequests;

    @SneakyThrows
    @Override
    public String updateUserToMonitoring(Message message, boolean operation){
        String messageText = message.getText(), answer;
        String newParticipants = Arrays.stream(messageText.split(" "))
                .skip(1).collect(Collectors.joining(";"));
        String usersIsValid = codeforcesRequests.usersIsValid(newParticipants);
        if (usersIsValid.equals(OK)) {
            Chat chat = chatRepository.findByChatId(String.valueOf(message.getChatId()));
            if (message.getChat().isUserChat()) {
                answer = updateUsersInChat(newParticipants, chat, operation);
            } else {
                answer = updateGroup(message, newParticipants, chat, operation);
            }
        } else {
            answer = usersIsValid;
        }
        return answer;
    }

    private String updateGroup(Message message, String newParticipants, Chat chat, boolean operation) throws ExecutionException, InterruptedException {
        String senderId = String.valueOf(message.getFrom().getId());
        Optional<List<String>> admins = telegramRequests.getAdministrators(chat.getChatId());
        String answer = "Эту функцию могут вызывать только админы группы!";
        if (admins.isPresent()) {
            boolean isAdmin = admins.get().stream().anyMatch(senderId::equals);
            if (isAdmin) {
                answer = updateUsersInChat(newParticipants, chat, operation);
            }
        }
        return answer;
    }

    private String updateUsersInChat(String newParticipants, Chat chat, boolean operation) {
        String answer;
        Set<Participant> participants = chat.getParticipants();
        if (operation) {
            Arrays.stream(newParticipants.split(";")).
                    forEach(p -> participants.add(new Participant(null, p)));
            chat.setParticipants(participants);
            chatRepository.save(chat);
            answer = "Операция добавления пользователя-(ей) прошла успешна!";
        } else {
            Arrays.stream(newParticipants.split(";")).
                    forEach(p -> participants.remove(new Participant(null, p)));
            chat.setParticipants(participants);
            chatRepository.save(chat);
            answer = "Операция удаления пользователя-(ей) прошла успешна!";
        }
        return answer;
    }
}
