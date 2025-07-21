package _04_completablefuture_arrange;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenComposeDemo02 {
    public static void main(String[] args) throws Exception {
        // 需求，异步读取filter_word.txt文件的内容，读取完成后，转换为敏感词数组待用
        CommonUtils.printThreadLog("主线程开始");

        CompletableFuture<String[]> completableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("读取文件");
            String content = CommonUtils.readFile("filter_word.txt");
            return content;
        }).thenCompose(content -> CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("分割数组");
            return content.split(",");
        }));

        CommonUtils.printThreadLog("主线程非阻塞，继续执行");
        CommonUtils.printThreadLog(Arrays.toString(completableFuture.get()));
        CommonUtils.printThreadLog("主线程结束");
    }
}
