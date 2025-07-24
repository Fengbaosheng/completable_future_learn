## 1、Future VS CompletableFuture
#### 1.1 准备工作
```java
package org.common;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class CommonUtils {
    public static String readFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static void sleepMillis(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printThreadLog(String message) {
        String result = new StringJoiner(" | ")
                .add(String.valueOf(System.currentTimeMillis()))
                .add(String.format("%2d", Thread.currentThread().getId()))
                .add(String.valueOf(Thread.currentThread().getName()))
                .add(message)
                .toString();
        System.out.println(result);
    }
}

```

#### 1.2 Future的局限性
eg:  替换新闻稿（news.txt）中敏感词，把敏感词换成***，敏感词存储在filter_words.txt中

```java
package future_vs_completablefuture;

import utils.CommonUtils;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        // step 1: 读取敏感词汇 ==> thread1
        Future<String[]> filterWordFuture = executor.submit(() -> {
            String str = CommonUtils.readFile("filter_word.txt");
            String[] filterWords = str.split(",");
            return filterWords;
        });

        // step 2: 读取新闻稿 ==> thread2
        Future<String> newsFuture = executor.submit(() -> {
            return CommonUtils.readFile("news.txt");
        });

        // step 3: 替换操作 ==> thread3
        Future<String> replaceFuture = executor.submit(() -> {
            String[] filterWords = filterWordFuture.get();
            String news = newsFuture.get();
            for (String filterWord : filterWords) {
                if (news.indexOf(filterWord) >= 0) {
                    news = news.replace(filterWord, "***");
                }
            }
            return news;
        });
        // step 4: 打印替换后的新闻稿 ==> main
        System.out.println("过滤敏感词后的新闻:" + replaceFuture.get());
    }
}

```

```latex
干他妈的，今天真TM热啊
```

```latex
他妈的,NB,TM,TMD,tm,tmd
```

![测试结果.png](../picture/%E6%B5%8B%E8%AF%95%E7%BB%93%E6%9E%9C.png)

通过上述代码，我们会发现，Future相比于所有任务都在主线程处理，有很多优势，但也同时存在不足，至少表现如下：

+ **在没有阻塞的情况下，无法对Future的结果执行进一步操作。**Future不会告诉你它什么时候完成，你如果想要得到结果，必须通过一个get()方法，该方法会阻塞直到得到结果为止。它不具备将回调函数附加到Future后并在Future的结果可用时自动调用回调的能力。
+ **无法解决任务相互依赖的问题。**filterWordFuture和newsFuture的结果不能自动发送给replaceFuture，需要在replaceFuture中手动获取，所以使用Future不能轻而易举的创建异步工作流。
+ **不能将多个Future合并在一起。**假设你需要多种不同的Future，你想在它们全部并行完成后再运行某个函数，Future很难独立完成这一需求。
+ **没有异常处理。**Future提供的方法中没有专门的API应对异常处理，还是需要开发者自己手动处理异常。

#### 1.3 CompletableFuture的优势
![实现关系.png](../picture/%E5%AE%9E%E7%8E%B0%E5%85%B3%E7%B3%BB.png)

**CompletableFuture**实现了**Future**和**CompletionStage**接口

CompletableFuture相对于Future具有以下优势：

+ 为快速创建、链接依赖和组合多个Future提供了大量的便利方法。
+ 提供了适用于各种开发场景的回调函数，它还提供了非常全面的异常处理支持。
+ 无缝衔接和亲和lambda表达式和Stream API。
+ 真正意义上的异步编程，把异步编程和函数式编程，响应式编程多种高阶编程思维集于一身，设计上更优雅。







