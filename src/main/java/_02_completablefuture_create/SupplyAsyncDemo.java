package _02_completablefuture_create;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class SupplyAsyncDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CommonUtils.printThreadLog("主线程开始");
        CompletableFuture<String> newsFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("异步线程是否为守护线程：" + Thread.currentThread().isDaemon());
                String content = CommonUtils.readFile("news.txt");
                return content;
            }
        });

        CommonUtils.printThreadLog("这里非阻塞，主线程继续执行");
        // 阻塞并等待newsFuture完成
        String news = newsFuture.get();
        System.out.println("news: " + news);
        CommonUtils.printThreadLog("主线程结束");

        /**
         * 疑问：get方法阻塞的，会不会影响程序性能？
         * 回调函数
         */
    }
}
