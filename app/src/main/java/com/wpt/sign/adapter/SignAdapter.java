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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SignBean> mDatas;

    private LayoutInflater mLayoutInflater;

    private View mFooterView;

    private static final int TYPE_NORMAL = 0;

    private static final int TYPE_FOOTER = 1;

    private static final String HOUR = "hour";

    private static final String MINUTE = "minute";

    private static final String SECOND = "second";

    public SignAdapter(Context context,List<SignBean> datas){
        mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setFooter(View footerView){
        mFooterView = footerView;
    }

    public View getFooterView() {
        return mFooterView;
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
        } else if (getItemViewType(position) == TYPE_FOOTER){
            HashMap<String,Integer> totalMap = new HashMap<>();
            for (SignBean bean: mDatas){
                handleTotalTime(bean,totalMap);
            }
            FooterViewHolder fViewHolder = (FooterViewHolder) holder;
            if (mDatas != null && mDatas.size() > 0){
                if (totalMap.get(SECOND) == null || totalMap.get(MINUTE) == null
                || totalMap.get(HOUR) == null){
                    return;
                }
                float avgSencod = (totalMap.get(SECOND) % 60) / (float)mDatas.size();
                long tempMinute = totalMap.get(SECOND) / 60;
                float avgMinute = ((totalMap.get(MINUTE)  + tempMinute)% 60) / (float)mDatas.size();
                long tempHour = (totalMap.get(MINUTE)  + tempMinute) / 60;
                float avgHour = (tempHour + totalMap.get(HOUR)) / (float)mDatas.size();
                String avg = (getFormat(avgHour) + "时" + getFormat(avgMinute) + "分" + getFormat(avgSencod) + "秒");
                fViewHolder.agvTv.setText(avg);
            }
        }
    }

    private float getFormat(float f){
        BigDecimal bigDecimal = new BigDecimal(f);
        return bigDecimal.setScale(1, RoundingMode.HALF_DOWN).floatValue();
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

    private void handleTotalTime(SignBean bean,@NonNull HashMap<String,Integer> totalMap){
        if (!TextUtils.isEmpty(bean.duration)){
            int hour = 0;
            int minute = 0;
            int second = 0;
            int hourIndex = bean.duration.indexOf("时");
            if (hourIndex > -1){
                String hourStr = bean.duration.substring(0,hourIndex);
                Log.d("wpt","hourStr=" +hourStr);
                hour = Integer.parseInt(hourStr);
            }
            int minuteIndex = bean.duration.indexOf("分");
            if (hourIndex > -1 && minuteIndex > -1 && minuteIndex > hourIndex){
                String minuteStr = bean.duration.substring(hourIndex + 1,minuteIndex);
                minute = Integer.parseInt(minuteStr);
                Log.d("wpt","minuteStr=" +minuteStr);
            }
            int secondIndex = bean.duration.indexOf("秒");
            if (secondIndex > -1 && minuteIndex > -1 && secondIndex > minuteIndex){
                String secondStr = bean.duration.substring(minuteIndex + 1,secondIndex);
                second = Integer.parseInt(secondStr);
                Log.d("wpt","secondStr=" +secondStr);
            }
            Integer preHour = totalMap.get(HOUR);
            Integer preMinute = totalMap.get(MINUTE);
            Integer preSeconde = totalMap.get(SECOND);
            totalMap.put(HOUR,preHour == null ?  hour : preHour + hour);
            totalMap.put(MINUTE,preMinute == null ?  minute : preMinute + minute);
            totalMap.put(SECOND,preSeconde == null ?  second : preSeconde + second);
        }
    }

}
