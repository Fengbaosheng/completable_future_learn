package _04_completablefuture_arrange;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;

public class AnyOfDemo {
    public static void main(String[] args) {
        // anyOf()
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            CommonUtils.sleepSeconds(2);
            return "Future1的结果";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            CommonUtils.sleepSeconds(1);
            return "Future2的结果";
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            CommonUtils.sleepSeconds(3);
            return "Future3的结果";
        });

        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(future1, future2, future3);
        System.out.println(anyOfFuture.join());

    }
}
