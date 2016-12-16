package com.view.calendar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.view.calendar.R;
import com.view.calendar.presenter.MainCalendarPresenterImpl;
import com.view.calendar.view.CalendarView;

public class MainActivity extends Activity{
    public RadioGroup rb_main;
    public TextView monthText, yearText, gapCountText, tv_week;
    public DrawerLayout dl_main;
    public CalendarView cv_main;

    private MainCalendarPresenterImpl presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        presenter=new MainCalendarPresenterImpl(this);
        presenter.initView();
        presenter.setListener();
    }

}
