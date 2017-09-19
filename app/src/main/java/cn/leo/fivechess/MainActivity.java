package cn.leo.fivechess;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import cn.leo.fivechess.AI.FiveChessAI;
import cn.leo.fivechess.bean.Chess;
import cn.leo.fivechess.view.ChessBoard;

public class MainActivity extends AppCompatActivity implements ChessBoard.onChessDownListener {
    Chess mChess[][] = new Chess[15][15];
    boolean blackTurn;
    private int index;
    private ChessBoard mBoard;
    private FiveChessAI mAI = new FiveChessAI();
    private int lastX;
    private int lastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }


    private void initView() {
        mBoard = (ChessBoard) findViewById(R.id.chess_bord);
        mBoard.setOnChessDownListener(this);
    }

    private void initData() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                mChess[i][j] = new Chess(); // 加载棋子对象
            }
        }
        blackTurn = true;
        index = 1;
        mBoard.setChess(mChess, index);
    }

    @Override
    public void onChessDown(int x, int y) {
        if (blackTurn) {
            Chess chess = mChess[x][y];
            if (chess.color == 0) {
                chess.color = 1;
                chess.x = x;
                chess.y = y;
                lastX = x;
                lastY = y;
                chess.index = index;
                mBoard.setChess(mChess, index);
                index++;

                if (isFive()) {
                    gameOver();
                } else {
                    blackTurn = false;
                    AIgo();
                }

            }
        }
    }

    private void AIgo() {
        Chess point = mAI.getPoint(mChess, 2);
        point.index = index;
        lastX = point.x;
        lastY = point.y;
        mChess[point.x][point.y] = point;
        mBoard.setChess(mChess, index);
        index++;
        if (isFive()) {
            gameOver();
        } else {
            blackTurn = true;
        }

    }

    private void gameOver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(blackTurn ? "你赢了" : "你输了");
        builder.setMessage(blackTurn ? "恭喜，你赢了!" : "别气馁，加把劲!");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                initData();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private boolean isFive() {
        // 判断是否五子及以上连线
        return (sameLine(1, 0) > 4 || sameLine(0, 1) > 4 || sameLine(1, 1) > 4
                || sameLine(-1, 1) > 4 || index == 225);
    }


    private int sameLine(int x, int y) { // 判断一条线有多少个同色子
        int num = 0; // 同一直线同色棋子数
        int i = lastX;
        int j = lastY;
        do { //检测直线一边同色棋子数
            if (mChess[i][j].color != mChess[lastX][lastY].color) {
                break;
            }
            i += x;
            j += y;
        } while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测
        num = Math.max(Math.abs(lastX - i), Math.abs(lastY - j));

        i = lastX;
        j = lastY;
        do { //检测直线另一边同色棋子数
            if (mChess[i][j].color != mChess[lastX][lastY].color) {
                break;
            }
            i -= x;
            j -= y;
        } while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测
        num = --num + Math.max(Math.abs(lastX - i), Math.abs(lastY - j));
        return num;
    }
}
