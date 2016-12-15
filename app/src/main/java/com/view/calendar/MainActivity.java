package com.view.calendar;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.view.calendar.util.DateUtil;
import com.view.calendar.util.StringUtil;
import com.view.calendar.view.CalendarCard;
import com.view.calendar.view.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener{
    private RadioGroup rb_main;
    private TextView monthText, yearText, gapCountText, tv_week;
    private Typeface typeFace;
    private DrawerLayout dl_main;
    private CalendarView cv_main;
    private float y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        typeFace = Typeface.createFromAsset(getAssets(), "font.ttf");
        initView();
        setListener();
    }

    private void setListener() {
        rb_main.setOnCheckedChangeListener(this);

        dl_main.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View arg0, float arg1) {
                if (!StringUtil.isNullOrEmpty(DateUtil.start_date)) {
                    try {
                        if (CalendarCard.isDoubleChoose){
                            yearText.setVisibility(View.GONE);
                            gapCountText.setVisibility(View.GONE);

                            if (!StringUtil.isNullOrEmpty(DateUtil.start_date)&&!StringUtil.isNullOrEmpty(DateUtil.end_date)){
                                int num=DateUtil.getGapCount(DateUtil.start_date,DateUtil.end_date)+1;
                                monthText.setTextSize(16);
                                monthText.setText("已选择"+Math.abs(num)+"天");
                                String str_sDate=new SimpleDateFormat("M月d日").format(new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.start_date));
                                String str_eDate=new SimpleDateFormat("M月d日").format(new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.end_date));
                                tv_week.setText(str_sDate+"～"+str_eDate);
                            }
                        }else{
                            yearText.setVisibility(View.VISIBLE);
                            gapCountText.setVisibility(View.VISIBLE);
                            monthText.setTextSize(26);
                            monthText.setText(new SimpleDateFormat("M月d日").format(new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.start_date)));
                            yearText.setText(DateUtil.getYear() + "");
                            gapCountText.setText("1");
                            tv_week.setText("星期" + DateUtil.getWeek(DateUtil.start_date));

                            gapCountText.setText(DateUtil.getStringGapCount(new Date(System.currentTimeMillis()),
                                    new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.start_date)));
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void initView() {
        rb_main = (RadioGroup) findViewById(R.id.rb_main);
        yearText = (TextView) findViewById(R.id.tv_CurrentYear);
        monthText = (TextView) findViewById(R.id.tv_CurrentMonth);
        gapCountText = (TextView) findViewById(R.id.tv_GapCountString);
        tv_week = (TextView) findViewById(R.id.tv_week);
        dl_main = (DrawerLayout) findViewById(R.id.dl_main);
        cv_main= (CalendarView) findViewById(R.id.cv_main);

        monthText.setTypeface(typeFace);
        yearText.setTypeface(typeFace);
        gapCountText.setTypeface(typeFace);
        tv_week.setTypeface(typeFace);

        monthText.setText(new SimpleDateFormat("M月d日").format(new Date(System.currentTimeMillis())));
        yearText.setText(DateUtil.getYear() + "");
        gapCountText.setText("今天");
        tv_week.setText("星期"+DateUtil.getWeek(System.currentTimeMillis()));

    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_single:
                CalendarCard.isDoubleChoose = false;
                dl_main.closeDrawers();
                break;
            case R.id.rb_multi:
                CalendarCard.isDoubleChoose = true;
                dl_main.closeDrawers();
                break;
        }
    }

}
