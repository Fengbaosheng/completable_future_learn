package _02_completablefuture_create;

public class Test {
    public static void main(String[] args) {
        Thread childThread = new Thread(() -> System.out.println("子线程运行"));
        System.out.println(childThread.isDaemon()); // 输出 false（默认是非守护线程）
    }
}
