package cn.yescallop.googletrans4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A test for ticket acquiring.
 *
 * @author Scallop Ye
 */
public class TicketTest {

    @Test
    public void acquire() {
        int[] token = new int[2];
        TokenTicketUtil.parseToken("438891.3447851691", token);
        String ticket = TokenTicketUtil.acquireTicket("测试：这里是Scallop", token);
        assertEquals(ticket, "773737.883714");
    }
}
