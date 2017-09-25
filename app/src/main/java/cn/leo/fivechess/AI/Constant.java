package cn.leo.fivechess.AI;

/**
 * Created by lsw on 2017/9/20.
 */

public class Constant {
    /**
     * 各情况定义的权重
     */
    //已落子
    public static final int HAD_CHESS = -10;
    //棋盘中间点
    public static final int MID_POINT = 1;
    //所在区域无生存空间
    public static final int NOT_ENOUGH_SPACE = -1;
    //将死
    public static final int CHECKMATE = 14;
    //让0缓1
    public static final int R_0_DELAY_1 = 12;
    //让0缓2
    public static final int R_0_DELAY_2 = 10;
    //让1缓1
    public static final int R_1_DELAY_1 = 8;
    //让1缓2
    public static final int R_1_DELAY_2 = 7;
    //让2缓2
    public static final int R_2_DELAY_2 = 4;
    //让2缓3
    public static final int R_2_DELAY_3 = 3;
    //对方_将死
    public static final int RIVAL_CHECKMATE = 13;
    //对方_让0缓1
    public static final int RIVAL_R_0_DELAY_1 = 11;
    //对方_让0缓2
    public static final int RIVAL_R_0_DELAY_2 = 9;
    //对方_让1缓1
    public static final int RIVAL_R_1_DELAY_1 = 6;
    //对方_让1缓2
    public static final int RIVAL_R_1_DELAY_2 = 5;
    //对方_让2缓2
    public static final int RIVAL_R_2_DELAY_2 = 2;
    //对方_让2缓3
    public static final int RIVAL_R_2_DELAY_3 = 1;
}
