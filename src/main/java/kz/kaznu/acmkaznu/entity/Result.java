package kz.kaznu.acmkaznu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private Long count;
    private String sum;
    private String allProblems;
    private Date date;

    public String telegramMessageText() {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder text = new StringBuilder();
        text.append("Username: ").append(username).append("\n");
        text.append("День: ").append(format.format(date));
        text.append("Количество решенных задач: ").append(count).append("\n");
        text.append("Сумма решеных задач: ").append(sum).append("\n");
        JSONArray problems = new JSONArray(allProblems);
        int cnt = 800;
        for (Object o : problems) {
            JSONObject problem = (JSONObject) o;
            text.append(cnt).append(":").append(problem.getString(String.valueOf(cnt))).append("\n");
        }
        return text.toString();
    }
}
