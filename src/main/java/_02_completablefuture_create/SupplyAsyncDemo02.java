package _02_completablefuture_create;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SupplyAsyncDemo02 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CommonUtils.printThreadLog("主线程开始");
        // 使用Lambda表达式
        CompletableFuture<String> newsFuture = CompletableFuture.supplyAsync(() -> {
            return CommonUtils.readFile("news.txt");
        });

        CommonUtils.printThreadLog("这里非阻塞，主线程继续执行");
        // 阻塞并等待newsFuture完成
        String news = newsFuture.get();
        System.out.println("news: " + news);
        CommonUtils.printThreadLog("主线程结束");
    }
}
