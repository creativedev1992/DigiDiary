package com.digi.diary;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by anupama.sinha on 25-10-2016.
 */
public class BaseApplication extends Application {

    private SQLiteDatabase mWritableDatabase;

    private static BaseApplication mInstance;
    public static BaseApplication getInstance() {
        return mInstance;
    }

    protected static void setInstance(BaseApplication mInstance) {
        BaseApplication.mInstance = mInstance;
    }

    /**
     * Get the database instance.
     *
     * @return mWritableDatabase
     */
    public SQLiteDatabase getWritableDbInstance() {

        if (mWritableDatabase == null) {
            BaseDbHelper dbHelper = new BaseDbHelper(this);
            mWritableDatabase = dbHelper.getWritableDatabase();
        }
        Log.i("check", "CREATE_QUERIES[i] "+DBManifest.CREATE_QUERIES[0]);
        return mWritableDatabase;
    }
}
