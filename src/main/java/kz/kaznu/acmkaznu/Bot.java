package kz.kaznu.acmkaznu;

import kz.kaznu.acmkaznu.entity.Chat;
import kz.kaznu.acmkaznu.entity.Participant;
import kz.kaznu.acmkaznu.handlers.InfoUsers;
import kz.kaznu.acmkaznu.handlers.LineChart;
import kz.kaznu.acmkaznu.handlers.UpdateMonitoringUsers;
import kz.kaznu.acmkaznu.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.token}")
    private String token;
    private final UpdateMonitoringUsers updateMonitoringUsers;
    private final InfoUsers infoUsers;
    private final LineChart lineChart;
    private final ChatRepository chatRepository;


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String title = update.getMessage().getChat().getTitle();
            Chat chat = chatRepository.findByChatId(String.valueOf(chatId));
            if (chat == null) {
                chat = new Chat(null, title, String.valueOf(chatId), new HashSet<>());
                chatRepository.save(chat);
            }
            String fullMessageText = update.getMessage().getText();
            String receivedCommand = fullMessageText.split(" ")[0];
            String text = "";
            switch (receivedCommand) {
                case "/users@ACMKazNuBot", "/users" -> text = infoUsers.getUserList(String.valueOf(chatId));
                case "/info_all@ACMKazNuBot", "/info_all" -> {
                    if (chat.getParticipants().isEmpty()) {
                        text = "У вас нет пользователей для мониторинга!";
                    } else {
                        showInSliderAllUsers(chat);
                    }
                }
                case "/info@ACMKazNuBot", "/info" -> text = infoUsers.getUserInfo(fullMessageText.split(" ")[1]);
                case "/table@ACMKazNuBot", "/table" -> sendPhoto(chatId, lineChart.generateLineChartHTML(chat).get());
                case "/add_users@ACMKazNuBot", "/add_users" ->
                        text = updateMonitoringUsers.updateUserToMonitoring(update.getMessage(), true);
                case "/delete_users@ACMKazNuBot", "/delete_users" ->
                        text = updateMonitoringUsers.updateUserToMonitoring(update.getMessage(), false);
            }
            if (!text.equals(""))
                sendMessage(chatId, text);
        } else if (update.hasCallbackQuery()) {
            editSliderText(update);
        }
    }

    @SneakyThrows
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        log.info("Send message to {} | text {}", chatId, text);
        execute(message);
    }

    @SneakyThrows
    private void sendPhoto(Long chatId, InputStream photo) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        File file = new File("chartLine.png");
        FileUtils.copyInputStreamToFile(photo, file);
        InputFile inputFile = new InputFile(file);
        sendPhoto.setPhoto(inputFile);
        execute(sendPhoto);
    }

    @SneakyThrows
    private void showInSliderAllUsers(Chat chat) {
        SendMessage message = new SendMessage();
        List<Participant> participants = new ArrayList<>(chat.getParticipants());
        message.setText(infoUsers.getUserInfo(participants.get(0).getHandle()));
        message.setChatId(chat.getChatId());
        message.enableMarkdown(true);
        message.setReplyMarkup(getInlineSliderButtons(participants, 0));
        log.info(message.getText());
        execute(message);
    }

    @SneakyThrows
    private void editSliderText(Update update) {
        EditMessageText editMessageText = new EditMessageText();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        int index = Integer.parseInt(update.getCallbackQuery().getData());
        Chat chat = chatRepository.findByChatId(String.valueOf(chatId));
        List<Participant> participants = new ArrayList<>(chat.getParticipants());

        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(infoUsers.getUserInfo(participants.get(index).getHandle()));
        editMessageText.enableMarkdown(true);
        editMessageText.setReplyMarkup(getInlineSliderButtons(participants, index));
        execute(editMessageText);
    }

    private InlineKeyboardMarkup getInlineSliderButtons(List<Participant> participants, int index) {
        int previousIndex = index - 1, nextIndex = index + 1;
        InlineKeyboardButton previous = new InlineKeyboardButton();
        if (previousIndex < 0) {
            previousIndex = participants.size() - 1;
        }
        previous.setText(participants.get(previousIndex).getHandle());
        previous.setCallbackData(String.valueOf(previousIndex));

        InlineKeyboardButton next = new InlineKeyboardButton();
        if (nextIndex >= participants.size()) {
            nextIndex = 0;
        }
        next.setText(participants.get(nextIndex).getHandle());
        next.setCallbackData(String.valueOf(nextIndex));

        List<InlineKeyboardButton> row = new LinkedList<>();
        row.add(previous);
        row.add(next);

        List<List<InlineKeyboardButton>> rowCollection = new LinkedList<>();
        rowCollection.add(row);

        InlineKeyboardMarkup slider = new InlineKeyboardMarkup();
        slider.setKeyboard(rowCollection);
        return slider;
    }
}
