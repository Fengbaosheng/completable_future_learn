package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;

public class ThenRunDemo {
    public static void main(String[] args) {
        // 需求，我们仅仅想知道敏感词文件是否读取完成。
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务开始读取filter_word.txt文件");
            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
            return filterWordsContent;
        }).thenRun(() -> {
            CommonUtils.printThreadLog("异步任务读取filter_word.txt文件完成");
        });

        CommonUtils.printThreadLog("主线程继续执行");
        CommonUtils.sleepSeconds(3);
        CommonUtils.printThreadLog("主线程结束");

        /**
         * thenRun(Runnable action)
         * 当异步任务完成后，我们只想得到一个完成的通知，不使用上一步异步任务的结果，就可以使用thenRun
         * 通常会把它使用在链式操作的末端
         */
    }
}
