package com.wpt.sign.utils;

import com.wpt.sign.application.SignApplication;
import com.wpt.sign.bean.SignBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DataManager {

    private static Calendar sCalendar = Calendar.getInstance();

    private static SimpleDateFormat mSimpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();

    public static SignBean saveData(long time, boolean isSignIn){
        SignBean bean = null;
        String date = stampToDate(time);
        String showTime = stampToHour(time);
        if (isSignIn){
            bean = new SignBean();
            bean.date = date;
            bean.signInTime = showTime;
            bean.signStampTime = time;
            SignApplication.getDaoSession().insert(bean);
        } else {
            List<SignBean> list = SignApplication.getDaoSession().loadAll(SignBean.class);
            for (SignBean temp : list){
                if (temp != null && temp.date.equals(date)){
                    bean = temp;
                    break;
                }
            }
            if (bean != null){
                bean.signUpTime = showTime;
                bean.duration = getDuration(bean.signStampTime,time);
                SignApplication.getDaoSession().update(bean);
            }
        }

        return bean;

    }

    public static Observable<List<SignBean>> listgetDatas() {

        return Observable.create(new Observable.OnSubscribe<List<SignBean>>() {
            @Override
            public void call(Subscriber<? super List<SignBean>> subscriber) {
                subscriber.onNext(SignApplication.getDaoSession().loadAll(SignBean.class));
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread());

    }
    /*
     * 将时间戳转换为日期
     */
    public static String stampToDate(long time){
        sCalendar.setTimeInMillis(time);
        mSimpleDateFormat.applyPattern("yyyy/MM/dd");
        return String.valueOf(mSimpleDateFormat.format(sCalendar.getTime()));
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToHour(long time){
        sCalendar.setTimeInMillis(time);
        mSimpleDateFormat.applyPattern("HH:mm:ss");
        return String.valueOf(mSimpleDateFormat.format(sCalendar.getTime()));
    }


    public static int stampToYear(long time){
        sCalendar.setTimeInMillis(time);
        return sCalendar.get(Calendar.YEAR);
    }

    public static int stampToMonth(long time){
        sCalendar.setTimeInMillis(time);
        return sCalendar.get(Calendar.MONTH) + 1;
    }

    public static int stampToDay(long time){
        sCalendar.setTimeInMillis(time);
        return sCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getDuration(long signInTime,long signUpTime){
        String duration = null;
        if (signInTime > signUpTime){
            return duration;
        }
        long secon = 1000;
        long minute = 60 * 1000;
        long hour = 60 * 60 * 1000;
        long millDur = signUpTime - signInTime;
        long durHour = millDur / hour;
        long durMinute = (millDur - durHour * hour) / minute;
        long durSecond = (millDur - (durHour * hour + durMinute * minute)) / secon;
        return durHour + "时" + durMinute + "分" + durSecond + "秒";
    }
}
