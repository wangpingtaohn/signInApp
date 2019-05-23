package com.wpt.sign.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wpt.sign.R;
import com.wpt.sign.bean.SignBean;

import java.util.ArrayList;
import java.util.List;

public class SignAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SignBean> mDatas;

    private LayoutInflater mLayoutInflater;

    private View mFooterView;

    private static final int TYPE_NORMAL = 0;

    private static final int TYPE_FOOTER = 1;

    private long mTotalHour;

    private long mTotalMinute;

    private long mTotalSecond;

    public SignAdapter(Context context,List<SignBean> datas){
        mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setFooter(View footerView){
        mFooterView = footerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView != null && position == mDatas.size()){
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mFooterView != null && viewType == TYPE_FOOTER){
            return new FooterViewHolder(mFooterView);
        }
        View view = mLayoutInflater.inflate(R.layout.sign_list_item,viewGroup,false);
        return new SignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_NORMAL){
            SignBean bean = mDatas.get(position);
            if (bean == null){
                return;
            }
            SignViewHolder sViewHolder = (SignViewHolder) holder;
            sViewHolder.dateTv.setText(bean.date);
            sViewHolder.signInTv.setText(bean.signInTime);
            sViewHolder.signOutTv.setText(bean.signUpTime);
            sViewHolder.durationTv.setText(bean.duration);
            handleTotalTime(bean);
        } else if (getItemViewType(position) == TYPE_FOOTER){
            FooterViewHolder fViewHolder = (FooterViewHolder) holder;
            if (mDatas != null && mDatas.size() > 0){
                String avg = (mTotalHour / mDatas.size() + "时" + mTotalMinute / mDatas.size() + "分" + mTotalSecond / mDatas.size() + "秒");
                fViewHolder.agvTv.setText(avg);
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = mDatas != null ? mDatas.size() : 0;
        return mFooterView == null ? count : count + 1;
    }

    class SignViewHolder extends RecyclerView.ViewHolder{

        TextView dateTv,signInTv, signOutTv, durationTv;

        public SignViewHolder(View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.item_date);
            signInTv = itemView.findViewById(R.id.item_sign_in);
            signOutTv = itemView.findViewById(R.id.item_sign_out);
            durationTv = itemView.findViewById(R.id.item_duration);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder{
        TextView agvTv;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            agvTv = itemView.findViewById(R.id.item_average_time);
        }
    }

    private void handleTotalTime(SignBean bean){
        if (!TextUtils.isEmpty(bean.duration)){
            int hourIndex = bean.duration.indexOf("时");
            if (hourIndex > -1){
                String hourStr = bean.duration.substring(0,hourIndex);
                Log.d("wpt","hourStr=" +hourStr);
                mTotalHour += Long.parseLong(hourStr);
            }
            int minuteIndex = bean.duration.indexOf("分");
            if (hourIndex > -1 && minuteIndex > -1 && minuteIndex > hourIndex){
                String minuteStr = bean.duration.substring(hourIndex + 1,minuteIndex);
                mTotalMinute += Long.parseLong(minuteStr);
                Log.d("wpt","minuteStr=" +minuteStr);
            }
            int secondIndex = bean.duration.indexOf("秒");
            if (secondIndex > -1 && minuteIndex > -1 && secondIndex > minuteIndex){
                String secondStr = bean.duration.substring(minuteIndex + 1,secondIndex);
                mTotalSecond += Long.parseLong(secondStr);
                Log.d("wpt","secondStr=" +secondStr);
            }
        }
    }
}
