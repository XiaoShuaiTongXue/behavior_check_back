import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class testSql {

    private static String behaviorFile = "D:\\Project\\springboot\\behavior_check\\file";
    public static void main(String[] args) throws IOException {
        String savePath = behaviorFile + File.separator + "11.txt";
        System.out.println(savePath);
        File file = new File(savePath);
        if (!file.exists()) {
            file.createNewFile();
            System.out.println("当前文件已经存在");
            return;
        }
        FileOutputStream fos = new FileOutputStream(file,true);
        for (int i = 0; i < 10; i++) {
            fos.write((i+"\n").getBytes(StandardCharsets.UTF_8));
        }
        System.out.println("添加数据");
        fos.close();
    }
}
