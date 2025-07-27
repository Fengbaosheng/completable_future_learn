package _05_completable_execption;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ExecptionallyDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            //int i = 1 / 0;
            return "result1";
        }).thenApply(result -> {
            String str = null;
            str.length();
            return result + " result2";
        }).thenApply(result -> {
            return result + " result3";
        }).exceptionally(ex -> {
            System.out.println("出现异常：" + ex.getMessage());
            return ex.getMessage();
        });
        System.out.println(future.get());
    }
}
