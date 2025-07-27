package _05_completable_execption;

import java.util.concurrent.CompletableFuture;

public class HandleDemo2 {
    public static void main(String[] args) {
        // 需求：对回调链中的一次异常进行恢复处理
        CompletableFuture.supplyAsync(() -> {
                    int i = 1 / 0;
                    return "result1";
                }).handle((result, ex) -> {
                    if (ex != null) {
                        System.out.println("出现异常：" + ex.getMessage());
                        return "UnKnow1";
                    }
                    return result;
                }).thenApply(result -> {
                    String str = null;
                    str.length();
                    return result + " result2";
                }).handle((result, ex) -> {
                    if (ex != null) {
                        System.out.println("出现异常" + ex.getMessage());
                        return " UnKnow2";
                    }
                    return result;
                })
                .thenApply(result -> {
                    return result + " result3";
                }).thenAccept(result -> {
                    System.out.println(result);
                });
    }
}
