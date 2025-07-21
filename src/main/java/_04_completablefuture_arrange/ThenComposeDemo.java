package _04_completablefuture_arrange;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenComposeDemo {
    public static CompletableFuture<String> readFileFuture(String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            String fileWordContent = CommonUtils.readFile(fileName);
            return fileWordContent;
        });
    }

    public static CompletableFuture<String[]> splitFuture(String context) {
        return CompletableFuture.supplyAsync(() -> {
            String[] filterWords = context.split(",");
            return filterWords;
        });
    }

    public static void main(String[] args) throws Exception {
        // 编排2个依赖关系的任务 thenCompose()
        // 使用thenApply()
        /*CompletableFuture<CompletableFuture<String[]>> completableFutureCompletableFuture
                = readFileFuture("filter_word.txt").thenApply(content -> {
            return splitFuture(content);
        });*/

        CompletableFuture<String[]> completableFuture = readFileFuture("filter_word.txt").thenCompose(content -> {
            return splitFuture(content);
        });
        CommonUtils.printThreadLog(Arrays.toString(completableFuture.get()));
    }

    /**
     * thenApply(Function<T,R>)
     * 重心在于对上一步异步任务的结果T进行应用转换，经Function回调转换后的结果R是一个简单的值
     *
     * thenCompose(Function<T, CompletableFuture<R>)
     * 重心在于对上一步异步任务的结果T进行应用，经Function回调转换后的结果是一个CompletableFuture对象
     */


}
