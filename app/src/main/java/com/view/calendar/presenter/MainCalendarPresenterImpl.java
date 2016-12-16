package com.view.calendar.presenter;

import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.view.calendar.MainActivity;
import com.view.calendar.R;
import com.view.calendar.util.DateUtil;
import com.view.calendar.util.StringUtil;
import com.view.calendar.view.CalendarCard;
import com.view.calendar.view.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.view.calendar.R.id.cv_main;
import static com.view.calendar.R.id.dl_main;
import static com.view.calendar.R.id.rb_main;

/**
 * Created by Destiny on 2016/12/15.
 */

public class MainCalendarPresenterImpl implements MainCalendarPresenter, RadioGroup.OnCheckedChangeListener {
    private MainActivity mActivity;
    private Typeface typeFace;

    public MainCalendarPresenterImpl(MainActivity mActivity) {
        this.mActivity = mActivity;
        typeFace = Typeface.createFromAsset(mActivity.getAssets(), "font.ttf");
    }

    private void setDrawerTitleInfo() {
        if (!StringUtil.isNullOrEmpty(DateUtil.start_date)) {
            try {
                if (CalendarCard.isDoubleChoose==1) {
                    mActivity.yearText.setVisibility(View.GONE);
                    mActivity.gapCountText.setVisibility(View.GONE);

                    if (!StringUtil.isNullOrEmpty(DateUtil.start_date) && !StringUtil.isNullOrEmpty(DateUtil.end_date)) {
                        int num = DateUtil.getGapCount(DateUtil.start_date, DateUtil.end_date) + 1;
                        mActivity.monthText.setTextSize(16);
                        mActivity.monthText.setText("已选择" + Math.abs(num) + "天");
                        String str_sDate = new SimpleDateFormat("M月d日").format(new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.start_date));
                        String str_eDate = new SimpleDateFormat("M月d日").format(new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.end_date));
                        mActivity.tv_week.setText(str_sDate + "～" + str_eDate);
                    }
                } else if (CalendarCard.isDoubleChoose==0){
                    mActivity.yearText.setVisibility(View.VISIBLE);
                    mActivity.gapCountText.setVisibility(View.VISIBLE);
                    mActivity.monthText.setTextSize(26);
                    mActivity.monthText.setText(new SimpleDateFormat("M月d日").format(new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.start_date)));
                    mActivity.yearText.setText(DateUtil.getYear() + "");
                    mActivity.gapCountText.setText("1");
                    mActivity.tv_week.setText("星期" + DateUtil.getWeek(DateUtil.start_date));

                    mActivity.gapCountText.setText(DateUtil.getStringGapCount(new Date(System.currentTimeMillis()),
                            new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.start_date)));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            if (CalendarCard.isDoubleChoose==2){
                mActivity.yearText.setVisibility(View.VISIBLE);
                mActivity.gapCountText.setVisibility(View.VISIBLE);
                mActivity.monthText.setTextSize(26);
                mActivity.monthText.setText(new SimpleDateFormat("M月d日").format(new Date(System.currentTimeMillis())));
                mActivity.yearText.setText(DateUtil.getYear() + "");
                mActivity.gapCountText.setText("今天");
                mActivity.tv_week.setText("星期" + DateUtil.getWeek(System.currentTimeMillis()));
            }
        }

    }

    @Override
    public void initView() {
        mActivity.rb_main = (RadioGroup) mActivity.findViewById(rb_main);
        mActivity.yearText = (TextView) mActivity.findViewById(R.id.tv_CurrentYear);
        mActivity.monthText = (TextView) mActivity.findViewById(R.id.tv_CurrentMonth);
        mActivity.gapCountText = (TextView) mActivity.findViewById(R.id.tv_GapCountString);
        mActivity.tv_week = (TextView) mActivity.findViewById(R.id.tv_week);
        mActivity.dl_main = (DrawerLayout) mActivity.findViewById(dl_main);
        mActivity.cv_main = (CalendarView) mActivity.findViewById(cv_main);

        mActivity.monthText.setTypeface(typeFace);
        mActivity.yearText.setTypeface(typeFace);
        mActivity.gapCountText.setTypeface(typeFace);
        mActivity.tv_week.setTypeface(typeFace);

        mActivity.monthText.setText(new SimpleDateFormat("M月d日").format(new Date(System.currentTimeMillis())));
        mActivity.yearText.setText(DateUtil.getYear() + "");
        mActivity.gapCountText.setText("今天");
        mActivity.tv_week.setText("星期" + DateUtil.getWeek(System.currentTimeMillis()));
    }

    @Override
    public void setListener() {
        mActivity.rb_main.setOnCheckedChangeListener(this);

        mActivity.dl_main.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View arg0, float arg1) {
                setDrawerTitleInfo();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_single:
                CalendarCard.isDoubleChoose = 0;
                mActivity.dl_main.closeDrawers();
                break;
            case R.id.rb_multi:
                CalendarCard.isDoubleChoose = 1;
                mActivity.dl_main.closeDrawers();
                break;
            case R.id.rb_select:
                CalendarCard.isDoubleChoose = 2;
                mActivity.dl_main.closeDrawers();
                break;
        }
    }
}
