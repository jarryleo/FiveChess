package cn.leo.fivechess;

import org.junit.Test;

import cn.leo.fivechess.utils.ChessToText;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void trend() throws Exception {
        String s = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002d000000000000000000000000000d2c2927000000000000000000000000000025000000000000000015082a06262b28230000000000001a0c01030410002100000000000000171407020f1d1b001e0000000000001918050b0e2400000000000000000022160913000000000000000000000000110a1c000000000000000000000012001f00000000000000000000000000000020000000000000000000000000000000000000000000";
        //String r = ChessToText.getTrend(s);
        String r = ChessToText.rotateChess(s,2);
        //System.out.println(r);
        ChessToText.printChessStr(s);
        System.out.println("------------------------------");
        ChessToText.printChessStr(r);
    }
}