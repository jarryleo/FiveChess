package cn.leo.fivechess.AI;

/**
 * Created by lsw on 2017/9/20.
 */
class Constant {
    /**
     * 各情况定义的权重
     */
    //已落子
    static final int HAD_CHESS = -1;
    //棋盘中间点
    static final int MID_POINT = 1;
    //所在区域无生存空间
    static final int NOT_ENOUGH_SPACE = -1;
    //将死
    static final int CHECKMATE = 19;
    //对方_将死
    static final int RIVAL_CHECKMATE = 18;
    //让0缓1
    static final int R_0_D_1 = 17;
    //对方_让0缓1
    static final int RIVAL_R_0_D_1 = 16;
    //让0缓2
    static final int R_0_D_2 = 15;
    //对方_让0缓2
    static final int RIVAL_R_0_D_2 = 14;
    //让1缓1_冲四
    static final int R_1_D_1_R4 = 13;
    //让1缓1_跳四
    static final int R_1_D_1_J4 = 12;
    //让1缓2_活三
    static final int R_1_D_2_L3 = 11;
    //让1缓2_跳三
    static final int R_1_D_2_J3 = 10;
    //让2缓3_活二
    static final int R_2_D_3_L2 = 9;
    //让2缓3_跳二
    static final int R_2_D_3_J2 = 8;
    //对方_让1缓1(冲四, 跳四)
    static final int RIVAL_R_1_D_1 = 7;
    //对方_让1缓2_活三
    static final int RIVAL_R_1_D_2_L3 = 6;
    //对方_让1缓2_跳三
    static final int RIVAL_R_1_D_2_J3 = 5;
    //让2缓2_眠三
    static final int R_2_D_2 = 4;
    //对方_让2缓2
    static final int RIVAL_R_2_D_2 = 3;
    //对方_让2缓3_活二
    static final int RIVAL_R_2_D_3_L2 = 2;
    //对方_让2缓3_跳二
    static final int RIVAL_R_2_D_3_J2 = 1;
}
