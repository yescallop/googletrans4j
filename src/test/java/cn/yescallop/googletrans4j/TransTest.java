package cn.yescallop.googletrans4j;

/**
 * @author Scallop Ye
 */
public class TransTest {

    public static void main(String[] args) throws Exception {
        TransClient client = TransClient.newBuilder()
                .host("translate.google.cn")
                .build();
        ;
        TransRequest req = TransRequest.newBuilder()
                .text("testing")
                .targetLang("ja")
                .build();
        TransResponse resp = client.send(req);
        System.out.println(resp.rawResponse());
    }
}
