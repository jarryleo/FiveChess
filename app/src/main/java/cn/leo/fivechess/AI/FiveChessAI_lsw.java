package cn.leo.fivechess.AI;

import android.util.SparseIntArray;

import cn.leo.fivechess.Constant;
import cn.leo.fivechess.bean.Chess;

public class FiveChessAI_lsw implements AI_Interface {
    /**
     * 五子棋AI
     *
     * @author 刘晟玮
     */
    Chess chess[][];// = new Chess[15][15]; // 接受棋盘的所有棋子
    private int ownWeight[][] = new int[15][15]; // 己方每个点权重
    private int oppositeWeight[][] = new int[15][15]; // 对方每个点权重
    private int computerColor; // 电脑要走的棋子颜色 黑1 或 白2 ,0为空

    @Override
    public Chess AIGo(Chess chess[][], int color) { // 返回计算机落子
        this.chess = chess;
        this.computerColor = color;
        Chess point = new Chess(); // 定义返回的对象
        int x = -1, y = -1; // 计算机要走的坐标
        int max = 0; // 最大权重
        calculateWeight(); // 计算权重

        /*TODO 根据己方和对方每个点权重计算要下子的位置*/
        for (int i = 0; i < chess.length; i++) {
            for (int j = 0; j < chess[i].length; j++) {
                if (max <= ownWeight[i][j]) {
                    if (Math.random() * 100 < 33 && max == ownWeight[i][j]) { //加点随机事件
                        max = ownWeight[i][j]; // 获取最大权重
                        x = i; // 获取坐标
                        y = j;
                    }
                }
                if (max <= oppositeWeight[i][j]) {
                    if (Math.random() * 100 > 66 && max == oppositeWeight[i][j]) { //加点随机事件
                        continue;
                    }
                    max = oppositeWeight[i][j]; // 获取最大权重
                    x = i; // 获取坐标
                    y = j;
                }
            }
        }
        point.x = x;
        point.y = y;
        point.color = computerColor;
        return point;
    }

    @Override
    public String getAIName() {
        return "刘晟玮的引擎";
    }

    private void calculateWeight() { // 获取双方所有坐标的权重
        for (int i = 0; i < chess.length; i++) {
            for (int j = 0; j < chess[i].length; j++) {
                ownWeight[i][j] = weightSum(i, j, computerColor);
                oppositeWeight[i][j] = weightSum(i, j, 3 - computerColor);
            }
        }
    }


    private int weightSum(int x, int y, int color) { // 一个坐标的四线总权重
        int weight = 0; // 总权重
        if (chess[x][y].color > 0) { // 坐标有子
            return Constant.HAD_CHESS;
        }
        int[] line = new int[4];
        line[0] = singleLine(x, y, color, 1, 0);
        line[1] = singleLine(x, y, color, 0, 1);
        line[2] = singleLine(x, y, color, 1, 1);
        line[3] = singleLine(x, y, color, -1, 1);
        /*TODO 根据四线权重计算总权重，形成33等权重另计但不能比自身5连权重高。*/
        //创建Map, 用于统计每种权重的个数
        SparseIntArray map = new SparseIntArray();
        for (int i = 0; i <= Constant.CHECKMATE; i++) {
            map.put(i, 0);
        }
        //统计
        for (int i : line) {
            map.put(i, map.get(i) + 1);
        }

        for (int i = Constant.CHECKMATE; i > 0; i--) {
            if (map.get(i) >= 1) {
                //找到最大权重
                weight = i * 10;
                if (i >= Constant.RIVAL_R_0_D_1) {
                    //当最大权重大于Constant.RIVAL_R_0_D_1时, 直接返回
                    return weight;
                }
                break;
            }
        }
        if (map.get(Constant.R_1_D_1) > 1 ||
                (map.get(Constant.R_1_D_1) == 1 && map.get(Constant.R_1_D_2_L3) > 0) ||
                (map.get(Constant.R_1_D_1) == 1 && map.get(Constant.R_1_D_2_J3) > 0)) {
            //让0缓1 = 让1缓1 + 让1缓1 = 让1缓1 + 让1缓2
            weight = Constant.R_0_D_1;
        } else if (map.get(Constant.RIVAL_R_1_D_1) > 1 ||
                (map.get(Constant.RIVAL_R_1_D_1) == 1 && map.get(Constant.RIVAL_R_1_D_2_L3) > 0) ||
                (map.get(Constant.RIVAL_R_1_D_1) == 1 && map.get(Constant.RIVAL_R_1_D_2_J3) > 0)) {
            //让0缓1 = 让1缓1 + 让1缓1 = 让1缓1 + 让1缓2
            weight = Constant.RIVAL_R_0_D_1;
        } else if ((map.get(Constant.R_1_D_2_L3) > 1) ||
                (map.get(Constant.R_1_D_2_J3) > 1) ||
                (map.get(Constant.R_1_D_2_L3) >= 1 && (map.get(Constant.R_1_D_2_J3) >= 1))) {
            //让0缓2 = 让1缓2 + 让1缓2
            weight = Constant.R_0_D_2;
        } else if ((map.get(Constant.RIVAL_R_1_D_2_L3) > 1) ||
                (map.get(Constant.RIVAL_R_1_D_2_J3) > 1) ||
                (map.get(Constant.RIVAL_R_1_D_2_L3) >= 1 &&
                        (map.get(Constant.RIVAL_R_1_D_2_J3) >= 1))) {
            //让0缓2 = 让1缓2 + 让1缓2
            weight = Constant.RIVAL_R_0_D_2;
        } else if (weight == Constant.R_1_D_1 &&
                (map.get(Constant.RIVAL_R_1_D_1) == 1 ||
                        map.get(Constant.RIVAL_R_1_D_2_L3) == 1 ||
                        map.get(Constant.RIVAL_R_1_D_2_J3) == 1)) {
            //己方让1缓1, 对方让1的权重, 与让0缓2平级
            weight += 10;
        } else if (weight == Constant.R_1_D_2_L3 &&
                (map.get(Constant.RIVAL_R_1_D_1) == 1 ||
                        map.get(Constant.RIVAL_R_1_D_2_L3) == 1 ||
                        map.get(Constant.RIVAL_R_1_D_2_J3) == 1)) {
            //己方活三(让1缓2), 对方让1的权重, 优于让1缓1
            weight += 15;
        } else if (weight == Constant.R_1_D_2_J3 &&
                (map.get(Constant.RIVAL_R_1_D_1) == 1 ||
                        map.get(Constant.RIVAL_R_1_D_2_L3) == 1 ||
                        map.get(Constant.RIVAL_R_1_D_2_J3) == 1)) {
            //己方跳三(让1缓2), 对方让1的权重, 优于让1缓1
            weight += 25;
        } else if (color == computerColor && weight >= Constant.R_1_D_2_J3) {
            //己方权重大于跳三(让1缓2)的权重时, 每存在一个己方让2, 则权重+1
            weight += map.get(Constant.R_2_D_2) + map.get(Constant.R_2_D_3);
        } else if (color != computerColor && weight >= Constant.RIVAL_R_1_D_2_J3) {
            //对方权重大于对方跳三(让1缓2)的权重时, 每存在一个对方让2, 则权重+1
            weight += map.get(Constant.RIVAL_R_2_D_2) + map.get(Constant.RIVAL_R_2_D_3);
        }

        if (x == 7 && y == 7) { // 中间点权重+1
            weight += Constant.MID_POINT;
        }
        return weight;
    }

    /*单线计算*/
    private int singleLine(int x, int y, int color, int px, int py) {
        int leftLive = oneSide(x, y, color, px, py, 0); //左边生存空间
        int rightLive = oneSide(x, y, color, -px, -py, 0); //右边生存空间
        if (leftLive + rightLive < 4) return Constant.NOT_ENOUGH_SPACE;//左右生存空间少于4，此线无意义；

        int leftSpace = oneSide(x, y, 0, px, py, 1); //左边相邻空格数
        int rightSpace = oneSide(x, y, 0, -px, -py, 1); //右边相邻空格数

        int leftSame = oneSide(x, y, color, px, py, 1); //左边相邻连续同色
        int rightSame = oneSide(x, y, color, -px, -py, 1); //右边相邻连续同色

        int leftNSame = oneSide(x, y, color, px, py, 2); //左边不相邻（1个空位）同色数
        int rightNSame = oneSide(x, y, color, -px, -py, 2); //右边不相邻（1个空位）同色数

        /*TODO 根据上方8个条件计算一条线的权重*/
        if (color == computerColor) { //己方
            if (isCheckmate(leftSame, rightSame))
                return Constant.CHECKMATE;
            if (isLife4(leftLive, rightLive, leftSame, rightSame))
                return Constant.R_0_D_1;
            if (isRush4(leftLive, rightLive, leftSame, rightSame))
                return Constant.R_1_D_1;
            if (isJump4(leftSpace, rightSpace, leftSame, rightSame, leftNSame, rightNSame))
                return Constant.R_1_D_1;
            if (isLife3(leftLive, rightLive, leftSpace, rightSpace, leftSame, rightSame))
                return Constant.R_1_D_2_L3;
            if (isJump3(leftSpace, rightSpace, leftSame, rightSame, leftNSame, rightNSame))
                return Constant.R_1_D_2_J3;
            if (isSleep3(leftLive, rightLive, leftSpace, rightSpace, leftSame, rightSame))
                return Constant.R_2_D_2;
            if (isLife2(rightLive, leftSpace, rightSpace, leftSame, rightSame))
                return Constant.R_2_D_3;
        } else {//对方
            if (isCheckmate(leftSame, rightSame))
                return Constant.RIVAL_CHECKMATE;
            if (isLife4(leftLive, rightLive, leftSame, rightSame))
                return Constant.RIVAL_R_0_D_1;
            if (isRush4(leftLive, rightLive, leftSame, rightSame))
                return Constant.RIVAL_R_1_D_1;
            if (isJump4(leftSpace, rightSpace, leftSame, rightSame, leftNSame, rightNSame))
                return Constant.RIVAL_R_1_D_1;
            if (isLife3(leftLive, rightLive, leftSpace, rightSpace, leftSame, rightSame))
                return Constant.RIVAL_R_1_D_2_L3;
            if (isJump3(leftSpace, rightSpace, leftSame, rightSame, leftNSame, rightNSame))
                return Constant.RIVAL_R_1_D_2_J3;
            if (isSleep3(leftLive, rightLive, leftSpace, rightSpace, leftSame, rightSame))
                return Constant.RIVAL_R_2_D_2;
            if (isLife2(rightLive, leftSpace, rightSpace, leftSame, rightSame))
                return Constant.RIVAL_R_2_D_3;
        }
        return 0;
    }

    private boolean isCheckmate(int leftSame, int rightSame) {
        return leftSame == 4 ||
                (leftSame == 3 && rightSame == 1) ||
                (leftSame >= 2 && rightSame >= 2) ||
                (leftSame == 1 && rightSame == 3) ||
                rightSame == 4;
    }

    private boolean isLife4(int leftLive, int rightLive, int leftSame, int rightSame) {
        return (leftSame == 3 && leftLive > 3 && rightLive > 0) ||
                (rightSame == 3 && rightLive > 3 && leftLive > 0) ||
                (leftSame == 2 && leftLive > 2 && rightSame == 1 && rightLive > 1) ||
                (rightSame == 2 && rightLive > 2 && leftSame == 1 && leftLive > 1);
    }

    private boolean isRush4(int leftLive, int rightLive, int leftSame, int rightSame) {
        return (leftSame == 3 && leftLive == 3 && rightLive > 0) ||
                (leftSame == 2 && leftLive == 2 && rightSame == 1 && rightLive > 1) ||
                (leftSame == 1 && leftLive == 1 && rightSame == 2 && rightLive > 2) ||
                (leftLive == 0 && rightSame == 3 && rightLive > 3) ||
                (rightSame == 3 && rightLive == 3 && leftLive > 0) ||
                (rightSame == 2 && rightLive == 2 && leftSame == 1 && leftLive > 1) ||
                (rightSame == 1 && rightLive == 1 && leftSame == 2 && leftLive > 2) ||
                (rightLive == 0 && leftSame == 3 && leftLive > 3);
    }

    private boolean isJump4(int leftSpace, int rightSpace, int leftSame,
                            int rightSame, int leftNSame, int rightNSame) {
        return (leftSpace == 1 && leftNSame >= 3) ||
                (leftSame == 1 && leftNSame >= 3) ||
                (leftSame == 2 && leftNSame >= 3) ||
                (leftSpace == 1 && leftNSame >= 2 && rightSame >= 1) ||
                (leftSame == 1 && leftNSame >= 2 && rightSame >= 1) ||
                (leftSpace == 1 && leftNSame >= 1 && rightSame >= 2) ||
                (rightSpace == 1 && rightNSame >= 3) ||
                (rightSame == 1 && rightNSame >= 3) ||
                (rightSame == 2 && rightNSame >= 3) ||
                (rightSpace == 1 && rightNSame >= 2 && leftSame >= 1) ||
                (rightSame == 1 && rightNSame >= 2 && leftSame >= 1) ||
                (rightSpace == 1 && rightNSame >= 1 && leftSame >= 2);
    }

    private boolean isLife3(int leftLive, int rightLive, int leftSpace,
                            int rightSpace, int leftSame, int rightSame) {
        return (leftSame == 2 && leftLive > 2 && rightSpace > 0) ||
                (leftSame == 1 && leftLive > 1 && rightSame == 1 && rightLive > 1) ||
                (leftSpace > 0 && rightSame == 2 && rightLive > 2);
    }

    private boolean isJump3(int leftSpace, int rightSpace, int leftSame,
                            int rightSame, int leftNSame, int rightNSame) {
        return (leftSpace == 1 && leftNSame == 2) ||
                (leftSame == 1 && leftNSame == 2) ||
                (leftSpace == 1 && leftNSame == 1 && rightSame == 1) ||
                (rightSpace == 1 && rightNSame == 2) ||
                (rightSame == 1 && rightNSame == 2) ||
                (rightSpace == 1 && rightNSame == 1 && leftSame == 1);
    }

    private boolean isSleep3(int leftLive, int rightLive, int leftSpace,
                             int rightSpace, int leftSame, int rightSame) {
        return (leftSame == 2 && leftLive == 2 && rightSpace == 1 && rightLive >= 2) ||
                (leftSame == 1 && leftLive == 1 && rightSame == 1 && rightLive >= 3) ||
                (leftLive == 0 && rightSame == 2 && rightLive >= 4) ||
                (rightSame == 2 && rightLive == 2 && leftSpace == 1 && leftLive >= 2) ||
                (rightSame == 1 && rightLive == 1 && leftSame == 1 && leftLive >= 3) ||
                (rightLive == 0 && leftSame == 2 && leftLive >= 4);
    }

    private boolean isLife2(int rightLive, int leftSpace, int rightSpace,
                            int leftSame, int rightSame) {
        return (leftSame == 1 && rightLive > 1 && rightSpace > 0) ||
                (rightSame == 1 && rightLive > 1 && leftSpace > 0);
    }


    /**
     * 往一个方向查找指定颜色数 （0空，1黑，2白）
     *
     * @param x     当前点x坐标
     * @param y     当前点y坐标
     * @param color 查找的颜色
     * @param px    方向控制 +1 0 -1
     * @param py    方向控制 +1 0 -1
     * @param mode  查找模式 1查找相邻的连续同色；2查找不相邻的同色(最多间隔一个空)；0：查找同色和空格；
     * @return 返回一个方向的查找到的数目，不包含当前点
     */
    private int oneSide(int x, int y, int color, int px, int py, int mode) {
        int num = 0, space = 0;
        while (!(x + px < 0 || x + px > 14 ||
                y + py < 0 || y + py > 14)) {
            x += px;
            y += py;
            if (chess[x][y].color == 3 - color) break;
            if (mode == 1 && chess[x][y].color != color) break;
            if (mode == 2 && chess[x][y].color == 0) {
                space++;
                if (space > 1) break;
                continue;
            }
            num++;
        }
        return num;
    }
}
