package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class CommonUtils {
    // 读取指定路径文件
    public static String readFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    // 休眠指定毫秒数
    public static void sleepMillis(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 休眠指定秒数
    public static void sleepSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 打印线程日志信息
    public static void printThreadLog(String message) {
        String result = new StringJoiner(" | ")
                .add(String.valueOf(System.currentTimeMillis()))
                .add(String.format("%2d", Thread.currentThread().getId()))
                .add(String.valueOf(Thread.currentThread().getName()))
                .add(message)
                .toString();
        System.out.println(result);
    }
}
