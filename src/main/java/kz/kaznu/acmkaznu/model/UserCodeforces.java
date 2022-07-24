package kz.kaznu.acmkaznu.model;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class UserCodeforces {
    private String handle;
    private long lastOnlineTimeSeconds;
    private int rating;
    private String rank;
    private int maxRating;
    private String maxRank;

    @Override
    public String toString() {
        return "Хэндл: " + handle + '\n' +
                "Был онлайн: " + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .format(new Date(lastOnlineTimeSeconds * 1000)) + '\n' +
                "Рейтинг: " + rating + '\n' +
                "Ранк: " + rank + '\n' +
                "Макс. рейтинг: " + maxRating + '\n' +
                "Макс. ранк: " + maxRank;
    }
}
