package cn.leo.fivechess;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.leo.fivechess.AI.AI_Interface;
import cn.leo.fivechess.AI.FiveChessAI_demo;
import cn.leo.fivechess.AI.FiveChessAI_leo;
import cn.leo.fivechess.AI.FiveChessAI_lsw;
import cn.leo.fivechess.bean.Chess;
import cn.leo.fivechess.view.ChessBoard;

public class MainActivity extends AppCompatActivity implements ChessBoard.onChessDownListener, View.OnClickListener {
    private static final int CHESS_MODE_HUMAN_VS_AI = 0; //人机模式
    private static final int CHESS_MODE_HUMAN_VS_HUMAN = 1;//人人模式
    private static final int CHESS_MODE_AI_VS_AI = 2;//机机模式
    private static final int FIRST_GO_HUMAN = 1;//人先走
    private static final int FIRST_GO_AI_A = 2;//AI_A先走
    private static final int FIRST_GO_AI_B = 3;//AI_B先走
    private static final int CHESS_COLOR_BLACK = 1;//黑子颜色
    private static final int CHESS_COLOR_WHITE = 2;//白子颜色
    private Handler mHandler;
    private ChessBoard mBoard; //棋盘，兼裁判
    private TextView mTv_score_a; //计分a
    private TextView mTv_score_b; //计分b
    private int score_a;
    private int score_b;
    private int count;//下棋局数
    private boolean isGameOver; //当前棋局是否结束
    private int humanColor = CHESS_COLOR_BLACK; //人类下子颜色
    private AI_Interface mAI_A = new FiveChessAI_leo(); //TODO AI引擎1
    //    private AI_Interface mAI_B = new FiveChessAI_demo(); //TODO 初级引擎
    private AI_Interface mAI_B = new FiveChessAI_lsw();//TODO AI引擎2
    /*人机模式*/
//    private int mode = CHESS_MODE_HUMAN_VS_AI; //TODO 设置下棋模式 、改成人机模式自动对战10000次，计算最终比分
//    private int firstSide = FIRST_GO_HUMAN;//上面没有human，这里就不能写human
    /*AI对战*/
    private int mode = CHESS_MODE_AI_VS_AI;
    private int firstSide = FIRST_GO_AI_A;

    private boolean auto = false;//自动
    private boolean justOne = false;
    private int turn = firstSide;//轮换
    private Button mBtnStart;
    private Button mBtnNextRound;


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
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_pre).setOnClickListener(this);
        mBtnNextRound = (Button) findViewById(R.id.btn_next_round);
        mBtnNextRound.setOnClickListener(this);

        mBoard.setOnChessDownListener(this);
        //开一个子线程执行的handler
        HandlerThread handlerThread = new HandlerThread("AI");
        handlerThread.start();
        //handler的handleMessage在上面的线程中执行
        mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FIRST_GO_AI_A:
                        AI_A_go(msg.arg1, msg.arg2);
                        break;
                    case FIRST_GO_AI_B:
                        AI_B_go(msg.arg1, msg.arg2);
                        break;
                }
            }
        };
    }

    private void initData() {
        freshScore();
        mBoard.startGame();
        if (mode == CHESS_MODE_AI_VS_AI) {
            mBoard.setLock(true);
            AIFight();
        } else if (mode == CHESS_MODE_HUMAN_VS_AI) {//人机模式
            if (firstSide == FIRST_GO_AI_A) { //如果机器先走
                AI_A_go(3 - humanColor, 1);
            } else if (firstSide == FIRST_GO_AI_B) { //如果机器先走
                AI_B_go(3 - humanColor, 1);
            }
        }
    }

    private void AIFight() {
        if (!auto) return;
        if (firstSide == FIRST_GO_AI_A) { //如果机器A先走
            mHandler.obtainMessage(FIRST_GO_AI_A, CHESS_COLOR_BLACK, 0).sendToTarget();
        } else if (firstSide == FIRST_GO_AI_B) { //如果机器B先走
            mHandler.obtainMessage(FIRST_GO_AI_B, CHESS_COLOR_BLACK, 0).sendToTarget();
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
            mHandler.obtainMessage(FIRST_GO_AI_A, 3 - humanColor, 1).sendToTarget();
        } else if (mode == CHESS_MODE_HUMAN_VS_HUMAN) {
            mBoard.setLock(false);
        } else if (mode == CHESS_MODE_AI_VS_AI) {
            mBoard.setLock(true);
        }
    }

    /*一个AI走子*/
    private void AI_A_go(int color, int refresh) {
        Chess point = mAI_A.AIGo(mBoard.getChess(), color);
        boolean down = mBoard.setChess(point.x, point.y, color, refresh == 1);
        if (down && mode == CHESS_MODE_AI_VS_AI) {
            //AI_B_go(3 - color);
            if (auto) {
                mHandler.obtainMessage(FIRST_GO_AI_B, 3 - color, 0).sendToTarget();
                turn = FIRST_GO_AI_A;
            }
        }
    }

    /*另一个AI走子*/
    private void AI_B_go(int color, int refresh) {
        Chess point = mAI_B.AIGo(mBoard.getChess(), color);
        boolean down = mBoard.setChess(point.x, point.y, color, refresh == 1);
        if (down && mode == CHESS_MODE_AI_VS_AI) {
            //AI_A_go(3 - color);
            if (auto) {
                mHandler.obtainMessage(FIRST_GO_AI_A, 3 - color, 0).sendToTarget();
                turn = FIRST_GO_AI_B;
            }
        }
    }

    /*棋局结束*/
    @Override
    public void onGameOver(int winColor) {
        if (winColor == 0) {
            Toast.makeText(this, "和棋！", Toast.LENGTH_SHORT).show();
        }
        count++;
        if (mode == CHESS_MODE_HUMAN_VS_AI) { //人机
            if (winColor == humanColor) {
                score_a++;
            } else if (winColor == 3 - humanColor) {
                score_b++;
            }
            freshScore();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(winColor == humanColor ? "你赢了" : "你输了");
            builder.setMessage(winColor == humanColor ? "恭喜，你赢了!" : "别气馁，加把劲!");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isGameOver = true;
                    mBoard.setLock(false);
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
            } else if (winColor == CHESS_COLOR_WHITE) {
                if (firstSide == FIRST_GO_AI_A) {
                    score_b++;
                } else {
                    score_a++;
                }
            }
            freshScore();
            if (count < 100) { //下10000局
                isGameOver = true;
                auto = false;
                mBoard.refreshUI();
                if (justOne) return;
                mBtnStart.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoStart();
                    }
                }, 1000);
            }
        } else if (mode == CHESS_MODE_HUMAN_VS_HUMAN) {//人类 对 人类
            if (winColor == CHESS_COLOR_BLACK) {
                score_a++;
            } else if (winColor == CHESS_COLOR_WHITE) {
                score_b++;
            }
            freshScore();
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

    private void freshScore() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mode == CHESS_MODE_HUMAN_VS_AI) { //人机
                    mTv_score_a.setText("(" + (humanColor == CHESS_COLOR_BLACK ? "黑" : "白") + ")人类：" + score_a);
                    mTv_score_b.setText("(" + (humanColor == CHESS_COLOR_BLACK ? "白" : "黑") + ")AI：" + score_b);
                } else if (mode == CHESS_MODE_AI_VS_AI) { //AI  VS  AI
                    mTv_score_a.setText("(" + (firstSide == FIRST_GO_AI_A ? "黑" : "白") + ")"
                            + mAI_A.getAIName() + ":" + score_a);
                    mTv_score_b.setText("(" + (firstSide == FIRST_GO_AI_B ? "黑" : "白") + ")"
                            + mAI_B.getAIName() + ":" + score_b);
                    mBtnStart.setText(auto ? "暂停" : "开始");
                    mBtnStart.setEnabled(!auto);
                    mBtnNextRound.setEnabled(!auto);
                } else if (mode == CHESS_MODE_HUMAN_VS_HUMAN) {//人类 对 人类
                    mTv_score_a.setText("黑方：" + score_a);
                    mTv_score_b.setText("白方：" + score_b);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start:
                justOne = false;
                autoStart();
                break;
            case R.id.btn_pre:
                isGameOver = false;
                mBoard.back();
                turn = 5 - turn;
                break;
            case R.id.btn_next:
//                boolean next = mBoard.next();
//                if (next) return;
                if (isGameOver) return;
                mHandler.obtainMessage(turn, 3 - mBoard.getLastColor(), 1).sendToTarget();
                turn = 5 - turn;
                break;
            case R.id.btn_next_round:
                isGameOver = true;
                justOne = true;
                autoStart();
//                mHandler.obtainMessage(turn, 3 - mBoard.getLastColor(), 0).sendToTarget();
//                turn = 5 - turn;
                break;
        }
    }

    //自动下一局
    private void autoStart() {
        if (auto) return;
        auto = true;
        mBtnStart.setEnabled(!auto);
        mBtnNextRound.setEnabled(!auto);
        if (auto) {
            if (isGameOver) {
                if (firstSide == FIRST_GO_AI_A) { //交换先手
                    firstSide = FIRST_GO_AI_B;
                } else {
                    firstSide = FIRST_GO_AI_A;
                }
                turn = firstSide;//轮换
                mBoard.startGame();
            }
            mHandler.obtainMessage(turn, 3 - mBoard.getLastColor(), 0).sendToTarget();
            turn = 5 - turn;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}
