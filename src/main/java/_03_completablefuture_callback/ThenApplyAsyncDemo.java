package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenApplyAsyncDemo {
    public static void main(String[] args) throws Exception{
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，转换为敏感词数组，主线程获取结果帮打印
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture<String[]> filterWordsFuture = CompletableFuture.supplyAsync(() -> {
//            CommonUtils.printThreadLog("读取filter_word.txt文件");
//            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
//            return filterWordsContent;

            // 此时立刻返回结果
            return "a,x,c,v";
        }).thenApply(filterWordsContent -> {
            /**
             * 一般而言,thenApply任务的执行和supplyAsync任务执行可以使用同一个线程执行
             * 如果suplyAsync任务立刻返回结果,则thenApply的任务在主线程中执行.
             */
            CommonUtils.printThreadLog("把文件内容转换成敏感词数组");
            String[] filterWords = filterWordsContent.split(",");
            return filterWords;
        });
        CommonUtils.printThreadLog("主线程继续执行");
        String[] filterWords = filterWordsFuture.get();
        CommonUtils.printThreadLog("filterWords: "+ Arrays.toString(filterWords));
        CommonUtils.printThreadLog("主线程结束");

        /**
         * 总结:
         * 一般而言,commonPool为了提高性能
         * thenApply中回调任务和supplyAsync中的异步任务使用的是同一个线程
         * 特殊情况:
         * 如果supplyAsync中的任务是立即返回结果(不是耗时的任务),那么thenApply回调任务也会在主线程执行
         */
    }
}
