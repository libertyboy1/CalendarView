package com.view.calendar.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.view.calendar.R;
import com.view.calendar.activity.MainActivity;
import com.view.calendar.adapter.CalendarViewAdapter;
import com.view.calendar.model.CustomDate;
import com.view.calendar.util.DateUtil;
import com.view.calendar.util.DensityUtil;
import com.view.calendar.util.StringUtil;

import static android.R.attr.id;

public class CalendarView extends LinearLayout implements OnClickListener, CalendarCard.OnCellClickListener, CalendarCard.OnDrawRowFinishLitener {
    private View view;
    private Context mContext;

    private ViewPager mViewPager;
    private ImageView iv_menu;

    private int mCurrentIndex = 498;
    private CalendarCard[] mShowViews;
    private CalendarViewAdapter<CalendarCard> adapter;
    private SildeDirection mDirection = SildeDirection.NO_SILDE;
    private  CalendarCard[] views;

    private boolean isChanged = true;

    @Override
    public void onFinish(int width, int height) {
        if (isChanged) {
            isChanged = false;
            mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(mContext, height+5)));
        }
    }

    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE;
    }

    private TextView monthText, yearText, gapCountText;
    private ImageButton closeImgBtn;

    private Typeface typeFace;

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public CalendarView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        typeFace = Typeface.createFromAsset(mContext.getAssets(), "font.ttf");

        view = LayoutInflater.from(mContext).inflate(R.layout.calendar_view, this, true);

        mViewPager = (ViewPager) view.findViewById(R.id.vp_calendar);
        closeImgBtn = (ImageButton) view.findViewById(R.id.btnClose);
        yearText = (TextView) view.findViewById(R.id.tvCurrentYear);
        monthText = (TextView) view.findViewById(R.id.tvCurrentMonth);
        gapCountText = (TextView) view.findViewById(R.id.tvGapCountString);
        iv_menu= (ImageView) view.findViewById(R.id.iv_menu);

        monthText.setTypeface(typeFace);
        yearText.setTypeface(typeFace);
        gapCountText.setTypeface(typeFace);

        closeImgBtn.setOnClickListener(this);
        iv_menu.setOnClickListener(this);

        views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(mContext, this);
            views[i].setOnDrawRowFinishLitener(this);
        }
        adapter = new CalendarViewAdapter<>(views);
        setViewPager();

        monthText.setText(new SimpleDateFormat("M月dd日").format(new Date(System.currentTimeMillis())));
        yearText.setText(DateUtil.getYear() + "");
        gapCountText.setText("今天");

    }

    private void setViewPager() {
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(498);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                measureDirection(position);
                updateCalendarView(position);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                ((Activity) mContext).finish();
                break;
            case R.id.iv_menu:
                ((MainActivity)mContext).dl_main.openDrawer(Gravity.LEFT);
                break;
            default:
                break;
        }
    }

    @Override
    public void clickDate(CustomDate date) {
        monthText.setText(date.month + "月" + date.day + "日");
        yearText.setText(date.year + "");
        try {
            gapCountText.setText(DateUtil.getStringGapCount(new Date(System.currentTimeMillis()),
                    new SimpleDateFormat("yyyy-MM-dd").parse(date.year + "-" + date.month + "-" + date.day)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void changeDate(CustomDate date) {
    }

    @Override
    public void doubleClickDate() {
        monthText.setText(new SimpleDateFormat("M月d日").format(new Date(System.currentTimeMillis())));
        yearText.setText(DateUtil.getYear() + "");
        if (CalendarCard.isDoubleChoose==1){
            if (!StringUtil.isNullOrEmpty(DateUtil.start_date)&&!StringUtil.isNullOrEmpty(DateUtil.end_date)){
                int num=DateUtil.getGapCount(DateUtil.start_date,DateUtil.end_date)+1;
                gapCountText.setText("已选择"+Math.abs(num)+"天");
            }else{
                gapCountText.setText("已选择1天");
            }
        }else{
            gapCountText.setText("已选择"+DateUtil.dates.size()+"天");
        }
    }

    /**
     * 计算方向
     *
     * @param arg0
     */
    private void measureDirection(int arg0) {

        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;

        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;

    }

    // 更新日历视图
    private void updateCalendarView(int arg0) {
        mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
    }


}
