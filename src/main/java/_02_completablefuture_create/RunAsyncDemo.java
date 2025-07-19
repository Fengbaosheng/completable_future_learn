package _02_completablefuture_create;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;

public class RunAsyncDemo {
    public static void main(String[] args) {
        // runAsync 创建异步任务
        CommonUtils.printThreadLog("主线程启动");
        // 使用Runnable匿名内部类
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                // 模拟读取文件
                CommonUtils.printThreadLog("读取文件开始");
                // 模拟读取文件耗时3秒钟
                CommonUtils.sleepSeconds(3);
                CommonUtils.printThreadLog("读取文件结束");
            }
        });

        CommonUtils.printThreadLog("这里非阻塞，主线程继续执行");
        CommonUtils.sleepSeconds(4); // 此处休眠是为了等待CompletableFuture背后的线程执行完成。
        CommonUtils.printThreadLog("主线程结束");

        /**
         * CompletableFuture中的异步任务底层通过开启线程的方式完成的
         */
    }
}
