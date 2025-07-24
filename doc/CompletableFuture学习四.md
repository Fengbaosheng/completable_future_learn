## 异步任务回调
CompletableFuture.get()方法是阻塞的。调用时它会阻塞等待直到这个Future完成，并在完成后返回结果。但是，很多时候这不是我们想要的。

对于构建异步系统，我们应该能够将回调附加到CompletableFuture上，当这个Future完成时，该回调因自动被调用。这样，我们就不必等待接过来，然后在Future的回调函数内编写完成Future之后需要执行的逻辑。你可以使用thenApply()、thenAccetp()和thenRun()方法，它们可以把回调函数附加到CompletableFuture。

#### thenApply
使用thenApply()方法可以处理和转换CompletableFuture的结果。它以Function<T,R>作为参数。Function<T,R>是一个函数式接口，表示一个转换操作，接受一个T的参数并返回一个R的结果。

```java
CompletableFuture<R> thenApply(Function<T,R> fn)
```

	需求：异步读取filter_word.txt文件中的内容，读取完成后，把内容转换成数组（敏感词数组），异步任务返回敏感词数组。

```java
package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenApplyDemo {
    public static void main(String[] args) throws Exception {
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，把内容转换成数组（敏感词数组），异步任务返回敏感词数组。
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture<String> readFileFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("异步任务读取filter_word.txt文件");
            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
            return filterWordsContent;
        });

        CompletableFuture<String[]> filterWordsFuture = readFileFuture.thenApply(content -> {
            CommonUtils.printThreadLog("异步任务将文件内容转换为敏感词数组");
            String[] filterWords = content.split(",");
            return filterWords;
        });

        CommonUtils.printThreadLog("主线程继续执行");
        String[] filterWords = filterWordsFuture.get();
        CommonUtils.printThreadLog("filterWords: " + Arrays.toString(filterWords));
        CommonUtils.printThreadLog("主线程结束");

        /**
         * 总结
         * thenApply(Function<T,R>)可以对异步任务的结果进一步应用Function转换
         * 转换后的结果可以在主线程获取，也可以下一步的转换。
         */
    }
}

```

此外，还可以通过附加一系列的thenApply()回调方法，在CompletableFuture上编写一系列转换序列。一个thenApply()方法的结果可以传递给序列的下一个，如果你对链式编程很了解，你会发现结果可以在链式操作上传递。

```java
package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenApplyDemo02 {
    public static void main(String[] args) throws Exception{
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，把内容转换成数组（敏感词数组），异步任务返回敏感词数组。
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture<String[]> filterWordsFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("异步任务读取filter_word.txt文件");
            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
            return filterWordsContent;
        }).thenApply(content -> {
            CommonUtils.printThreadLog("异步任务将文件内容转换为敏感词数组");
            String[] filterWords = content.split(",");
            return filterWords;
        });

        CommonUtils.printThreadLog("主线程继续执行");
        String[] filterWords = filterWordsFuture.get();
        CommonUtils.printThreadLog("filterWords: " + Arrays.toString(filterWords));
        CommonUtils.printThreadLog("主线程结束");
    }
}

```

#### thenAccept
如果你不想从回调函数返回结果，而只想在Future完成后运行一些代码，则可以使用thenAccept()。这个方法是入参一个Comsumer<T>，它可以对异步任务的执行结果进行消费使用，方法返回CompletableFuture<Void>。

```java
CompletableFuture<Void> thenAccept(Consumer<T> action)
```

	通常用作回调链中的最后一个回调。

需求：异步读取filter_word.txt文件中的内容，读取完成后，转换成敏感词数组，然后打印敏感词数组。

```java
package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenAcceptDemo {
    public static void main(String[] args) {
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，把内容转换成数组（敏感词数组）,然后打印敏感词数组
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("异步任务读取filter_word.txt文件");
            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
            return filterWordsContent;
        }).thenApply(content -> {
            return content.split(",");
        }).thenAccept(filterWords -> {
            CommonUtils.printThreadLog("filterWords: " + Arrays.toString(filterWords));
        });

        CommonUtils.printThreadLog("主线程继续执行");
        CommonUtils.sleepSeconds(3);
        CommonUtils.printThreadLog("主线程结束");
    }
}

```

#### thenRun
前面我们已经知道，通过thenApply(Function<T,R>)对链式操作中的上一个异步任务的结果进行转换，返回一个新的结果；通过thenAccept<Consumer<T>>对链式操作中上一个异步任务的结果进行消费使用，不返回新结果。

如果我们只想从CompletableFuture的链式操作得到一个完成的通知，甚至都不使用上一步链式操作的结果，那么CompletableFuture.thenRun()会是你最佳的选择，它需要一个Runnable并返回CompletableFuture<Void>.

```java
ConpletableFuture<Void> thenRun(Runable action);
```

需求：我们仅仅想知道filter_word.txt的文件是否读取完成

```java
package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.concurrent.CompletableFuture;

public class ThenRunDemo {
    public static void main(String[] args) {
        // 需求，我们仅仅想知道敏感词文件是否读取完成。
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务开始读取filter_word.txt文件");
            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
            return filterWordsContent;
        }).thenRun(() -> {
            CommonUtils.printThreadLog("异步任务读取filter_word.txt文件完成");
        });

        CommonUtils.printThreadLog("主线程继续执行");
        CommonUtils.sleepSeconds(3);
        CommonUtils.printThreadLog("主线程结束");

        /**
         * thenRun(Runnable action)
         * 当异步任务完成后，我们只想得到一个完成的通知，不使用上一步异步任务的结果，就可以使用thenRun
         * 通常会把它使用在链式操作的末端
         */
    }
}

```

#### 更进一步提升并行化
CompletableFuture提供的所有回调方法都有两个异步变体

```java
CompletableFuture<U> thenApply(Function<T,U> fn)
// 回调方法的异步变体（异步回调）
CompletableFuture<U> thenApplyAsync(Function<T,U> fn)
CompletableFuture<U> thenApplyAsync(Function<T,U> fn, Executor executor)
```

注意：这些带了Async的异步回调通过在单独的线程中执行回调任务来帮助我们进一步促进并行化计算。

需求：异步读取filter_word.txt文件中的内容，读取完成后，转换为敏感词数组，主线程获取结果帮打印

```java
package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenApplyAsyncDemo {
    public static void main(String[] args) throws Exception{
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，转换为敏感词数组，主线程获取结果帮打印
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture<String[]> filterWordsFuture = CompletableFuture.supplyAsync(() -> {
//            CommonUtils.printThreadLog("读取filter_word.txt文件");
//            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
//            return filterWordsContent;

            // 此时立刻返回结果
            return "a,x,c,v";
        }).thenApply(filterWordsContent -> {
            /**
             * 一般而言,thenApply任务的执行和supplyAsync任务执行可以使用同一个线程执行
             * 如果suplyAsync任务立刻返回结果,则thenApply的任务在主线程中执行.
             */
            CommonUtils.printThreadLog("把文件内容转换成敏感词数组");
            String[] filterWords = filterWordsContent.split(",");
            return filterWords;
        });
        CommonUtils.printThreadLog("主线程继续执行");
        String[] filterWords = filterWordsFuture.get();
        CommonUtils.printThreadLog("filterWords: "+ Arrays.toString(filterWords));
        CommonUtils.printThreadLog("主线程结束");

        /**
         * 总结:
         * 一般而言,commonPool为了提高性能
         * thenApply中回调任务和supplyAsync中的异步任务使用的是同一个线程
         * 特殊情况:
         * 如果supplyAsync中的任务是立即返回结果(不是耗时的任务),那么thenApply回调任务也会在主线程执行
         */
    }
}

```



要更好地控制执行回调任务的线程，可以使用异步回调。如果使用thenApplyAsync回调，那么它将从ForkJoinPool.commonPool()获得的另一个线程中执行

```java
package _03_completablefuture_callback;

import utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ThenApplyAsyncDemo02 {
    public static void main(String[] args) throws Exception {
        // 需求：异步读取filter_word.txt文件中的内容，读取完成后，转换为敏感词数组，主线程获取结果帮打印
        CommonUtils.printThreadLog("主线程开启");

        CompletableFuture<String[]> filterWordsFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtils.printThreadLog("读取filter_word.txt文件");
            String filterWordsContent = CommonUtils.readFile("filter_word.txt");
            return filterWordsContent;
        }).thenApplyAsync(filterWordsContent -> {
            /**
             * 一般而言,thenApply任务的执行和supplyAsync任务执行可以使用同一个线程执行
             * 如果suplyAsync任务立刻返回结果,则thenApply的任务在主线程中执行.
             */
            CommonUtils.printThreadLog("把文件内容转换成敏感词数组");
            String[] filterWords = filterWordsContent.split(",");
            return filterWords;
        });
        CommonUtils.printThreadLog("主线程继续执行");
        String[] filterWords = filterWordsFuture.get();
        CommonUtils.printThreadLog("filterWords: " + Arrays.toString(filterWords));
        CommonUtils.printThreadLog("主线程结束");
    }
}

```

以上程序中可鞥的运行结果显示的supplyAsync和thenApplyAsync是同一个线程，是因为JVM对线程池有优化，所以可能需要多运行几次。

此外，如果将Executor传递给thenApplyAsync()回调，则该回调的异步任务将从Executor的线程池中获取线程执行。

```java
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

```

![线程池获取线程不同.png](../picture/%E7%BA%BF%E7%A8%8B%E6%B1%A0%E8%8E%B7%E5%8F%96%E7%BA%BF%E7%A8%8B%E4%B8%8D%E5%90%8C.png)  

上图所示，说明supplyAsync和thenApplyAsync运行时可能不是一个线程



其他两个变调的变体版本如下，道理是一样的不再赘述

```java
// thenAccept和其异步回调
CompletableFuture<Void> thenAccept(Consumer<T> action);
CompletableFuture<Void> thenAcceptAsync(Consumer<T> action);
CompletableFuture<Void> thenAcceptAsync(Consumer<T> action, Executor extcutor);
// thenRun和其异步回调
CompletableFuture<Void> thenRun(Runnable action);
CompletableFuture<Void> thenRunAsync(Runnable action);
CompletableFuture<Void> thenRunAsync(Runnable action, Executor extcutor);
```



