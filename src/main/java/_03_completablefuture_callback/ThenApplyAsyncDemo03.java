package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.*;

public class ThenApplyAsyncDemo03 {
    private static ExecutorService executor = new ThreadPoolExecutor(
            3,
            5,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10)) {
    };

    public static void main(String[] args) {
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，转换为敏感词数组，主线程获取结果帮打印
        CommonUtils.printThreadLog("主线程开启");
        try {
            CompletableFuture<String[]> filterWordsFuture = CompletableFuture.supplyAsync(() -> {
                CommonUtils.printThreadLog("读取filter_word.txt文件");
                String filterWordsContent = CommonUtils.readFile("filter_word.txt");
                return filterWordsContent;
            }, executor).thenApplyAsync(filterWordsContent -> {
                /**
                 * 一般而言,thenApply任务的执行和supplyAsync任务执行可以使用同一个线程执行
                 * 如果suplyAsync任务立刻返回结果,则thenApply的任务在主线程中执行.
                 */
                CommonUtils.printThreadLog("把文件内容转换成敏感词数组");
                String[] filterWords = filterWordsContent.split(",");
                return filterWords;
            }, executor);
            CommonUtils.printThreadLog("主线程继续执行");
            String[] filterWords = filterWordsFuture.get();
            CommonUtils.printThreadLog("filterWords: " + Arrays.toString(filterWords));
            CommonUtils.printThreadLog("主线程结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
