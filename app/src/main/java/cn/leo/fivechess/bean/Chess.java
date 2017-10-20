package cn.leo.fivechess.bean;

public class Chess implements Cloneable { // 单个棋子
    public int x, y; // 棋子坐标
    public int index; // 落子序号
    public int color = 0; // 棋子颜色 黑1 或 白2 ,0为空

    public Chess() {
    }

    public Chess(int x, int y, int index, int color) {
        this.x = x;
        this.y = y;
        this.index = index;
        this.color = color;
    }

    @Override
    public Chess clone() {
        return new Chess(x, y, index, color);
    }

    public void copy(Chess chess) {
        this.x = chess.x;
        this.y = chess.y;
        this.index = chess.index;
        this.color = chess.color;
    }
}
