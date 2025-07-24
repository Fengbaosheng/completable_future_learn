package _04_completablefuture_arrange;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AllOfDemo {
    public static CompletableFuture<String> readFileFuture(String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            return CommonUtils.readFile(fileName);
        });
    }

    public static void main(String[] args) {
        // 需求：统计news1.txt，news2.txt，news3.txt文件中包含CompletableFuture关键字的文件的个数
        //step1 创建List集合，存储文件名
        List<String> fileList = Arrays.asList("news1.txt", "news2.txt", "news3.txt");

        // step2 根据文件名调用readFileFuture创建多个CompletableFuture，并存入List集合中
        List<CompletableFuture<String>> readFileFutureList = fileList.stream().map(fileName -> {
            return readFileFuture(fileName);
        }).collect(Collectors.toList());

        // step3 把List集合转成数组待用以便传入allOf方法
        CompletableFuture[] readFileFutureArray = readFileFutureList.toArray(new CompletableFuture[readFileFutureList.size()]);

        // step4 使用allOf方法合并多个异步任务
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(readFileFutureArray);

        // step5 当多个异步任务都完成后，使用回调操作文件结果，统计符合条件的文件个数
        CompletableFuture<Long> countFuture = allOfFuture.thenApply(v -> {
            long count = readFileFutureList.stream().map(future -> future.join())
                    .filter(content -> content.contains("CompletableFuture"))
                    .count();
            return count;
        });
        // step 主线程打印输出文件个数
        System.out.println(countFuture.join());
    }

    /**
     * allOf特别适合合并多个异步任务，当所有异步任务都完成时可以进一步操作
     */
}
