package kz.kaznu.acmkaznu.handlers.impl;

import kz.kaznu.acmkaznu.entity.Chat;
import kz.kaznu.acmkaznu.entity.Participant;
import kz.kaznu.acmkaznu.handlers.InfoUsers;
import kz.kaznu.acmkaznu.repository.ChatRepository;
import kz.kaznu.acmkaznu.request.CodeforcesRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class InfoUsersImpl implements InfoUsers {

    private final static String OK = "OK";
    private final ChatRepository chatRepository;
    private final CodeforcesRequests codeforcesRequests;
    public String getUserList(String chatId) {
        AtomicReference<String> answer = new AtomicReference<>("Список пользователей");
        AtomicInteger count = new AtomicInteger(1);
        Chat chat = chatRepository.findByChatId(chatId);
        Set<Participant> participants = chat.getParticipants();
        participants.forEach(participant -> answer.getAndSet(answer.get() + "\n" +
                count.getAndSet(count.get() + 1) + ". " + participant.getHandle()));
        return answer.get();
    }

    @Override
    public String getUserInfo(String handle) {
        String usersIsValid = codeforcesRequests.usersIsValid(handle), answer;
        if (usersIsValid.equals(OK)) {
            answer = codeforcesRequests.getUserInfo(handle).toString();
        } else {
            answer = usersIsValid;
        }
        return answer;
    }
}
