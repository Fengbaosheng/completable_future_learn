package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenAcceptDemo {
    public static void main(String[] args) {
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，把内容转换成数组（敏感词数组）,然后打印敏感词数组
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("异步任务读取filter_word.txt文件");
            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
            return filterWordsContent;
        }).thenApply(content -> {
            CommonUtils.printThreadLog("异步任务把文件内容转换成敏感词数组");
            return content.split(",");
        }).thenAccept(filterWords -> {
            CommonUtils.printThreadLog("filterWords: " + Arrays.toString(filterWords));
        });

        CommonUtils.printThreadLog("主线程继续执行");
        CommonUtils.sleepSeconds(3);
        CommonUtils.printThreadLog("主线程结束");

        /**
         * 总结
         * thenAccept(Consumer<T> c)可以对异步任务结果消费使用
         * 返回一个不带结果的CompletableFuture对象
         */
    }
}
