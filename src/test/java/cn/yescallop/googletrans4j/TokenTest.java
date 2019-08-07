package cn.yescallop.googletrans4j;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * @author Scallop Ye
 */
public class TokenTest {

    public static void main(String[] args) throws Exception {
        TokenAcquirer ta = new TokenAcquirer(
                HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_2)
                        .connectTimeout(Duration.ofSeconds(5))
                        .build(),
                "translate.google.cn"
        );
        System.out.println(ta.acquire("测试：这里是Scallop"));
    }
}
