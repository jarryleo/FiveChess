package cn.leo.fivechess.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.leo.fivechess.bean.Chess;

/**
 * Created by JarryLeo on 2017/4/29.
 */

public class ChessBoard extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 棋盘大小，15*15，五子棋 19*19 围棋
     */
    private int mLines = 15;
    private Chess[][] mChess;
    private onChessDownListener mChessDownLister;
    private float mDistance;
    private float mOldDist;
    private int mMode;
    private float mZoom = 1.0f;
    private int mLength;
    private float downX;
    private float downY;
    private boolean mLocked;
    private boolean mScroll;
    private int mIndex;

    public ChessBoard(Context context) {
        this(context, null);
    }

    public ChessBoard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChessBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setTextSize(32);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        mLength = Math.min(widthPixels, heightPixels);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mLength, MeasureSpec.EXACTLY);
        heightMeasureSpec = widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

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
                    mLength - mDistance - mLines / 2,
                    (i + 1) * mDistance, mPaint); // 画棋盘格子
            canvas.drawLine((i + 1) * mDistance,
                    mDistance,
                    (i + 1) * mDistance,
                    mLength - mDistance - mLines / 2, mPaint);
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
                if (mChess[i][j].color == 1) {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(Color.BLACK); // 画黑子
                    canvas.drawCircle((mChess[i][j].x + 1) * mDistance,
                            (mChess[i][j].y + 1) * mDistance, mDistance / 2.5f, mPaint);
                    mPaint.setColor(Color.WHITE); // 画编号
                    canvas.drawText(mChess[i][j].index + "", (mChess[i][j].x + 1) * mDistance
                            , (mChess[i][j].y + 1) * mDistance + 12, mPaint);
                } else if (mChess[i][j].color == 2) {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(Color.WHITE); // 画白子
                    canvas.drawCircle((mChess[i][j].x + 1) * mDistance, (mChess[i][j].y + 1) * mDistance,
                            mDistance / 2.5f, mPaint);
                    mPaint.setColor(Color.BLACK); // 画编号
                    canvas.drawText(mChess[i][j].index + "", (mChess[i][j].x + 1) * mDistance
                            , (mChess[i][j].y + 1) * mDistance + 12, mPaint);
                }
                if (mChess[i][j].color != 0 && mChess[i][j].index == mIndex) { // 画出最后落子位置
                    mPaint.setColor(Color.RED);
                    mPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle((mChess[i][j].x + 1) * mDistance, (mChess[i][j].y + 1) * mDistance,
                            mDistance / 2.5f, mPaint);
                }
            }
        }
    }


    public void setChess(Chess[][] chess, int index) {
        mIndex = index;
        mChess = chess;
        invalidate();//重绘

    }

    public void setOnChessDownListener(onChessDownListener listener) {
        mChessDownLister = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                //单指按下
                mMode = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
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
                        zoomOut(newDist - mOldDist);
                    }
                    if (newDist < mOldDist - 10) {
                        zoomIn(mOldDist - newDist);
                    }
                    mOldDist = newDist;
                }
                if (mMode == 1 && !mLocked) {
                    //拖动界面

                    if (Math.abs(downX - event.getX()) > 10 &&
                            Math.abs(downY - event.getY()) > 10) {
                        mScroll = true;
                        if (mZoom > 1.0f) {
                            int left = (int) (event.getX() - downX) + getLeft();
                            int top = (int) (event.getY() - downY) + getTop();
                            setLeft(left);
                            setTop(top);
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
                    if (mChessDownLister != null) {
                        mChessDownLister.onChessDown(x, y);
                    }
                }
                mScroll = false;
                break;
        }
        return true;
    }

    private void zoomIn(float dist) {
        //缩小
        mZoom -= 0.05f;
        if (mZoom < 1.0f) {
            mZoom = 1.0f;
            requestLayout();
        }
        setScaleX(mZoom);
        setScaleY(mZoom);
    }

    private void zoomOut(float dist) {
        //放大
        mZoom += 0.05f;
        if (mZoom > 2.0f) {
            mZoom = 2.0f;
        }
        setScaleX(mZoom);
        setScaleY(mZoom);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public interface onChessDownListener {
        void onChessDown(int x, int y);
    }
}
