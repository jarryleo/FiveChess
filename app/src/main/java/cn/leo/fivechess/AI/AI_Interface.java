package cn.leo.fivechess.AI;

import cn.leo.fivechess.bean.Chess;

/**
 * AI 接口  为了实现引擎替换，最好实现这个接口
 * Created by 刘佳睿 on 2017/9/26.
 */

public interface AI_Interface {
    /**
     * AI的调用入口
     *
     * @param chess 传入棋局
     * @param color 传入AI所执棋子 颜色 （1 黑 2 白）
     * @return 返回一个落子点
     */
    Chess AIGo(Chess chess[][], int color);

    /**
     * 给你的引擎取个名字，方便评比展示
     *
     * @return
     */
    String getAIName();
}
