package cn.leo.fivechess;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import cn.leo.fivechess.AI.FiveChessAI;
import cn.leo.fivechess.bean.Chess;
import cn.leo.fivechess.view.ChessBoard;

public class MainActivity extends AppCompatActivity implements ChessBoard.onChessDownListener {
    private ChessBoard mBoard;
    private FiveChessAI mAI = new FiveChessAI();

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
        mBoard.startGame();
    }

    @Override
    public void onChessDown(int x, int y) {
        mBoard.setChess(x, y, 1);
        AIgo();
    }

    private void AIgo() {
        Chess point = mAI.getPoint(mBoard.getChess(), 2);
        mBoard.setChess(point.x, point.y, 2);
    }

    @Override
    public void onGameOver(int winColor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(winColor == 1 ? "你赢了" : "你输了");
        builder.setMessage(winColor == 1 ? "恭喜，你赢了!" : "别气馁，加把劲!");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initData();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


}
