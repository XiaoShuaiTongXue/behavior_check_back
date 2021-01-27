import com.behavior.pojo.Series;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class testSql {

    private static String behaviorFile = "D:\\Project\\springboot\\behavior_check\\file\\behavior";

    public static void main(String[] args) throws IOException {
        List<Integer> leaveCountList = new ArrayList<>();
        List<Integer> sleepCountList = new ArrayList<>();
        List<Integer> talkCountList = new ArrayList<>();
        List<Integer> outCountList = new ArrayList<>();
        String savePath = behaviorFile + File.separator + "803577947888812032.txt";
        File file = new File(savePath);
        Scanner scanner = new Scanner(file);
        Gson gson = new Gson();
        int[] lastData = new int[4];
        while (scanner.hasNext()) {
            int[] data = Arrays.stream(scanner.next().split(",")).mapToInt(Integer::parseInt).toArray();
            leaveCountList.add(data[0] - lastData[0]);
            sleepCountList.add(data[1] - lastData[1]);
            talkCountList.add(data[2] - lastData[2]);
            outCountList.add(data[3] - lastData[3]);
            lastData = data;
        }
        List<Series> seriesList = new ArrayList<>();
        Series series = new Series();
        series.setName("走神次数");
        series.setData(leaveCountList);
        seriesList.add(series);
        series.setName("瞌睡次数");
        series.setData(sleepCountList);
        seriesList.add(series);
        series.setName("说话次数");
        series.setData(talkCountList);
        seriesList.add(series);
        series.setName("旷课次数");
        series.setData(outCountList);
        seriesList.add(series);
        System.out.println(gson.toJson(seriesList));
    }
}
