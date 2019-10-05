package cn.yescallop.googletrans4j;

/**
 * A test for translating.
 *
 * @author Scallop Ye
 */
public class TransTest {

    public static void main(String[] args) throws Exception {
        TransClient client = TransClient.newBuilder()
                .host("translate.google.cn")
                .build();

        TransRequest req = TransRequest.newBuilder()
                .text("曼珠沙華")
                .sourceLang("ja")
                .parameters(TransParameter.TRANSLITERATION)
                .build();
        TransResponse resp = client.send(req);
        System.out.println(resp);
    }
}
