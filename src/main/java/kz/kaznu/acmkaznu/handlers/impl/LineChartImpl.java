package kz.kaznu.acmkaznu.handlers.impl;

import kz.kaznu.acmkaznu.entity.Chat;
import kz.kaznu.acmkaznu.entity.Participant;
import kz.kaznu.acmkaznu.handlers.LineChart;
import kz.kaznu.acmkaznu.request.CodeforcesRequests;
import kz.kaznu.acmkaznu.request.HTMLToImage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class LineChartImpl implements LineChart {

    private final static String START_LINE_CHART = """
              <html>
              <head>
                <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
                <script type="text/javascript">
                  google.charts.load('current', {'packages':['corechart']});
                  google.charts.setOnLoadCallback(drawChart);
                        
                  function drawChart() {
                    var data = google.visualization.arrayToDataTable([
            """;
    private final static String END_LINE_CHART = """
                    ]);  
                    var options = {
                      title: 'Codeforces line chart',
                      curveType: 'function',
                      legend: { position: 'bottom' }
                    };
                    var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));     
                    chart.draw(data, options);
                  }
                </script>
              </head>
              <body>
                <div id="curve_chart" style="width: 1920px; height: 1080px"></div>
              </body>
            </html>
            """;

    private final CodeforcesRequests codeforcesRequests;

    private final HTMLToImage htmlToImage;

    @SneakyThrows
    @Override
    @Async
    public CompletableFuture<InputStream> generateLineChartHTML(Chat chat) {
        LocalDate today = LocalDate.now();
        Set<Participant> participants = chat.getParticipants();
        List<String> table = new ArrayList<>();
        for (int i = 31; i >= 0; i--) {
            if (i == 31) {
                table.add("['Date'");
                continue;
            }
            table.add("['" + today.minusDays(i) + "'");
        }
        for (Participant participant : participants) {
            HashMap<String, Integer> participantSubmissions = codeforcesRequests.getUserSubmissions(participant.getHandle());
            table.set(0, table.get(0) + ",'" + participant.getHandle() + "'");
            for (int i = 1; i <= 31; i++) {
                int countAcceptedSubmissions = participantSubmissions.
                        getOrDefault(String.valueOf(today.minusDays(31 - i)), 0);
                table.set(i, table.get(i) + "," + countAcceptedSubmissions);
            }
        }
        StringBuilder fullHTMLLineChart = new StringBuilder(START_LINE_CHART);
        for (int i = 0; i < table.size(); i++) {
            if (i + 1 == table.size()) {
                table.set(i, table.get(i) + "]");
            } else {
                table.set(i, table.get(i) + "],");
            }
            fullHTMLLineChart.append(table.get(i));
        }
        fullHTMLLineChart.append(END_LINE_CHART);
        return CompletableFuture.completedFuture(htmlToImage.generateLineChartImage(fullHTMLLineChart.toString()).get());
    }
}
