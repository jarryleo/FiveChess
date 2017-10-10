package cn.leo.fivechess.AI;

/**
 * Created by lsw on 2017/9/20.
 */
class Constant {
    /**
     * 各情况定义的权重
     */
    //已落子
    static final int HAD_CHESS = -10;
    //棋盘中间点
    static final int MID_POINT = 1;
    //所在区域无生存空间
    static final int NOT_ENOUGH_SPACE = -1;
    //将死
    static final int CHECKMATE = 20;
    //对方_将死
    static final int RIVAL_CHECKMATE = 19;
    //让0缓1
    static final int R_0_D_1 = 18;
    //对方_让0缓1
    static final int RIVAL_R_0_D_1 = 17;
    //让0缓2
    static final int R_0_D_2 = 16;
    //对方_让0缓2
    static final int RIVAL_R_0_D_2 = 15;
    //让1缓1_冲四
    static final int R_1_D_1_R4 = 14;
    //让1缓1_跳四
    static final int R_1_D_1_J4 = 13;
    //让1缓2_活三
    static final int R_1_D_2_L3 = 12;
    //让1缓2_跳三
    static final int R_1_D_2_J3 = 11;
    //对方_让1缓1_冲四
    static final int RIVAL_R_1_D_1_R4 = 10;
    //对方_让1缓1_跳四
    static final int RIVAL_R_1_D_1_J4 = 9;
    //对方_让1缓2_活三
    static final int RIVAL_R_1_D_2_L3 = 8;
    //对方_让1缓2_跳三
    static final int RIVAL_R_1_D_2_J3 = 7;
    //让2缓2_眠三
    static final int R_2_D_2 = 6;
    //让2缓3_活二
    static final int R_2_D_3_L2 = 5;
    //让2缓3_跳二
    static final int R_2_D_3_J2 = 4;
    //对方_让2缓2_眠三
    static final int RIVAL_R_2_D_2 = 3;
    //对方_让2缓3_活二
    static final int RIVAL_R_2_D_3_L2 = 2;
    //对方_让2缓3_跳二
    static final int RIVAL_R_2_D_3_J2 = 1;
}
