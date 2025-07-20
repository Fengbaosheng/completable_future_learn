package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenApplyDemo {
    public static void main(String[] args) throws Exception {
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，把内容转换成数组（敏感词数组），异步任务返回敏感词数组。
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture<String> readFileFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("异步任务读取filter_word.txt文件");
            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
            return filterWordsContent;
        });

        CompletableFuture<String[]> filterWordsFuture = readFileFuture.thenApply(content -> {
            CommonUtils.printThreadLog("异步任务将文件内容转换为敏感词数组");
            String[] filterWords = content.split(",");
            return filterWords;
        });

        CommonUtils.printThreadLog("主线程继续执行");
        String[] filterWords = filterWordsFuture.get();
        CommonUtils.printThreadLog("filterWords: " + Arrays.toString(filterWords));
        CommonUtils.printThreadLog("主线程结束");

        /**
         * 总结
         * thenApply(Function<T,R>)可以对异步任务的结果进一步应用Function转换
         * 转换后的结果可以在主线程获取，也可以下一步的转换。
         */
    }
}
