package cn.yescallop.googletrans4j;

import java.net.http.HttpClient;

/**
 * A test for token acquiring.
 *
 * @author Scallop Ye
 */
public class TokenTest {

    public static void main(String[] args) throws Exception {
        String token = TokenTicketUtil.acquireToken(
                HttpClient.newHttpClient(),
                "translate.google.cn"
        );
        System.out.println(token);
    }
}
