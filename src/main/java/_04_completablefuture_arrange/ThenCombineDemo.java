package _04_completablefuture_arrange;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;

public class ThenCombineDemo {
    public static void main(String[] args) throws Exception {
        CommonUtils.printThreadLog("主线程开始");
        // 读取敏感词汇的文件并解析到数组中
        CompletableFuture<String[]> filterWordsFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("读取filter_word.txt文件");
            String filterWordContent = CommonUtils.readFile("filter_word.txt");
            return filterWordContent;
        }).thenCompose(content -> CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("提取敏感词数组");
            String[] filterWords = content.split(",");
            return filterWords;
        }));

        // 读取news文件内容
        CompletableFuture<String> newsFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("读取news.txt文件");
            return CommonUtils.readFile("news.txt");
        });

        // 替换
        CompletableFuture<String> combineFuture = filterWordsFuture.thenCombine(newsFuture, (words, content) -> {
            CommonUtils.printThreadLog("转换操作");
            for (String word : words) {
                if (content.indexOf(word) > -1) {
                    content = content.replace(word, "***");
                }
            }
            return content;
        });
        CommonUtils.printThreadLog("主线程非阻塞，继续执行");
        CommonUtils.printThreadLog(combineFuture.get());
        CommonUtils.printThreadLog("主线程结束");
    }
}
