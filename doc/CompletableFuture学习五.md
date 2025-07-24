## 异步任务编排
#### 编排2个依赖关系的异步任务 thenCompose()
需求：异步读取filter_word.txt文件中的内容，读取完成后，转换成敏感词数组让主线程待用

关于读取和解析内容，假设使用以下的 readFileFuture(String) 和 splitFuture(String) 方法完成

```java
    public static CompletableFuture<String> readFileFuture(String fileName) {
        return CompletableFuture.supplyAsync(()->{
            String fileWordContent = CommonUtils.readFile(fileName);
            return fileWordContent;
        });
    }

    public static CompletableFuture<String[]> splitFuture(String context){
        return CompletableFuture.supplyAsync(() -> {
            String[] filterWords = context.split(",");
            return filterWords;
        });
    }
```

现在，让我们先了解如果使用thenApply()结果会发生什么

```java
CompletableFuture<String> readFileFuture = readFileFuture("filter_word.txt");
CompletableFuture<CompletableFuture<String[]>> completableFutureCompletableFuture 
    = readFileFuture.thenApply(content -> {
    return splitFuture(content);
});
```

回顾之前的案例，thenApply(Function<T,R>)中Function回调会对上一步任务结果转换后得到一个简单值，但现在这种情况下，最终结果是嵌套的CompletableFuture，所以这是不符合预期的，那怎么办呢？

我们想要的是：把上一步异步任务的结果，转成一个CompletableFuture对象，这个CompletableFuture对象中包含本次异步任务处理后的结果。也就是说，我们想组合上一步异步任务的结果到下一个新的异步任务中，结果由这个新的异步任务返回。

此时，我们可以使用thenCompose()方法代替，我们可以把它理解为异步任务的组合。

```java
CompletableFuture<R> thenCompose(Function<T, CompletableFuture<R>> func)
```

所以，thenCompose()用来连接两个有依赖关系的异步任务，结果由第二个任务返回。

```java
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

```

因此，这里累计了一个经验：

如果我们想编排两个依赖关系的异步任务（CompletableFuture对象），可以使用thenCompose()方法。

当然，thenCompose也存在异步回调变体版本：

```java
CompletableFuture<R> thenCompose(Function<T, CompletableFuture<R>> func)

CompletableFuture<R> thenComposeAsync(Function<T, CompletableFuture<R>> func)
CompletableFuture<R> thenComposeAsync(Function<T, CompletableFuture<R>> func, Executor executor)
```

#### 编排2个非依赖关系的异步任务thenCombine()
我们已经知道，当其中一个Future依赖于另一个Future，使用thenCompose()用于组合两个Future。如果两个Future之间没有依赖关系，我们希望两个Future独立运行并在两者都完成后执行回调操作，可以使用thenCombine();

```java
// T是第一个任务的结果 U是第二个任务的结果 V是经BiFunction应用转换后的结果
CompletableFuture<V> thenCombine(CompletableFuture<U> other,BiFunction<T,U,V> func)
```

需求：替换新闻稿news.txt中敏感词词汇，把敏感词词汇替换成***，敏感词词汇存储在filter_words.txt中。

```java
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

```

注意：当两个Future都完成时，才将两个异步任务的结果传递给thenCombine()的回调函数做进一步处理。

和以往一样，thenCombine也存在异步回调变体版本

```java
CompletableFuture<V> thenCombine(CompletableFuture<U> other,BiFunction<T,U,V> func)
CompletableFuture<V> thenCombineAsync(CompletableFuture<U> other,BiFunction<T,U,V> func)
CompletableFuture<V> thenCombineAsync(CompletableFuture<U> other,BiFunction<T,U,V> func,Executor executor)
```

#### 合并多个异步任务 allOf/anyOf
我们使用thenCompose()和thenCombine()将两个CompletableFuture组合在和合并在一起。

如果要编排任意数量的CompletableFuture怎么办？可以使用以下方法来组合任意数量的CompletableFuture。

```java
public static CompletableFuture<Void> allOf(CompletableFuture<?>...cfs)
public static CompletableFuture<Object> anyOf(CompletableFuture<?>...cfs)
```

CompletableFuture.allOf()用于以下情形中：有多个独立并行运行的Future，并在所有这些Future都完成后执行一些操作。

需求：统计news1.txt，news2.txt，news3.txt文件中包含CompletableFuture关键字的文件的个数

```java
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

```



当给定的多个异步任务有任意一个Future完成时，需要执行一些操作，我们可以使用anyOf()方法

```java
public static CompletableFuture<Object> anyOf(CompletableFuture<?>...cfs)
```

anyOf()返回一个新的CompletableFuture，新的CompletableFuture的结果和cfs中已完成的那个异步任务结果相同。

演示案例：anyOf执行过程

```plain
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
```

在上面的案例中，当三个CompletableFuture中任意一个完成时，anyOfFuture就完成了。由于future2的睡眠时间最少，因此它将首先完成，最终结果将是“Future2的结果"。

注意：

+ anyOf()方法返回类型必须是CompletableFuture<Object>。
+ anyOf()的问题在于，如果你拥有不同返回类型结果的CompletableFuture，那么你将不知道最终CompletableFuture的类型。



