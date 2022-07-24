package kz.kaznu.acmkaznu.request;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface HTMLToImage {
    CompletableFuture<InputStream> generateLineChartImage(String HTML);

}
