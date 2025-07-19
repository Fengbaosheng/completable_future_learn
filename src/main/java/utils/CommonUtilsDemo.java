package utils;

public class CommonUtilsDemo {
    public static void main(String[] args) {
        // 测试CommonUtils工具类
        String s = CommonUtils.readFile("news.txt");
        CommonUtils.printThreadLog(s);
    }
}
