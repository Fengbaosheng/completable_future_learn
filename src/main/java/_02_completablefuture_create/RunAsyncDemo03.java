package _02_completablefuture_create;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;

public class RunAsyncDemo03 {
    public static void main(String[] args) {
        // 使用CompletableFuture开启异步任务读取news.txt文件中的新闻稿并打印输出
        // runAsync 创建异步任务
        CommonUtils.printThreadLog("主线程启动");
        // 使用Lambda表达式
        CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().isDaemon());
            CommonUtils.printThreadLog("读取文件");
            System.out.println(CommonUtils.readFile("news.txt"));
        });

        CommonUtils.printThreadLog("这里非阻塞，主线程继续执行");
        CommonUtils.sleepSeconds(4); // 此处休眠是为了等待CompletableFuture背后的线程执行完成。
        CommonUtils.printThreadLog("主线程结束");
    }
}
