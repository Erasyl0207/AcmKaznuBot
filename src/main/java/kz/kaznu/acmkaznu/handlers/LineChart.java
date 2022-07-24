package kz.kaznu.acmkaznu.handlers;

import kz.kaznu.acmkaznu.entity.Chat;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;


public interface LineChart {
    CompletableFuture<InputStream> generateLineChartHTML(Chat chat);
}
