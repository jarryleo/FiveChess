package cn.leo.fivechess.AI;

import android.util.Log;

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
    Chess chess[][]; // 接受棋盘的所有棋子
    private int ownWeight[][] = new int[15][15]; // 己方每个点权重
    private int oppositeWeight[][] = new int[15][15]; // 对方每个点权重
    private int computerColor; // 电脑要走的棋子颜色 黑1 或 白2 ,0为空
    private int chessCount;
    private boolean isSon;
    private FiveChessAI_leo son_A, son_B;

    /*创建2个自己的副本，用于自我对弈*/
    public FiveChessAI_leo() {
        son_A = new FiveChessAI_leo(true);
        son_B = new FiveChessAI_leo(true);
    }

    /*自己的副本构造*/
    private FiveChessAI_leo(boolean isSon) {
        this.isSon = isSon;
    }

    @Override
    public Chess AIGo(Chess chess[][], int color) { // 返回计算机落子
        this.chess = chess;
        this.computerColor = color;
        this.chessCount = 0;
        calculateWeight(); // 计算权重
        Chess point = new Chess(); // 定义返回的对象
        point.color = computerColor; //返回的颜色必然是自己要走的颜色
        point.x = -1;
        point.y = -1;
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
//                    if (!isSon) Log.e("own", "AIGo: weight=" + ownMax + ",X=" + i + ",Y=" + j);
                } else if (ownMax == ownWeight[i][j]) {
                    if (Math.random() * 100 > 50) { //权重相同加点随机事件
                        ownMax = ownWeight[i][j]; // 获取己方最大权重
                        x1 = i; // 获取坐标
                        y1 = j;
//                        if (!isSon) Log.e("own", "AIGo: weight=" + ownMax + ",X=" + i + ",Y=" + j);
                    }
                }
                //己方已经四连，直接落子
                if (ownMax == STEP_KILL || ownMax == STEP_DANGER) {
                    point.x = x1;
                    point.y = y1;
                    point.index = ownMax;
                    return point;
                }
                /*处理对方权重*/
                if (oppositeMax < oppositeWeight[i][j]) {
                    oppositeMax = oppositeWeight[i][j]; // 获取对方最大权重
                    x2 = i; // 获取坐标
                    y2 = j;
//                    if (!isSon) Log.e("opposite", "AIGo: weight=" + ownMax + ",X=" + i + ",Y=" + j);
                } else if (oppositeMax == oppositeWeight[i][j]) {
                    if (Math.random() * 100 > 50) { //权重相同加点随机事件
                        oppositeMax = oppositeWeight[i][j]; // 获取对方最大权重
                        x2 = i; // 获取坐标
                        y2 = j;
//                        if (!isSon)
//                            Log.e("opposite", "AIGo: weight=" + ownMax + ",X=" + i + ",Y=" + j);
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
        if (ownMax < 10 && oppositeMax < 10) return point;//和棋
        /*开始根据权重分析形势*/
        //对方已经双线成杀，不拦截，全力冲四跳四
        if (oppositeMin == -STEP_SLAY) {
            for (int i = 0; i < chess.length; i++) {
                for (int j = 0; j < chess[i].length; j++) {
                    if (ownWeight[i][j] == STEP_AT_FOUR ||
                            ownWeight[i][j] == STEP_FOUR) {
                        point.x = i;
                        point.y = j;
                        point.index = ownWeight[i][j];
                        return point;
                    }
                }
            }
        }
        if (isSon) {
            //对方将要5连但是可以拦截
            if (oppositeMax == STEP_DANGER) {
                point.x = x2;
                point.y = y2;
                point.index = oppositeMax;
                return point;
            }
            //己方将要双线成杀 或 将活四
            if (ownMax == STEP_SLAY ||
                    ownMax == STEP_FOUR) {
                point.x = x1;
                point.y = y1;
                point.index = ownMax;
                return point;
            }
            //对面将活四 或 将双线成杀
            if (oppositeMax == STEP_FOUR ||
                    oppositeMax == STEP_SLAY) {
                point.x = x2;
                point.y = y2;
                point.index = oppositeMax;
                return point;
            }
        }
        //如果不是自己的副本,且下子总数已达到5个，则开始自我模拟对弈
        if (!isSon && chessCount > 5) {
            //1、找出自己权重需要模拟对弈的几个点
            for (int i = 0; i < chess.length; i++) {
                for (int j = 0; j < chess[i].length; j++) {
                    //拷贝棋局，用来模拟对弈
                    Chess[][] chessCopy = new Chess[15][15];
                    for (int e = 0; e < chess.length; e++) {
                        for (int r = 0; r < chess[e].length; r++) {
                            chessCopy[e][r] = chess[e][r].clone();
                        }
                    }
                    if (ownWeight[i][j] + oppositeWeight[i][j] > 10000) {
                        //2、模拟下子找出每个点的胜率
                        chessCopy[i][j].color = computerColor;//模拟落子
                        for (int k = chessCount + 1; k < 225; k++) {
                            Chess chess1 = son_A.AIGo(chessCopy, 3 - computerColor);//A副本走对方棋子
                            if (chess1.x < 0 || chess1.y < 0) {
                                //A认为和棋
                                break;
                            } else if (chess1.index == STEP_KILL || chess1.index == STEP_DANGER) {
                                //A赢了
                                ownWeight[i][j] -= chess1.index;
                                Log.w("模拟对弈败局", "AIGo: X=" + i + ",Y=" + j);
                                break;
                            } else {
                                //A走的子在模拟棋盘落子
                                chessCopy[chess1.x][chess1.y].color = chess1.color;
                            }
                            Chess chess2 = son_B.AIGo(chessCopy, computerColor);//B副本走电脑的棋
                            if (chess2.x < 0 || chess2.y < 0) {
                                //B认为和棋
                                break;
                            } else if (chess2.index == STEP_KILL || chess2.index == STEP_DANGER) {
                                //B赢了
                                //3、选择胜率最大的点传给父类
                                ownWeight[i][j] += chess2.index;
                                Log.w("模拟对弈发现胜局", "AIGo: X=" + i + ",Y=" + j);
                                break;
                            } else {
                                //B走的子在模拟棋盘落子
                                chessCopy[chess2.x][chess2.y].color = chess2.color;
                            }
                        }
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
        }
        //剩下走双方权重相合最大的点
        point.x = x3;
        point.y = y3;
        point.index = sumMax;
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
                ownWeight[i][j] = weightSum(i, j, computerColor, true);
                oppositeWeight[i][j] = weightSum(i, j, 3 - computerColor, true);
            }
        }
    }

    /*分析一个点所能形成的局势，用权重表示*/
    private int weightSum(int x, int y, int color, boolean ignoreFour) {
        int weight = 0; // 总权重
        // 如果坐标处有子，则没有权重,如果是对面权重计算需要判断已有局面
        if (chess[x][y].color > 0) {
            if (color == computerColor) chessCount++;
            if (color == computerColor ||
                    chess[x][y].color == computerColor) {
                return -1;
            }
        }
        /*获取 横竖 阳线，左斜线 右斜线 阴线 四线权重*/
        int[] line = new int[4];
        line[0] = singleLine(x, y, color, 1, 0);
        line[1] = singleLine(x, y, color, 0, 1);
        line[2] = singleLine(x, y, color, 1, 1);
        line[3] = singleLine(x, y, color, -1, 1);
        int doubleLine = 0; //成双线杀的条件，大于等于2表示成杀
        int four = 0;//活四及以上或跳四及以上个数
        int op = chess[x][y].color > 0 ? -1 : 1;//已有子的坐标系数为-1
        for (int i = 0; i < line.length; i++) {
            int life = line[i] / 1000;
            int side = line[i] / 100 % 10;
            int jump = line[i] % 100;
            //活5
            if (life > 4) {
                weight = STEP_KILL;
                //冲5或跳5
                if (side > 4 || jump % 10 > 4) {
                    weight = STEP_DANGER;
                }
                return weight * op;
            }
            //活4
            if (life == 4) {
                weight = STEP_FOUR;
                return weight * op;
            }
            //活三，冲四 ，跳四及以上，活跳三
            if (life == 3 || side == 4 || jump % 10 >= 4 ||
                    (jump / 10 == 0 && jump % 10 == 3)) {
                doubleLine++; //满足双杀条件
                if (jump % 10 >= 3) { //跳权重少点
                    weight += 8000;
                } else {
                    weight += 10000;
                }
                //冲四跳四
                if (side >= 4 || jump % 10 >= 4) {
                    four++;
                }
                //活二，冲三，冲跳三
            } else if (life == 2 || side == 3 || jump % 10 == 3) {
                weight += 1000;
                //余下权重计算
            } else {
                weight = weight + life * 100 + side * 100 +
                        (jump % 10) * 10 - (jump / 10);
                //中心点权重加100，AI开局就不会乱走
                if (x == 7 && y == 7) {
                    weight += 100;
                }
            }
        }
        if (doubleLine > 1) {
            weight = STEP_SLAY;//双线成杀
        } else if (doubleLine == 1 && four > 0 && ignoreFour) {
            weight = STEP_AT_FOUR;// 冲四或跳四
        }
        return weight * op;
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
