package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenApplyDemo02 {
    public static void main(String[] args) throws Exception{
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，把内容转换成数组（敏感词数组），异步任务返回敏感词数组。
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture<String[]> filterWordsFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("异步任务读取filter_word.txt文件");
            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
            return filterWordsContent;
        }).thenApply(content -> {
            CommonUtils.printThreadLog("异步任务将文件内容转换为敏感词数组");
            String[] filterWords = content.split(",");
            return filterWords;
        });

        CommonUtils.printThreadLog("主线程继续执行");
        String[] filterWords = filterWordsFuture.get();
        CommonUtils.printThreadLog("filterWords: " + Arrays.toString(filterWords));
        CommonUtils.printThreadLog("主线程结束");
    }
}
