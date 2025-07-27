package _05_completable_execption;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HandleDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int i = 1 / 0;
            return "result1";
        }).handle((result, ex) -> {
            CommonUtils.printThreadLog("上一步异常的恢复");
            if (ex != null) {
                CommonUtils.printThreadLog("出现异常：" + ex.getMessage());
                return "UnKnow";
            }
            return result;
        });
        String result = future.get();
        CommonUtils.printThreadLog(result);
    }

    /**
     * 异步任务不管是否发生异常，handle方法都会执行
     * 所以，handle方法核心作用在于对上一步异步任务进行现场修复
     */
}
