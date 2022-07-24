package kz.kaznu.acmkaznu.handlers;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.ExecutionException;

public interface UpdateMonitoringUsers {

    String updateUserToMonitoring(Message message, boolean operation);
}
