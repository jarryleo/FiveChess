package cn.leo.fivechess;

/**
 * Created by lsw on 2017/9/20.
 */

public class Constant {
    /**
     * 各情况定义的权重
     */
    //已落子
    public static final int HAD_CHESS = -1;
    //棋盘中间点
    public static final int MID_POINT = 1;
    //所在区域无生存空间
    public static final int NOT_ENOUGH_SPACE = -1;
    //    //将死
//    public static final int CHECKMATE = 14;
//    //让0缓1
//    public static final int R_0_D_1 = 12;
//    //让0缓2
//    public static final int R_0_D_2 = 10;
//    //让1缓1
//    public static final int R_1_D_1 = 8;
//    //让1缓2
//    public static final int R_1_D_2 = 7;
//    //让2缓2
//    public static final int R_2_D_2 = 4;
//    //让2缓3
//    public static final int R_2_D_3 = 3;
//    //对方_将死
//    public static final int RIVAL_CHECKMATE = 13;
//    //对方_让0缓1
//    public static final int RIVAL_R_0_D_1 = 11;
//    //对方_让0缓2
//    public static final int RIVAL_R_0_D_2 = 9;
//    //对方_让1缓1
//    public static final int RIVAL_R_1_D_1 = 6;
//    //对方_让1缓2
//    public static final int RIVAL_R_1_D_2 = 5;
//    //对方_让2缓2
//    public static final int RIVAL_R_2_D_2 = 2;
//    //对方_让2缓3
//    public static final int RIVAL_R_2_D_3 = 1;
    //将死
    public static final int CHECKMATE = 16;
    //对方_将死
    public static final int RIVAL_CHECKMATE = 15;
    //让0缓1
    public static final int R_0_D_1 = 14;
    //对方_让0缓1
    public static final int RIVAL_R_0_D_1 = 13;
    //让0缓2
    public static final int R_0_D_2 = 12;
    //对方_让0缓2
    public static final int RIVAL_R_0_D_2 = 11;
    //让1缓1
    public static final int R_1_D_1 = 10;
    //让1缓2_活三
    public static final int R_1_D_2_L3 = 9;
    //让1缓2_跳三
    public static final int R_1_D_2_J3 = 8;
    //对方_让1缓1
    public static final int RIVAL_R_1_D_1 = 7;
    //对方_让1缓2_活三
    public static final int RIVAL_R_1_D_2_L3 = 6;
    //对方_让1缓2_跳三
    public static final int RIVAL_R_1_D_2_J3 = 5;
    //让2缓2
    public static final int R_2_D_2 = 4;
    //让2缓3
    public static final int R_2_D_3 = 3;
    //对方_让2缓2
    public static final int RIVAL_R_2_D_2 = 2;
    //对方_让2缓3
    public static final int RIVAL_R_2_D_3 = 1;
}
