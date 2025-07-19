package _02_completablefuture_create;

import utils.CommonUtils;

import java.util.concurrent.*;

public class SupplyAsyncDemo03 {
    private static ExecutorService executor = new ThreadPoolExecutor(
            3,
            5,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10)) {
    };

    public static void main(String[] args) {
        try {
            CommonUtils.printThreadLog("主线程开始");
            // 使用Lambda表达式
            CompletableFuture<String> newsFuture = CompletableFuture.supplyAsync(() -> {
                CommonUtils.printThreadLog("开始读取文件");
                String content = CommonUtils.readFile("news.txt");
                CommonUtils.printThreadLog("读取文件结束");
                System.out.println("异步线程是否为守护线程：" + Thread.currentThread().isDaemon());
                return content;
            }, executor);

            CommonUtils.printThreadLog("这里非阻塞，主线程继续执行");
            // 阻塞并等待newsFuture完成
            String news = newsFuture.get();
            System.out.println("news: " + news);
            CommonUtils.printThreadLog("主线程结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
