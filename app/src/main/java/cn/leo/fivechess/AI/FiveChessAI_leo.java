package cn.leo.fivechess.AI;

import cn.leo.fivechess.bean.Chess;

public class FiveChessAI_leo implements AI_Interface {
    private static final int STEP_KILL = 99999;
    private static final int STEP_DANGER = 99998;
    private static final int STEP_FOUR = 88888;
    private static final int STEP_SLAY = 77777;
    private static final int STEP_AT_FOUR = 44444;
    /**
     * 五子棋AI
     *
     * @author 刘佳睿
     */
    Chess chess[][];// = new Chess[15][15]; // 接受棋盘的所有棋子
    private int ownWeight[][] = new int[15][15]; // 己方每个点权重
    private int oppositeWeight[][] = new int[15][15]; // 对方每个点权重
    private int computerColor; // 电脑要走的棋子颜色 黑1 或 白2 ,0为空

    @Override
    public Chess AIGo(Chess chess[][], int color) { // 返回计算机落子
        this.chess = chess;
        this.computerColor = color;
        calculateWeight(); // 计算权重
        Chess point = new Chess(); // 定义返回的对象
        point.color = computerColor; //返回的颜色必然是自己要走的颜色
        int x1 = -1, y1 = -1; //己方最大权重记录坐标
        int x2 = -1, y2 = -1; //对方最大权重记录坐标
        int x3 = -1, y3 = -1; //双方权重合值最大记录坐标
        int ownMax = 0; // 己方最大权重
        int oppositeMax = 0; // 对方最大权重
        int oppositeMin = 0; // 对方最小权重
        int sumMax = 0;//双方合起来的权重
        /*开始分析所有权重*/
        for (int i = 0; i < chess.length; i++) {
            for (int j = 0; j < chess[i].length; j++) {
                /*处理己方权重*/
                if (ownMax < ownWeight[i][j]) {
                    ownMax = ownWeight[i][j]; // 获取己方最大权重
                    x1 = i; // 获取坐标
                    y1 = j;
                } else if (ownMax == ownWeight[i][j]) {
                    if (Math.random() * 100 > 50) { //权重相同加点随机事件
                        ownMax = ownWeight[i][j]; // 获取己方最大权重
                        x1 = i; // 获取坐标
                        y1 = j;
                    }
                }
                //己方已经四连，直接落子
                if (ownMax == STEP_KILL || ownMax == STEP_DANGER) {
                    point.x = x1;
                    point.y = y1;
                    return point;
                }
                /*处理对方权重*/
                if (oppositeMax < oppositeWeight[i][j]) {
                    oppositeMax = oppositeWeight[i][j]; // 获取对方最大权重
                    x2 = i; // 获取坐标
                    y2 = j;
                } else if (oppositeMax == oppositeWeight[i][j]) {
                    if (Math.random() * 100 > 50) { //权重相同加点随机事件
                        oppositeMax = oppositeWeight[i][j]; // 获取对方最大权重
                        x2 = i; // 获取坐标
                        y2 = j;
                    }
                }
                //最小权重，负的，对面已经有的形式判断
                if (oppositeMin > oppositeWeight[i][j]) {
                    oppositeMin = oppositeWeight[i][j];
                }
                /*处理双方权重相加*/
                if (sumMax < ownWeight[i][j] + oppositeWeight[i][j]) {  //两边总权重
                    sumMax = ownWeight[i][j] + oppositeWeight[i][j];
                    x3 = i;
                    y3 = j;
                } else if (sumMax == ownWeight[i][j] + oppositeWeight[i][j]) {
                    if (Math.random() * 100 > 50) { //权重相同加点随机事件
                        sumMax = ownWeight[i][j] + oppositeWeight[i][j];
                        x3 = i;
                        y3 = j;
                    }
                }
            }
        }
        //对方已经双线成杀，不拦截，全力冲四跳四
        if (oppositeMin == -STEP_SLAY) {
            for (int i = 0; i < chess.length; i++) {
                for (int j = 0; j < chess[i].length; j++) {
                    if (ownWeight[i][j] == STEP_AT_FOUR ||
                            ownWeight[i][j] == STEP_FOUR) {
                        point.x = i;
                        point.y = j;
                        return point;
                    }
                }
            }
        }
        //对方将要5连但是可以拦截
        if (oppositeMax == STEP_DANGER) {
            point.x = x2;
            point.y = y2;
            return point;
        }
        //己方将要双线成杀
        if (ownMax == STEP_SLAY) {
            point.x = x1;
            point.y = y1;
            return point;
        }
        //对方将要双线成杀
        if (oppositeMax == STEP_SLAY) {
            point.x = x2;
            point.y = y2;
            return point;
        }
        //自己将活四
        if (ownMax == STEP_FOUR) {
            point.x = x1;
            point.y = y1;
            //对面将活四
        } else if (oppositeMax == STEP_FOUR) {
            point.x = x2;
            point.y = y2;
            //剩下走双方权重相合最大的点
        } else {
            point.x = x3;
            point.y = y3;
        }
        return point;
    }

    @Override
    public String getAIName() {
        return "刘佳睿的引擎";
    }

    /*获取双方所有坐标的权重*/
    private void calculateWeight() {
        for (int i = 0; i < chess.length; i++) {
            for (int j = 0; j < chess[i].length; j++) {
                ownWeight[i][j] = weightSum(i, j, computerColor);
                oppositeWeight[i][j] = weightSum(i, j, 3 - computerColor);
            }
        }
    }

    /*分析一个点所能形成的局势，用权重表示*/
    private int weightSum(int x, int y, int color) {
        int weight = 0; // 总权重
        // 如果坐标处有子，则没有权重,如果是对面权重计算需要判断已有局面
        if ((chess[x][y].color > 0 && color == computerColor)
                || chess[x][y].color == computerColor) {
            return -1;
        }
        /*获取 横竖 阳线，左斜线 右斜线 阴线 四线权重*/
        int[] line = new int[4];
        line[0] = singleLine(x, y, color, 1, 0);
        line[1] = singleLine(x, y, color, 0, 1);
        line[2] = singleLine(x, y, color, 1, 1);
        line[3] = singleLine(x, y, color, -1, 1);
        int doubleLine = 0;
        int four = 0;
        for (int i = 0; i < line.length; i++) {
            int life = line[i] / 1000;
            int side = line[i] / 100 % 10;
            int jump = line[i] % 100;
            if (life > 4) {
                weight = STEP_KILL; //活5
                if (life == side || life == jump % 10) {
                    weight = STEP_DANGER; //冲5或跳5
                }
                return weight * ((chess[x][y].color > 0 &&
                        color != computerColor) ? -1 : 1);
            }
            if (life == 4) { //活4
                weight = STEP_FOUR;
                return weight * ((chess[x][y].color > 0 &&
                        color != computerColor) ? -1 : 1);
            }
            //活三，冲四 ，跳四及以上，活跳三
            if (life == 3 || side == 4 || jump % 10 >= 4 ||
                    (jump / 10 == 0 && jump % 10 == 3)) {
                doubleLine++;
                if (jump % 10 == 3) {
                    weight += 9000;
                } else {
                    weight += 10000;
                }
                if (side == 4 || jump % 10 >= 4) {
                    four++;
                }
                //活二，冲三，冲跳三
            } else if (life == 2 || side == 3 || jump % 10 == 3) {
                weight += 1000;
                //余下
            } else {
                weight = weight + life * 100 + side * 100 +
                        (jump % 10) * 10 - (jump / 10);
                if (x == 7 && y == 7) {
                    weight += 100; //中心点权重加100，AI开局就不会乱走
                }
            }
        }
        if (doubleLine > 1) { //双线成杀
            weight = STEP_SLAY;
        } else if (doubleLine == 1 && four > 0) { // 冲四或跳四
            weight = STEP_AT_FOUR;
        }
        return weight * ((chess[x][y].color > 0 &&
                color != computerColor) ? -1 : 1);
    }

    /*单线权重计算*/
    private int singleLine(int x, int y, int color, int px, int py) {
        int leftLive = oneSide(x, y, color, px, py, 0); //左边生存空间
        int rightLive = oneSide(x, y, color, -px, -py, 0); //右边生存空间
        if (leftLive + rightLive < 4) return 1;//左右生存空间少于4，此线无意义；
        int leftSame = oneSide(x, y, color, px, py, 1); //左边相邻连续同色
        int rightSame = oneSide(x, y, color, -px, -py, 1); //右边相邻连续同色
        int leftNSame = oneSide(x, y, color, px, py, 2); //左边不相邻（1个空位）连续同色
        int rightNSame = oneSide(x, y, color, -px, -py, 2); //右边不相邻（1个空位）连续同色
        int life = isLife(leftLive, leftSame, rightLive, rightSame);
        int side = isSide(leftLive, leftSame, rightLive, rightSame);
        int jump = isJump(leftLive, leftSame, leftNSame, rightLive, rightSame, rightNSame);
        return life * 1000 + side * 100 + jump; //千位为活子数，百位冲子数，十为标识个位的跳是否一边被拦
    }

    /*判断活几*/
    private int isLife(int ll, int ls, int rl, int rs) {
        int num = ls + rs + 1;
        if (num > 4 || (ll > ls && rl > rs)) {
            return num;
        }
        return 0;
    }

    /*判断冲几*/
    private int isSide(int ll, int ls, int rl, int rs) {
        if (ll == ls ^ rl == rs) {
            return ls + rs + 1;
        }
        return 0;
    }

    /*判断跳几*/
    private int isJump(int ll, int ls, int lns, int rl, int rs, int rns) {
        int sum = ls + rs + 1;
        int lj = sum + lns - ls;
        int rj = sum + rns - rs;
        lj = lj == sum ? 0 : lj; //分开活,冲和跳
        rj = rj == sum ? 0 : rj;
        int num = (lj > rj ? lj : rj);
        if (num >= 4) return num; //跳四以上不考虑冲
        if (num < 2) return 0; //跳2以下无意义
        int l_side = 0, r_side = 0;
        if ((lns + 1 >= ll) ^ (rl == rs)) {
            l_side = 1;//左冲
        }
        if ((rns + 1 >= rl) ^ (ll == ls)) {
            r_side = 1; //右冲
        }
        if (lj == num && l_side == 0) {
            return num;//活跳
        }
        if (rj == num && r_side == 0) {
            return num;//活跳
        }
        return 10 + num;//冲跳，十位标识有障碍形成冲
    }

    /*单线参数获取*/
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
