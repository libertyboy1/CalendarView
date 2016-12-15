package com.view.calendar.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.view.calendar.model.CustomDate;
import com.view.calendar.util.CalendarUtil;
import com.view.calendar.util.DateUtil;
import com.view.calendar.util.StringUtil;

import static android.R.attr.y;

/**
 * 自定义日历卡
 *
 * @author suaobo
 */
public class CalendarCard extends View {

    private static final int TOTAL_COL = 7; // 7列
    private static final int TOTAL_ROW = 6; // 6行

    private Paint mCirclePaint; // 绘制圆形的画笔
    private Paint mTextPaint; // 绘制文本的画笔

    private int mViewWidth; // 视图的宽度
    private int mViewHeight; // 视图的高度
    private int mCellSpace; // 单元格间距

    private float mDownX;
    private float mDownY;

    private Row rows[] = new Row[TOTAL_ROW]; // 行数组，每个元素代表一行
    private static CustomDate mShowDate; // 自定义的日期，包括year,month,day
    private OnCellClickListener mCellClickListener; // 单元格点击回调事件
    private OnDrawRowFinishLitener mDrawRowFinishLitener;//组全部画完回调
    private int touchSlop; //
    private boolean callBackCellSpace;

    private Cell mClickCell;

    private String current_date;
    private Typeface typeFace;

    public static boolean isDoubleChoose = false;

    /**
     * 单元格点击的回调接口
     *
     * @author suaobo
     */
    public interface OnCellClickListener {
        void clickDate(CustomDate date); // 单选回调点击的日期

        void doubleClickDate(); // 多选回调点击的日期

        void changeDate(CustomDate date); // 回调滑动ViewPager改变的日期
    }

    /**
     * 组全部画完回调
     *
     * @author suaobo
     */
    public interface OnDrawRowFinishLitener {
        void onFinish(int width, int height); // 回调点击的日期
    }

    public CalendarCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CalendarCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarCard(Context context) {
        super(context);
        init(context);
    }

    public CalendarCard(Context context, OnCellClickListener listener) {
        super(context);
        this.mCellClickListener = listener;
        init(context);
    }

    private void init(Context context) {
        typeFace = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initDate();
    }

    private void initDate() {
        mShowDate = new CustomDate();
        current_date = mShowDate.year + "-" + mShowDate.month + "-" + mShowDate.day;

        fillDate();
    }

    private void fillDate() {
        int monthDay = DateUtil.getCurrentMonthDay(); // 今天
        int lastMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month - 1); // 上个月的天数
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month); // 当前月的天数
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year, mShowDate.month);
        boolean isCurrentMonth = false;
        if (DateUtil.isCurrentMonth(mShowDate)) {
            isCurrentMonth = true;
        }
        int day = 0;
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL; // 单元格位置
                // 这个月的
                if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {
                    day++;
                    rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(mShowDate, day), State.CURRENT_MONTH_DAY, i, j);
                    // 今天
                    if (isCurrentMonth && day == monthDay) {
                        CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
                        rows[j].cells[i] = new Cell(date, State.TODAY, i, j);
                    }

                    if (isCurrentMonth && day > monthDay) { // 如果比这个月的今天要大，表示还没到
                        rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(mShowDate, day), State.UNREACH_DAY, i, j);
                    }

                    // 过去一个月
                } else if (position < firstDayWeek) {
                    rows[j].cells[i] = new Cell(new CustomDate(mShowDate.year, mShowDate.month - 1, lastMonthDays - (firstDayWeek - position - 1)),
                            State.PAST_MONTH_DAY, i, j);
                    // 下个月
                } else if (position >= firstDayWeek + currentMonthDays) {
                    rows[j].cells[i] = new Cell((new CustomDate(mShowDate.year, mShowDate.month + 1, position - firstDayWeek - currentMonthDays + 1)),
                            State.NEXT_MONTH_DAY, i, j);
                }
            }
        }

        mCellClickListener.changeDate(mShowDate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
//        mCellSpace = mViewWidth / TOTAL_COL;
		mCellSpace = Math.min(mViewHeight / TOTAL_ROW, mViewWidth / TOTAL_COL);
        if (!callBackCellSpace) {
            callBackCellSpace = true;
        }
        mTextPaint.setTextSize(mCellSpace / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (mDownX / mCellSpace);
                    int row = (int) (mDownY / mCellSpace);
                    measureClickCell(col, row);
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 计算点击的单元格
     *
     * @param col
     * @param row
     */
    private void measureClickCell(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW)
            return;
        if (mClickCell != null) {
            rows[mClickCell.j].cells[mClickCell.i] = mClickCell;
        }
        if (rows[row] != null) {
            mClickCell = new Cell(rows[row].cells[col].date, rows[row].cells[col].state, rows[row].cells[col].i, rows[row].cells[col].j);

            CustomDate date = rows[row].cells[col].date;
            date.week = col;

            String click_date = date.year + "-" + date.month + "-" + date.day;

            if (!isDoubleChoose) {
                mCellClickListener.clickDate(date);
                DateUtil.start_date = click_date;
                DateUtil.end_date="";
                Log.e("DateUtil.start_date1", DateUtil.start_date );
            } else {
                /******* 判断开始时间是否为空（是否第一次选择日期） *******/
                if (StringUtil.isNullOrEmpty(DateUtil.start_date) && StringUtil.isNullOrEmpty(DateUtil.end_date)) {
                    /*** 当前为第一次选择日期 ****/
                    DateUtil.start_date = click_date;
                    Log.e("DateUtil.start_date2", DateUtil.start_date );
                } else if (!StringUtil.isNullOrEmpty(DateUtil.start_date) && StringUtil.isNullOrEmpty(DateUtil.end_date)) {
                    /***** 当前为第二次选择时间 *****/
                    /**** 判断当前选择时间是否早于开始时间 ***/
                    Log.e("DateUtil.start_date---", DateUtil.start_date );
                    if (DateUtil.isFront(DateUtil.start_date, click_date)) {
                        /****** 当前选择时间早于开始时间，将当前选择时间设置成开始时间，将原开始时间设置为结束时间 *****/
                        DateUtil.end_date = DateUtil.start_date;
                        DateUtil.start_date = click_date;
                        Log.e("click_date3", click_date);
                        Log.e("DateUtil.start_date3", DateUtil.start_date );
                        Log.e("DateUtil.end_date3", DateUtil.end_date );
                    } else {
                        /***** 当前选择时间晚于开始时间，将当前选择时间设置成结束时间 *****/
                        DateUtil.end_date = click_date;
                        Log.e("DateUtil.end_date4", DateUtil.end_date );
                    }
                } else {
                    /*** 后续选择时间 ****/
                    try {
                        int start = DateUtil.getGapCount(new SimpleDateFormat("yyyy-MM-dd").parse(click_date),
                                new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.start_date));
                        /**** 当前选择时间距离开始时间的天数 ****/
                        int end = DateUtil.getGapCount(new SimpleDateFormat("yyyy-MM-dd").parse(click_date),
                                new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.end_date));
                        /***** 当前选择时间距离结束时间的天数 ******/

                        if (start > 0) {
                            /**** 当前选择时间在开始时间之前 *****/
                            DateUtil.start_date = click_date;
                            Log.e("DateUtil.start_date5", DateUtil.start_date );
                        } else if (end < 0) {
                            /**** 当前选择时间在结束时间之后 *****/
                            DateUtil.end_date = click_date;
                            Log.e("DateUtil.end_date6", DateUtil.end_date );
                        } else if (start < 0 && end > 0) {
                            /***** 当前选择时间在开始时间和结束时间之间 *****/

                            if (Math.abs(start) < Math.abs(end)) {
                                /**** 当前选择时间距离开始时间较近 *****/
                                DateUtil.start_date = click_date;
                                Log.e("DateUtil.start_date7", DateUtil.start_date );
                                /**** 重设开始时间为当前选择时间 ******/
                            } else if (Math.abs(start) >= Math.abs(end)) {
                                /***** 当前选择时间距离结束时间较近 *******/
                                DateUtil.end_date = click_date;
                                Log.e("DateUtil.end_date8", DateUtil.end_date );
                                /****** 重设结束时间为当前选择时间 ********/
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                mCellClickListener.doubleClickDate();

            }

            // 刷新界面
            update();
        }
    }

    /**
     * 组元素
     *
     * @author suaobo
     */
    class Row {
        public int j;

        Row(int j) {
            this.j = j;
        }

        public Cell[] cells = new Cell[TOTAL_COL];

        // 绘制单元格
        public void drawCells(Canvas canvas) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) {
                    cells[i].drawSelf(canvas);
                    if (i == cells.length - 1) {
                        if (mDrawRowFinishLitener != null) {
                            mDrawRowFinishLitener.onFinish(getWidth(), getHeight());
                        }
                    }
                }
            }
        }

    }

    /**
     * 单元格元素
     *
     * @author suaobo
     */
    class Cell {
        public CustomDate date;
        public State state;
        public int i;
        public int j;

        public Cell(CustomDate date, State state, int i, int j) {
            super();
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
        }

        public void drawSelf(Canvas canvas) {


            mTextPaint.setTypeface(typeFace);
            // 绘制文字
            String content = date.day + "";

            Calendar calendar = Calendar.getInstance();
            calendar.set(date.year, date.month - 1, date.day);

            CalendarUtil calendarUtil = new CalendarUtil(calendar);

            String lunarDate = calendarUtil.toString();

            if (i == 0 && j == 0) {

                mTextPaint.setColor(Color.parseColor("#0b000000"));

                float wight = (float) ((6.5) * mCellSpace - mTextPaint.measureText(lunarDate) / 2);
                float height = (float) ((5.7) * mCellSpace - mTextPaint.measureText(lunarDate, 0, 1) / 6);

                mTextPaint.setTextSize(height / 1.2f);

                int month = rows[3].cells[3].date.month;

                canvas.drawText(month + "", (wight - mTextPaint.measureText(month + "")) / 1.8f, (height - mTextPaint.measureText(month + "", 0, 1)) * 1.5f,
                        mTextPaint);

            }

            switch (state) {
                case TODAY: // 今天
                    mTextPaint.setColor(Color.parseColor("#fffffe"));
                    break;
                case CURRENT_MONTH_DAY: // 当前月日期
                    mTextPaint.setColor(Color.BLACK);
                    break;
                case PAST_MONTH_DAY: // 过去一个月
                    mTextPaint.setColor(Color.GRAY);
                    break;
                case NEXT_MONTH_DAY: // 下一个月
                    mTextPaint.setColor(Color.GRAY);
                    break;
                case UNREACH_DAY: // 还未到的天
                    mTextPaint.setColor(Color.BLACK);
                    break;
                default:
                    break;
            }

            /*********** 判断开始时间和结束时间是否全部不为空 ************/
            if (!StringUtil.isNullOrEmpty(DateUtil.start_date) && !StringUtil.isNullOrEmpty(DateUtil.end_date)) {
                /****** 全部不为空 *****/
                try {
                    int start = DateUtil.getGapCount(new SimpleDateFormat("yyyy-MM-dd").parse(date.year + "-" + date.month + "-" + date.day),
                            new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.start_date));
                    int end = DateUtil.getGapCount(new SimpleDateFormat("yyyy-MM-dd").parse(date.year + "-" + date.month + "-" + date.day),
                            new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.end_date));
                    /****** 判断当前绘画时间是否在开始和结束时间之间 *****/
                    if (start <= 0 && end >= 0) {
                        /****** 在开始和结束之间，绘画灰色背景框 *****/
                        mCirclePaint.setColor(Color.parseColor("#efefef")); // 灰色选中圆形
                        canvas.drawCircle((float) (mCellSpace * (i + 0.5)), (float) ((j + 0.5) * mCellSpace), mCellSpace / 2.5f, mCirclePaint);
                        mTextPaint.setColor(Color.BLACK);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                /****** 判断开始时间是否不为空，并且判断当前绘画时间是否是开始时间 *****/
                if (!StringUtil.isNullOrEmpty(DateUtil.start_date) && (date.year + "-" + date.month + "-" + date.day).equals(DateUtil.start_date)) {
                    /****** 绘画灰色背景框 *****/
                    mCirclePaint.setColor(Color.parseColor("#efefef")); // 灰色选中圆形
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)), (float) ((j + 0.5) * mCellSpace), mCellSpace / 2.5f, mCirclePaint);
                    mTextPaint.setColor(Color.BLACK);
                }
            }
            /*********** 判断当前绘画日期是否为本日，是本日在标出红圈 ************/
            if ((date.year + "-" + date.month + "-" + date.day).equals(current_date)) {
                mTextPaint.setColor(Color.parseColor("#fffffe"));
                mCirclePaint.setColor(Color.parseColor("#F24949")); // 红色圆形
                canvas.drawCircle((float) (mCellSpace * (i + 0.5)), (float) ((j + 0.5) * mCellSpace), mCellSpace / 2.5f, mCirclePaint);
            }

            mTextPaint.setTextSize(mCellSpace / 3);
            canvas.drawText(content, (float) ((i + 0.5) * mCellSpace - mTextPaint.measureText(content) / 2),
                    (float) ((j + 0.7) * mCellSpace - mTextPaint.measureText(content, 0, 1) * 1.2), mTextPaint);

            mTextPaint.setTextSize(mCellSpace / 5);
            canvas.drawText(lunarDate, (float) ((i + 0.5) * mCellSpace - mTextPaint.measureText(lunarDate) / 2),
                    (float) ((j + 0.7) * mCellSpace - mTextPaint.measureText(lunarDate, 0, 1) / 6 + 6), mTextPaint);

        }
    }

    /**
     * @author suaobo 单元格的状态 当前月日期，过去的月的日期，下个月的日期
     */
    enum State {
        TODAY, CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, UNREACH_DAY;
    }

    // 从左往右划，上一个月
    public void leftSlide() {
        if (mShowDate.month == 1) {
            mShowDate.month = 12;
            mShowDate.year -= 1;
        } else {
            mShowDate.month -= 1;
        }
        update();
    }

    // 从右往左划，下一个月
    public void rightSlide() {
        if (mShowDate.month == 12) {
            mShowDate.month = 1;
            mShowDate.year += 1;
        } else {
            mShowDate.month += 1;
        }
        update();
    }

    public void update() {
        fillDate();
        invalidate();
    }

    public void setOnDrawRowFinishLitener(OnDrawRowFinishLitener mDrawRowFinishLitener) {
        this.mDrawRowFinishLitener = mDrawRowFinishLitener;
    }




}
