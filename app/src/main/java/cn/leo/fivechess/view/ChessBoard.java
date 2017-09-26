package cn.leo.fivechess.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import cn.leo.fivechess.bean.Chess;

/**
 * Created by JarryLeo on 2017/4/29.
 */

public class ChessBoard extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 棋盘大小，15*15 五子棋 / 19*19 围棋
     */
    private final static int mLines = 15; //格子数
    private Chess[][] mChess = new Chess[15][15];//棋子
    private onChessDownListener mChessDownLister;//落子监听
    private float mDistance;//格子间距
    private float mOldDist;//双指之间上次距离
    private int mMode;//手指数
    private float mZoom = 1.0f;//缩放倍数
    private int mLength;//棋盘宽
    private float downX;//手指落点X
    private float downY;//手指落点Y
    private boolean mLocked;//是否锁定滑动。缩放时不滑动
    private boolean mScroll;//是否在滑动
    private float mTextSize;
    private long mScrollTime;
    private boolean mAfterZoom;
    private int mIndex;//落子序号
    private int lastX;//最后落子坐标X
    private int lastY;//最后落子坐标Y
    private int lastColor;//最后落子颜色
    private boolean lock;//是否锁定人类下子
    private boolean isGameOver;

    public ChessBoard(Context context) {
        this(context, null);
    }

    public ChessBoard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChessBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeWidth(2.0f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        mLength = Math.min(widthPixels, heightPixels);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mLength, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mDistance = mLength / (mLines + 1);
        //绘制棋盘
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        for (int i = 0; i < mLines; i++) {
            canvas.drawLine(mDistance,
                    (i + 1) * mDistance,
                    mLength - mDistance,
                    (i + 1) * mDistance, mPaint); // 画棋盘格子
            canvas.drawLine((i + 1) * mDistance,
                    mDistance,
                    (i + 1) * mDistance,
                    mLength - mDistance, mPaint);
            if (i == 3 || i == 7 || i == 11) { // 画星位。这是五子棋的星位
                canvas.drawCircle((i + 1) * mDistance, (i + 1) * mDistance, 5, mPaint);
                canvas.drawCircle((i + 1) * mDistance, (mLines - i) * mDistance, 5, mPaint);
            }
        }
        if (mChess != null) {
            drawChess(canvas);
        }

    }

    private void drawChess(Canvas canvas) {
        //绘制棋子
        for (int i = 0; i < mChess.length; i++) {
            for (int j = 0; j < mChess[i].length; j++) {
                if (mChess[i][j].color == 0) continue;
                if (mChess[i][j].color == 1) {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(Color.BLACK); // 画黑子
                    canvas.drawCircle((mChess[i][j].x + 1) * mDistance,
                            (mChess[i][j].y + 1) * mDistance, mDistance / 2.5f, mPaint);
                    mPaint.setColor(Color.WHITE); // 画编号
                    canvas.drawText(mChess[i][j].index + "", (mChess[i][j].x + 1) * mDistance
                            , (mChess[i][j].y + 1) * mDistance + (mTextSize / 3), mPaint);
                } else if (mChess[i][j].color == 2) {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(Color.WHITE); // 画白子
                    canvas.drawCircle((mChess[i][j].x + 1) * mDistance, (mChess[i][j].y + 1) * mDistance,
                            mDistance / 2.5f, mPaint);
                    mPaint.setColor(Color.BLACK); // 画编号
                    canvas.drawText(mChess[i][j].index + "", (mChess[i][j].x + 1) * mDistance
                            , (mChess[i][j].y + 1) * mDistance + (mTextSize / 3), mPaint);
                }
                if (mChess[i][j].index == mIndex) { // 画出最后落子位置
                    mPaint.setColor(Color.RED);
                    mPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle((mChess[i][j].x + 1) * mDistance, (mChess[i][j].y + 1) * mDistance,
                            mDistance / 2.5f, mPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAfterZoom) {
            mOldDist = spacing(event);
            mAfterZoom = false;
            return true;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                //单指按下
                mMode = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mMode >= 2)
                    mOldDist = spacing(event);
                mMode += 1;
                mLocked = true;
                //多指按下
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mMode -= 1;
                //多指抬起
                break;
            case MotionEvent.ACTION_MOVE:
                //滑动
                if (mMode >= 2) {
                    //双指缩放
                    float newDist = spacing(event);
                    if (newDist > mOldDist + 10) {
                        zoomOut();
                        mAfterZoom = true;
                    } else if (newDist < mOldDist - 10) {
                        zoomIn();
                        mAfterZoom = true;
                    }
                }
                if (mMode == 1 && !mLocked) {
                    //拖动界面
                    if (Math.abs(downX - event.getX()) > 10 &&
                            Math.abs(downY - event.getY()) > 10) {
                        mScroll = true;
                        if (SystemClock.uptimeMillis() - mScrollTime > 16) {
                            int left = (int) (event.getX() - downX + getX());
                            int top = (int) (event.getY() - downY + getY());
                            setX(left);
                            setY(top);
                            mScrollTime = SystemClock.uptimeMillis();
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                mMode = 0;
                mLocked = false;

                //单指抬起
                if (!mScroll && Math.abs(downX - event.getX()) < 10 &&
                        Math.abs(downY - event.getY()) < 10) {
                    //落子
                    int x = (int) ((event.getX() + 0.5 * mDistance) / mDistance) - 1;
                    int y = (int) ((event.getY() + 0.5 * mDistance) / mDistance) - 1;
                    if (mChessDownLister != null && !lock && mChess[x][y].color == 0) {
                        mChessDownLister.onChessDown(x, y);
                    }
                }
                mScroll = false;
                break;
        }
        return true;
    }

    private void zoomIn() {
        //缩小
        mZoom -= 0.05f;
        if (mZoom < 1.0f) {
            mZoom = 1.0f;
        }
        setScaleX(mZoom);
        setScaleY(mZoom);
    }

    private void zoomOut() {
        //放大
        mZoom += 0.05f;
        if (mZoom > 2.0f) {
            mZoom = 2.0f;
        }
        setScaleX(mZoom);
        setScaleY(mZoom);
    }

    /*计算两指距离*/
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() < 2) return 0;
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public interface onChessDownListener {
        void onChessDown(int x, int y);

        void onGameOver(int winColor);
    }

    /*开始游戏*/
    public void startGame() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (mChess[i][j] == null) {
                    mChess[i][j] = new Chess(); // 加载棋子对象
                } else {
                    mChess[i][j].color = 0;
                }
            }
        }
        mLocked = false;
        isGameOver = false;
        mIndex = 0;
        lastColor = 0;
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();//重绘
            }
        });
    }

    /*设置监听*/
    public void setOnChessDownListener(onChessDownListener listener) {
        mChessDownLister = listener;
    }

    public Chess[][] getChess() {
        return mChess;
    }

    /*落子*/
    public void setChess(int x, int y, int color) {
        if (isGameOver) return;
        Log.e("落子", "x=" + x + " y=" + y + " color=" + color);
        mIndex++;
        lastX = x;
        lastY = y;
        lastColor = color;
        mChess[x][y].index = mIndex;
        mChess[x][y].color = color;
        mChess[x][y].x = x;
        mChess[x][y].y = y;
        lock = !lock;
        if (mChessDownLister != null && isFive()) {
            isGameOver = true;
            mChessDownLister.onGameOver(lastColor);
        }
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });

    }

    /*锁定人类走子*/
    public void setLock(boolean lock) {
        this.lock = lock;
    }

    private boolean isFive() {
        // 判断是否五子及以上连线
        return (sameLine(1, 0) > 4 || sameLine(0, 1) > 4 || sameLine(1, 1) > 4
                || sameLine(-1, 1) > 4 || mIndex == 225);
    }


    private int sameLine(int x, int y) { // 判断一条线有多少个同色子
        int num; // 同一直线同色棋子数
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
