package cn.leo.fivechess.utils;

import java.util.HashMap;

import cn.leo.fivechess.bean.Chess;

/**
 * Created by Leo on 2017/10/20.
 */

public class ChessToText {
    /**
     * 棋盘转字符串，2位16进制数字表示一个落子序号，奇数和偶数不同色
     *
     * @param chesses
     * @return
     */
    public static String getChessString(Chess[][] chesses) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < chesses.length; i++) {
            for (int j = 0; j < chesses[i].length; j++) {
                Chess chess = chesses[j][i];
                if (chess.color == 0) {
                    result.append("00");
                } else {
                    String hex = Integer.toHexString(chess.index);
                    result.append(hex.length() > 1 ? hex : "0" + hex);
                }
            }
        }
        return result.toString();
    }

    /**
     * 旋转棋局
     *
     * @param chessStr
     * @param rotate   角度 90,180,270  对应1,2,3
     * @return
     */
    public static String rotateChess(String chessStr, int rotate) {
        StringBuilder result = new StringBuilder();
        String newString;
        for (int i = 0; i < 225; i++) {
            int newX = i % 15;//10
            int newY = i / 15;//3
            int x = newY;//3
            int y = 14 - newX;//4
            result.append(chessStr.charAt(y * 30 + 2 * x));
            result.append(chessStr.charAt(y * 30 + 2 * x + 1));
        }
        newString = result.toString();
        if (--rotate > 0) {
            newString = rotateChess(newString, rotate);
        }
        return newString;
    }

    /**
     * 获取走势
     * 从第二个子开始，每个子的坐标减去上个子的坐标得走势
     * 4位一个坐标，前2位X，后2位Y，与前一个子坐标相减值+50为走势
     *
     * @param chess 棋盘转换成的字符串
     * @return 走势字符串
     */
    public static String getTrend(String chess) {
        HashMap<Integer, Integer> sp = new HashMap<>();
        for (int i = 0; i < chess.length(); i += 2) {
            String index = chess.substring(i, i + 2);
            int num = Integer.parseInt(index, 16);
            if (num > 0)
                sp.put(num, i / 2);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 2; i < sp.size(); i++) {
            int value = sp.get(i);
            int v = sp.get(i - 1);
            int x = (value % 15) - (v % 15) + 50;
            int y = (value / 15) - (v / 15) + 50;
            result.append(x).append(y);
        }
        return result.toString();
    }

    /**
     * 打印棋盘字符串矩阵
     *
     * @param string
     */
    public static void printChessStr(String string) {
        for (int i = 0; i < string.length(); i += 30) {
            System.out.println(string.substring(i, i + 30));
        }
    }
}
