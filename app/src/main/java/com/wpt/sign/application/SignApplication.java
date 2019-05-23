package com.wpt.sign.application;

import android.app.Application;

import com.wpt.sign.greendao.DaoMaster;
import com.wpt.sign.greendao.DaoSession;

import org.greenrobot.greendao.database.Database;

public class SignApplication extends Application {

    private String DATA_BASE_NAME = "signDbName";

    private static DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,DATA_BASE_NAME);
        Database database = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);

        daoSession = daoMaster.newSession();

    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
