package cn.yescallop.googletrans4j;

import java.net.http.HttpClient;

/**
 * A test for token acquiring.
 *
 * @author Scallop Ye
 */
public class TokenTest {

    public static void main(String[] args) throws Exception {
        TokenAcquirer ta = new TokenAcquirer(
                HttpClient.newHttpClient(),
                "translate.google.cn",
                null
        );
        System.out.println(ta.acquire("测试：这里是Scallop"));
    }
}
