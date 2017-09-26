package cn.leo.fivechess.AI;

import cn.leo.fivechess.bean.Chess;

public class FiveChessAI_base implements AI_Interface {
    /**
     * 五子棋AI 基础框架。可以不用这套基础，只要你自己写得好
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
        Chess point = new Chess(); // 定义返回的对象
        int x = -1, y = -1; // 计算机要走的坐标
        int max = 0; // 最大权重
        calculateWeight(); // 计算权重

        /*TODO 根据己方和对方每个点权重计算要下子的位置 ，棋力关键点1，着重处理*/
        for (int i = 0; i < chess.length; i++) {
            for (int j = 0; j < chess[i].length; j++) {
                if (max <= ownWeight[i][j]) {
                    if (Math.random() * 100 < 33 && max == ownWeight[i][j]) { //权重相同加点随机事件
                        max = ownWeight[i][j]; // 获取最大权重
                        x = i; // 获取坐标
                        y = j;
                    }
                }
                if (max <= oppositeWeight[i][j]) {
                    if (Math.random() * 100 > 66 && max == oppositeWeight[i][j]) { //权重相同加点随机事件
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
        return "基础号引擎";
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
        if (chess[x][y].color > 0) { // 坐标处有子，没有权重
            return 0;
        }
        /*获取 横竖 阳线，左斜线 右斜线 阴线 四线权重*/
        int[] line = new int[4];
        line[0] = singleLine(x, y, color, 1, 0);
        line[1] = singleLine(x, y, color, 0, 1);
        line[2] = singleLine(x, y, color, 1, 1);
        line[3] = singleLine(x, y, color, -1, 1);

        /*TODO 根据四线权重计算总权重，形成33等权重另计但不能比自身5连权重高。棋力关键点2，着重处理*/

        return weight;
    }

    /*单线权重计算*/
    private int singleLine(int x, int y, int color, int px, int py) {
        int leftLive = oneSide(x, y, color, px, py, 0); //左边生存空间
        int rightLive = oneSide(x, y, color, -px, -py, 0); //右边生存空间

        if (leftLive + rightLive < 4) return 0;//左右生存空间少于4，此线无意义；

        int leftSpace = oneSide(x, y, 0, px, py, 1); //左边相邻空格数
        int rightSpace = oneSide(x, y, 0, -px, -py, 1); //右边相邻空格数

        int leftSame = oneSide(x, y, color, px, py, 1); //左边相邻连续同色
        int rightSame = oneSide(x, y, color, -px, -py, 1); //右边相邻连续同色

        int leftNSame = oneSide(x, y, color, px, py, 2); //左边不相邻（1个空位）连续同色
        int rightNSame = oneSide(x, y, color, -px, -py, 2); //右边不相邻（1个空位）连续同色

        /*TODO 根据上方8个条件计算一条线的权重 棋力关键点3，着重处理*/
        if (color == computerColor) { //己方
            //己方权重
        } else {//对方
            //对方权重
        }
        return 0;
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
     * @author 刘佳睿 这只是 查找一条线指定方向 的 棋子数 ，可以自己调教
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
