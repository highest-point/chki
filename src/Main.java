import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("参数错误：需要三个文件路径参数");
            return;
        }

        try {
            // 读取并预处理文件内容
            String original = processContent(Files.readString(Paths.get(args[0]), StandardCharsets.UTF_8));
            String plagiarized = processContent(Files.readString(Paths.get(args[1]), StandardCharsets.UTF_8));

            // 计算最长公共子序列长度
            int lcsLength = calculateLCS(original, plagiarized);

            // 计算相似度并写入结果
            double similarity = calculateSimilarity(original.length(), plagiarized.length(), lcsLength);
            String result = String.format("%.2f", similarity);

            Files.writeString(Paths.get(args[2]), result, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.err.println("文件处理错误：" + e.getMessage());
        }

    }

    // 预处理内容（保留中文字符）
    public static String processContent(String content) {
        return content.replaceAll("[^\\u4e00-\\u9fa5]", "");
    }

    // 动态规划实现LCS计算
    public static int calculateLCS(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    // 计算相似度（保留两位小数）
    public static double calculateSimilarity(int len1, int len2, int lcs) {
        if (len1 + len2 == 0) return 100.00;
        return Math.round(200.0 * lcs / (len1 + len2) * 100) / 100.0;
    }

}