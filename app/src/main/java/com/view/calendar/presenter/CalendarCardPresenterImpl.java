package com.view.calendar.presenter;

import android.util.Log;

import com.view.calendar.model.CustomDate;
import com.view.calendar.util.DateUtil;
import com.view.calendar.util.StringUtil;
import com.view.calendar.view.CalendarCard;

import java.text.SimpleDateFormat;

import static com.view.calendar.view.CalendarCard.isDoubleChoose;

/**
 * Created by Destiny on 2016/12/15.
 */

public class CalendarCardPresenterImpl implements CalendarCardPresenter {
    private CalendarCard card;
    public CalendarCardPresenterImpl(CalendarCard card){
        this.card=card;
    }

    @Override
    public void setChooseDate(CustomDate date,String clickDate) {

        if (isDoubleChoose==0) {
            card.mCellClickListener.clickDate(date);
            DateUtil.start_date =  clickDate;
            DateUtil.end_date="";
            DateUtil.dates.clear();
        } else if (isDoubleChoose==1){
            DateUtil.dates.clear();
            /******* 判断开始时间是否为空（是否第一次选择日期） *******/
            if (StringUtil.isNullOrEmpty(DateUtil.start_date) && StringUtil.isNullOrEmpty(DateUtil.end_date)) {
                /*** 当前为第一次选择日期 ****/
                DateUtil.start_date = clickDate;
            } else if (!StringUtil.isNullOrEmpty(DateUtil.start_date) && StringUtil.isNullOrEmpty(DateUtil.end_date)) {
                /***** 当前为第二次选择时间 *****/
                /**** 判断当前选择时间是否早于开始时间 ***/
                if (DateUtil.isFront(DateUtil.start_date, clickDate)) {
                    /****** 当前选择时间早于开始时间，将当前选择时间设置成开始时间，将原开始时间设置为结束时间 *****/
                    DateUtil.end_date = DateUtil.start_date;
                    DateUtil.start_date = clickDate;
                } else {
                    /***** 当前选择时间晚于开始时间，将当前选择时间设置成结束时间 *****/
                    DateUtil.end_date = clickDate;
                }
            } else {
                /*** 后续选择时间 ****/
                try {
                    int start = DateUtil.getGapCount(new SimpleDateFormat("yyyy-MM-dd").parse(clickDate),
                            new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.start_date));
                    /**** 当前选择时间距离开始时间的天数 ****/
                    int end = DateUtil.getGapCount(new SimpleDateFormat("yyyy-MM-dd").parse(clickDate),
                            new SimpleDateFormat("yyyy-MM-dd").parse(DateUtil.end_date));
                    /***** 当前选择时间距离结束时间的天数 ******/

                    if (start > 0) {
                        /**** 当前选择时间在开始时间之前 *****/
                        DateUtil.start_date = clickDate;
                    } else if (end < 0) {
                        /**** 当前选择时间在结束时间之后 *****/
                        DateUtil.end_date = clickDate;
                    } else if (start < 0 && end > 0) {
                        /***** 当前选择时间在开始时间和结束时间之间 *****/
                        if (Math.abs(start) < Math.abs(end)) {
                            /**** 当前选择时间距离开始时间较近 *****/
                            DateUtil.start_date = clickDate;
                            /**** 重设开始时间为当前选择时间 ******/
                        } else if (Math.abs(start) >= Math.abs(end)) {
                            /***** 当前选择时间距离结束时间较近 *******/
                            DateUtil.end_date = clickDate;
                            /****** 重设结束时间为当前选择时间 ********/
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            card.mCellClickListener.doubleClickDate();

        }else if (isDoubleChoose==2){
            DateUtil.start_date="";
            DateUtil.end_date="";
            if (!DateUtil.dates.contains(clickDate)){
                DateUtil.dates.add(clickDate);
            }
            card.mCellClickListener.doubleClickDate();
        }

    }
}
