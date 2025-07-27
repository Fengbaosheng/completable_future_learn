package _05_completable_execption;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;

public class ExecptionChainDemo {
    public static void main(String[] args) {
        // 异常如何在回调链中传播
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            return "result1";
        }).thenApply(result -> {
            return result + " result2";
        }).thenApply(result -> {
            return result + " result3";
        }).thenAccept(result -> {
            CommonUtils.printThreadLog(result);     // 控制台输出结果 result1 result2 result3
        });

        CompletableFuture<Void> future2 = CompletableFuture.supplyAsync(() -> {
            // int i = 1/0;    // 此行出现异常，后续的return，thenApply，thenAccept都不会调用执行，CompletableFuture转入异常处理，所以此方法什么也没有返回
            return "result1";
        }).thenApply(result -> {
            CommonUtils.printThreadLog(result);
            int i = 10 / 0;
            return result + " result2";
        }).thenApply(result -> {
            return result + " result3";
        }).thenAccept(result -> {
            CommonUtils.printThreadLog(result);
        });
    }

    /**
     * 如果回调链中出现任何异常，回调链不会再向下执行，立即转入异常处理
     */
}
