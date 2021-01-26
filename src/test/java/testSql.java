import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;

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
            leaveCountList.add(data[0]-lastData[0]);
            sleepCountList.add(data[1]-lastData[1]);
            talkCountList.add(data[2]-lastData[2]);
            outCountList.add(data[3]-lastData[3]);
            lastData = data;
        }
        Map<String,Object> countMap = new HashMap<>();
        countMap.put("走神次数",leaveCountList);
        countMap.put("瞌睡次数",sleepCountList);
        countMap.put("说话次数",talkCountList);
        countMap.put("旷课次数",outCountList);
        System.out.println(gson.toJson(countMap));
    }
}
