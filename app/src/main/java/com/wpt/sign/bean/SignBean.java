package com.wpt.sign.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SignBean {

    @Id(autoincrement = true)
    public Long id;

    public String date;

    public long signStampTime;

    public String signInTime;

    public String signUpTime;

    public String duration;

    @Generated(hash = 791854322)
    public SignBean(Long id, String date, long signStampTime, String signInTime,
            String signUpTime, String duration) {
        this.id = id;
        this.date = date;
        this.signStampTime = signStampTime;
        this.signInTime = signInTime;
        this.signUpTime = signUpTime;
        this.duration = duration;
    }

    @Generated(hash = 1440706430)
    public SignBean() {
    }

    public Long getId() {
        return this.id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSignInTime() {
        return this.signInTime;
    }

    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }

    public String getSignUpTime() {
        return this.signUpTime;
    }

    public void setSignUpTime(String signUpTime) {
        this.signUpTime = signUpTime;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSignStampTime() {
        return this.signStampTime;
    }

    public void setSignStampTime(long signStampTime) {
        this.signStampTime = signStampTime;
    }

}
