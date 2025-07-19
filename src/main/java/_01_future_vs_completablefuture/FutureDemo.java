package _01_future_vs_completablefuture;

import utils.CommonUtils;


import java.util.concurrent.*;

public class FutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        // step 1: 读取敏感词汇 ==> thread1
        Future<String[]> filterWordFuture = executor.submit(() -> {
            String str = CommonUtils.readFile("filter_word.txt");
            String[] filterWords = str.split(",");
            return filterWords;
        });

        // step 2: 读取新闻稿 ==> thread2
        Future<String> newsFuture = executor.submit(() -> {
            return CommonUtils.readFile("news.txt");
        });

        // step 3: 替换操作 ==> thread3
        Future<String> replaceFuture = executor.submit(() -> {
            String[] filterWords = filterWordFuture.get();
            String news = newsFuture.get();
            for (String filterWord : filterWords) {
                if (news.indexOf(filterWord) >= 0) {
                    news = news.replace(filterWord, "***");
                }
            }
            // CommonUtils.sleepSeconds(5);
            return news;
        });
        // step 4: 打印替换后的新闻稿 ==> main
        System.out.println("过滤敏感词后的新闻:" + replaceFuture.get());

    }
}
