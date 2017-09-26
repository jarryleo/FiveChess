package cn.leo.fivechess;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cn.leo.fivechess.AI.AI_Interface;
import cn.leo.fivechess.AI.FiveChessAI_demo;
import cn.leo.fivechess.AI.FiveChessAI_lsw;
import cn.leo.fivechess.bean.Chess;
import cn.leo.fivechess.view.ChessBoard;

public class MainActivity extends AppCompatActivity implements ChessBoard.onChessDownListener {
    private static final int CHESS_MODE_HUMAN_VS_AI = 0; //人机模式
    private static final int CHESS_MODE_HUMAN_VS_HUMAN = 1;//人人模式
    private static final int CHESS_MODE_AI_VS_AI = 2;//机机模式
    private static final int FIRST_GO_HUMAN = 1;//人先走
    private static final int FIRST_GO_AI_A = 2;//AI_A先走
    private static final int FIRST_GO_AI_B = 3;//AI_B先走
    private static final int CHESS_COLOR_BLACK = 1;//黑子颜色
    private static final int CHESS_COLOR_WHITE = 2;//白子颜色

    private ChessBoard mBoard; //棋盘，兼裁判
    private TextView mTv_score_a; //计分a
    private TextView mTv_score_b; //计分b
    private int score_a;
    private int score_b;
    private int count;//下棋局数
    private boolean isGameOver; //当前棋局是否结束
    private AI_Interface mAI_A = new FiveChessAI_demo(); //TODO AI引擎1
    private AI_Interface mAI_B = new FiveChessAI_lsw();//TODO AI引擎2
    /*人机模式*/
    //private int mode = CHESS_MODE_HUMAN_VS_AI; //TODO 设置下棋模式 、改成人机模式自动对战10000次，计算最终比分
    //private int firstSide = FIRST_GO_HUMAN;//上面没有human，这里就不能写human
    private int humanColor = CHESS_COLOR_BLACK; //人类下子颜色
    /*AI对战*/
    private int mode = CHESS_MODE_AI_VS_AI;
    private int firstSide = FIRST_GO_AI_A;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }


    private void initView() {
        mBoard = (ChessBoard) findViewById(R.id.chess_bord);
        mTv_score_a = (TextView) findViewById(R.id.score_a);
        mTv_score_b = (TextView) findViewById(R.id.score_b);
        mBoard.setOnChessDownListener(this);
    }

    private void initData() {
        mBoard.startGame();
        if (mode == CHESS_MODE_AI_VS_AI) {
            if (firstSide == FIRST_GO_AI_A) { //如果机器先走
                AI_A_go(CHESS_COLOR_BLACK); //黑子先走
            } else if (firstSide == FIRST_GO_AI_B) { //如果机器先走
                AI_B_go(CHESS_COLOR_BLACK); //黑子先走
            }
        } else if (mode == CHESS_MODE_HUMAN_VS_AI) {//人机模式
            if (firstSide == FIRST_GO_AI_A) { //如果机器先走
                AI_A_go(3 - humanColor);
            } else if (firstSide == FIRST_GO_AI_B) { //如果机器先走
                AI_B_go(3 - humanColor);
            }
        }
    }

    @Override
    public void onChessDown(int x, int y) {
        if (isGameOver) {
            isGameOver = false;
            initData();
        } else {
            afterHumanGo(x, y);
        }
    }

    /*人类手指点击棋盘后的动作*/
    private void afterHumanGo(int x, int y) {
        mBoard.setChess(x, y, humanColor);
        if (mode == CHESS_MODE_HUMAN_VS_AI) {
            AI_A_go(3 - humanColor);
            //AI_B_go(3 - humanColor);
        } else if (mode == CHESS_MODE_HUMAN_VS_HUMAN) {
            mBoard.setLock(false);
        } else if (mode == CHESS_MODE_AI_VS_AI) {
            mBoard.setLock(true);
        }
    }

    /*一个AI走子*/
    private void AI_A_go(int color) {
        Chess point = mAI_A.AIGo(mBoard.getChess(), color);
        mBoard.setChess(point.x, point.y, color);
        if (mode == CHESS_MODE_AI_VS_AI) {
            AI_B_go(3 - color);
        }
    }

    /*另一个AI走子*/
    private void AI_B_go(int color) {
        Chess point = mAI_B.AIGo(mBoard.getChess(), color);
        mBoard.setChess(point.x, point.y, color);
        if (mode == CHESS_MODE_AI_VS_AI) {
            AI_A_go(3 - color);
        }
    }

    /*棋局结束*/
    @Override
    public void onGameOver(int winColor) {
        count++;
        if (mode == CHESS_MODE_HUMAN_VS_AI) { //人机
            if (winColor == humanColor) {
                score_a++;
            } else {
                score_b++;
            }
            mTv_score_a.setText("人类：" + score_a);
            mTv_score_a.setText("AI：" + score_b);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(winColor == humanColor ? "你赢了" : "你输了");
            builder.setMessage(winColor == humanColor ? "恭喜，你赢了!" : "别气馁，加把劲!");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isGameOver = true;
                }
            });
            builder.setCancelable(false);
            builder.show();
        } else if (mode == CHESS_MODE_AI_VS_AI) { //AI  VS  AI
            if (winColor == CHESS_COLOR_BLACK) { //先手赢
                if (firstSide == FIRST_GO_AI_A) {
                    score_a++;
                } else {
                    score_b++;
                }
            } else {
                if (firstSide == FIRST_GO_AI_A) {
                    score_b++;
                } else {
                    score_a++;
                }
            }
            mTv_score_a.setText("AI：" + mAI_A.getAIName() + "胜利次数：" + score_a);
            mTv_score_a.setText("AI：" + mAI_B.getAIName() + "胜利次数：" + score_b);
            if (count < 10000) { //下10000局
                if (firstSide == FIRST_GO_AI_A) { //交换先手
                    firstSide = FIRST_GO_AI_B;
                } else {
                    firstSide = FIRST_GO_AI_A;
                }
                initData();
            }
        } else {//人类 对 人类
            if (winColor == CHESS_COLOR_BLACK) {
                score_a++;
            } else {
                score_b++;
            }

            mTv_score_a.setText("黑方：" + score_a);
            mTv_score_a.setText("白方：" + score_b);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(winColor == CHESS_COLOR_BLACK ? "黑子胜" : "白子胜");
            builder.setMessage(winColor == CHESS_COLOR_BLACK ? "黑子胜" : "白子胜");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isGameOver = true;
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }
}
