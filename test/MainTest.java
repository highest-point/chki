import org.junit.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.Assert.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainTest {
    @Rule
    public org.junit.rules.TemporaryFolder tempFolder = new org.junit.rules.TemporaryFolder();

    // 测试1: 预处理过滤非中文字符
    @Test
    public void testProcessContent() {
        String input = "Hello世界！123测试_";
        String expected = "世界测试";
        assertEquals(expected, Main.processContent(input));
    }

    // 测试2: 完全相同字符串的LCS
    @Test
    public void testCalculateLCS_IdenticalStrings() {
        String str1 = "中文相似度检测";
        assertEquals(7, Main.calculateLCS(str1, str1));
    }

    // 测试3: 完全不同的字符串
    @Test
    public void testCalculateLCS_NoCommon() {
        String str1 = "天地玄黄";
        String str2 = "宇宙洪荒";
        assertEquals(0, Main.calculateLCS(str1, str2));
    }

    // 测试4: 部分匹配场景
    @Test
    public void testCalculateLCS_PartialMatch() {
        String str1 = "软件工程真有趣";
        String str2 = "软件工程不容易";
        assertEquals(4, Main.calculateLCS(str1, str2)); // "软件工程"
    }

    // 测试5: 空字符串处理
    @Test
    public void testCalculateLCS_EmptyString() {
        assertEquals(0, Main.calculateLCS("", "非空"));
        assertEquals(0, Main.calculateLCS("非空", ""));
        assertEquals(0, Main.calculateLCS("", ""));
    }

    // 测试6: 相似度计算分母为零
    @Test
    public void testCalculateSimilarity_ZeroDenominator() {
        assertEquals(100.00, Main.calculateSimilarity(0, 0, 0), 0.001);
    }

    // 测试7: 四舍五入处理
    @Test
    public void testCalculateSimilarity_Rounding() {
        // 200 * 3 / (5 + 5) = 60.0
        assertEquals(60.00, Main.calculateSimilarity(5, 5, 3), 0.001);

        // 200 * 7 / (10 + 11) = 66.666... → 66.67
        assertEquals(66.67, Main.calculateSimilarity(10, 11, 7), 0.001);
    }

    // 测试8: 完整流程测试（使用临时文件）
    @Test
    public void testFullProcess() throws IOException {
        Path original = tempFolder.newFile("orig.txt").toPath();
        Path plagiarized = tempFolder.newFile("plag.txt").toPath();
        Path output = tempFolder.newFile("result.txt").toPath();

        Files.write(original, "软件工程测试案例".getBytes());
        Files.write(plagiarized, "软件案例测试工程".getBytes());

        Main.main(new String[]{
                original.toString(),
                plagiarized.toString(),
                output.toString()
        });

        String result = new String(Files.readAllBytes(output));
        assertEquals("50.00", result);
    }

    // 测试9: 参数错误处理（验证错误输出）
    @Test
    public void testInvalidArguments() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Main.main(new String[]{"only_one_arg"});
        assertTrue(errContent.toString().contains("参数错误"));
    }

    // 测试10: 空文件处理
    @Test
    public void testEmptyFiles() throws IOException {
        Path original = tempFolder.newFile("empty1.txt").toPath();
        Path plagiarized = tempFolder.newFile("empty2.txt").toPath();
        Path output = tempFolder.newFile("empty_result.txt").toPath();

        Main.main(new String[]{
                original.toString(),
                plagiarized.toString(),
                output.toString()
        });

        String result = new String(Files.readAllBytes(output));
        assertEquals("100.00", result);
    }
}