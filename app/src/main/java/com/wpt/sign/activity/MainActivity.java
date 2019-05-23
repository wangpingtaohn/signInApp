package com.wpt.sign.activity;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wpt.sign.R;
import com.wpt.sign.adapter.SignAdapter;
import com.wpt.sign.bean.SignBean;
import com.wpt.sign.utils.DataManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private Button mSignInBtn;

    private Button mSignOutBtn;

    private SignAdapter mAdapter;

    private TextView mTitleTv;

    private List<SignBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("wpt","onCreate");

        mTitleTv = findViewById(R.id.title);
        mSignInBtn = findViewById(R.id.sign_in_btn);
        mSignOutBtn = findViewById(R.id.sign_out_btn);
        RecyclerView mRecyclerView = findViewById(R.id.sign_recyclerview);

        mAdapter = new SignAdapter(this,mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        View footer = LayoutInflater.from(this).inflate(R.layout.sign_list_item_footer, mRecyclerView, false);
        mAdapter.setFooter(footer);
        mRecyclerView.setAdapter(mAdapter);


        mSignInBtn.setOnClickListener(this);
        mSignOutBtn.setOnClickListener(this);

        initData();

    }


    private void initData(){
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
                        mList.addAll(signBeans);
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
        }
    }

    private void signIn(){
        long curTime = System.currentTimeMillis();
        for (SignBean sign: mList){
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
        mList.add(bean);
        mAdapter.notifyDataSetChanged();
    }

    private void signOut(){
        SignBean bean = DataManager.saveData(System.currentTimeMillis(),false);
        if (bean == null){
            return;
        }
        for (SignBean sign: mList){
            if (TextUtils.equals(bean.date,sign.date)){
                sign.signUpTime = bean.signUpTime;
                sign.duration = bean.duration;
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

}
