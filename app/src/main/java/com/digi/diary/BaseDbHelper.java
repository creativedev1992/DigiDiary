package com.digi.diary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.digi.diary.db.tables.NotesTable;

/**
 * Created by anupama.sinha on 25-10-2016.
 */
public class BaseDbHelper extends SQLiteOpenHelper implements DBManifest {
    String LOG_TAG = "BaseDbHelper";

    public BaseDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "Tables creation start.");
        int size = CREATE_QUERIES.length;
        for (int i = 0; i < size; i++) {
            db.execSQL(CREATE_QUERIES[i]);
            Log.i(LOG_TAG, "CREATE_QUERIES[i] "+CREATE_QUERIES[i]);

        }
        Log.i(LOG_TAG, "Tables creation end.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        int size = CREATE_QUERIES.length;
        for (int i = 0; i < size; i++) {
            db.execSQL("DROP TABLE IF EXISTS " + NotesTable.CATEOGTY_TABLE);


        }


        // Create tables again
        onCreate(db);
    }
}
