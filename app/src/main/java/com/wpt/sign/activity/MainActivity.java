package com.wpt.sign.activity;

import android.app.DatePickerDialog;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.wpt.sign.R;
import com.wpt.sign.adapter.SignAdapter;
import com.wpt.sign.bean.SignBean;
import com.wpt.sign.utils.DataManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Subscriber;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private Button mSignInBtn;

    private Button mSignOutBtn;

    private SignAdapter mAdapter;

    private TextView mTitleTv;

    private TextView mStartTv;

    private TextView mEndTv;

    private Calendar mCalendar = Calendar.getInstance();

    private int  mStartYear;

    private int  mStartMonth;

    private int  mStartDay;

    private int  mEndYear;

    private int  mEndMonth;

    private int  mEndDay;

    private Button mCheckBtn;

    private View mFooter;

    private List<SignBean> mAllList = new ArrayList<>();

    private List<SignBean> mRangeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("wpt","onCreate");

        mCheckBtn = findViewById(R.id.sign_check_btn);
        mTitleTv = findViewById(R.id.title);
        mStartTv = findViewById(R.id.sign_start_text);
        mEndTv = findViewById(R.id.sign_end_text);
        mSignInBtn = findViewById(R.id.sign_in_btn);
        mSignOutBtn = findViewById(R.id.sign_out_btn);
        RecyclerView mRecyclerView = findViewById(R.id.sign_recyclerview);

        mStartTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mEndTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mAdapter = new SignAdapter(this, mRangeList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFooter = LayoutInflater.from(this).inflate(R.layout.sign_list_item_footer, mRecyclerView, false);
        mAdapter.setFooter(mFooter);
        mRecyclerView.setAdapter(mAdapter);


        mSignInBtn.setOnClickListener(this);
        mSignOutBtn.setOnClickListener(this);
        mStartTv.setOnClickListener(this);
        mEndTv.setOnClickListener(this);
        mCheckBtn.setOnClickListener(this);

        initData();

    }


    private void initData(){
        StringBuilder start = new StringBuilder();
        StringBuilder end = new StringBuilder();
        mStartYear = mCalendar.get(Calendar.YEAR);
        mStartMonth = mCalendar.get(Calendar.MONTH) + 1;
        mStartDay = 1;

        mEndYear = mCalendar.get(Calendar.YEAR);
        mEndMonth = mCalendar.get(Calendar.MONTH) + 1;
        mEndDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        start.append(mStartYear)
                .append("/")
                .append(mStartMonth)
                .append("/")
                .append(mStartDay);
        end.append(mEndYear)
                .append("/")
                .append(mStartMonth)
                .append("/")
                .append(mEndDay);
        mStartTv.setText(start);
        mEndTv.setText(end);
        mTitleTv.setText(DataManager.stampToHour(System.currentTimeMillis()));
        DataManager.listgetDatas()
                .subscribe(new Subscriber<List<SignBean>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("wpt","onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("wpt","onError");
                    }

                    @Override
                    public void onNext(List<SignBean> signBeans) {
                        if (signBeans == null || signBeans.size() == 0){
                            return;
                        }
                        for (SignBean bean : signBeans) {
                            if (bean == null){
                                continue;
                            }
                            boolean isRange = isRange(DataManager.stampToYear(bean.signStampTime),
                                    DataManager.stampToMonth(bean.signStampTime),
                                    DataManager.stampToDay(bean.signStampTime));
                            if (isRange){
                                mRangeList.add(bean);
                            }
                        }
                        mAllList.addAll(signBeans);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }



    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onClick(View view) {
        if (view == mSignInBtn){
            signIn();
        } else if (view == mSignOutBtn){
            signOut();
        } else if (view == mStartTv){
            showStartDatePickr();
        } else if (view == mEndTv){
            showEndDatePickr();
        } else if (view == mCheckBtn){
            doQuery();
        }
    }

    private void signIn(){
        long curTime = System.currentTimeMillis();
        for (SignBean sign: mAllList){
            if (sign == null){
                continue;
            }
            String curDate = DataManager.stampToDate(curTime);
            if (TextUtils.equals(curDate,sign.date)){
                mSignInBtn.setEnabled(false);
                Toast.makeText(this,R.string.had_sign_in,Toast.LENGTH_SHORT).show();
                return;
            }
        }
        SignBean bean = DataManager.saveData(curTime,true);
        if (bean == null){
            return;
        }
        mRangeList.add(bean);
        mAllList.add(bean);
        mAdapter.notifyDataSetChanged();
    }

    private void signOut(){
        SignBean bean = DataManager.saveData(System.currentTimeMillis(),false);
        if (bean == null){
            return;
        }
        for (SignBean sign: mAllList){
            if (TextUtils.equals(bean.date,sign.date)){
                sign.signUpTime = bean.signUpTime;
                sign.duration = bean.duration;
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    DatePickerDialog startPicker;
    private void showStartDatePickr() {
        if (startPicker == null) {
            startPicker = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            mStartYear = year;
                            mStartMonth = month + 1;
                            mStartDay = dayOfMonth;
                            StringBuilder selData = new StringBuilder();
                            selData.append(year)
                                    .append("/")
                                    .append((month + 1))
                                    .append("/")
                                    .append(dayOfMonth);
                            mStartTv.setText(selData);
                        }
                    },
                    mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        startPicker.show();
    }

    DatePickerDialog endPicker;
    private void showEndDatePickr() {
        if (endPicker == null) {
            endPicker = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            mEndYear = year;
                            mEndMonth = month + 1;
                            mEndDay = dayOfMonth;
                            StringBuilder selData = new StringBuilder();
                            selData.append(year)
                                    .append("/")
                                    .append((month + 1))
                                    .append("/")
                                    .append(dayOfMonth);
                            mEndTv.setText(selData);
                        }
                    },
                    mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        endPicker.show();
    }

    private boolean isRange(int year,int month,int day){
        if (year < mStartYear || year > mEndYear){
            return false;
        }
        if (year == mStartYear && year == mEndYear){
            if (month < mStartMonth || month > mEndMonth){
                return false;
            }
        }
        if (month == mStartMonth && month == mEndMonth){
            if (day < mStartDay || day > mEndDay){
                return false;
            }
        }
        return true;
    }

    private void doQuery(){
        if (mAllList == null || mAllList.size() == 0){
            return;
        }
        if (mStartYear > mEndYear) {
            Toast.makeText(this,"开始时间不能大于结束时间",Toast.LENGTH_SHORT).show();
            return;
        }
        if (mStartYear == mEndYear && mStartMonth > mEndMonth){
            Toast.makeText(this,"开始时间不能大于结束时间",Toast.LENGTH_SHORT).show();
            return;
        }if (mStartYear == mEndYear && mStartMonth == mEndMonth && mStartDay > mEndDay){
            Toast.makeText(this,"开始时间不能大于结束时间",Toast.LENGTH_SHORT).show();
            return;
        }
        mRangeList.clear();
        for (SignBean bean : mAllList) {
            if (bean == null){
                continue;
            }
            boolean isRange = isRange(DataManager.stampToYear(bean.signStampTime),
                    DataManager.stampToMonth(bean.signStampTime),
                    DataManager.stampToDay(bean.signStampTime));
            if (isRange){
                mRangeList.add(bean);
            }
        }
        if (mRangeList.size() == 0){
            Toast.makeText(this,"该时间段无数据",Toast.LENGTH_SHORT).show();
            mAdapter.setFooter(null);
        } else {
            if (mAdapter.getFooterView() == null){
                mAdapter.setFooter(mFooter);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

}
