package com.digi.diary.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.digi.diary.BaseApplication;
import com.digi.diary.BaseModel;
import com.digi.diary.BaseTable;
import com.digi.diary.model.NotesModel;

import java.util.ArrayList;

/**
 * Created by anupama.sinha on 25-10-2016.
 */
public class NotesTable extends BaseTable {

    //	private static final String PRIMARY_KEY="_id";
    private static final String NOTES_ID = "notesId";
    private static final String NOTES_TITLE= "notes_title";
    private static final String NOTES_DATES = "notes_date";
    private static final String NOTES_CONTENTS = "notes_contents";
    private static final String NOTES_PHOTO_URI = "notes_photo";
    /**
     * Table Name:-
     */

    public static final String CATEOGTY_TABLE = "NotesTable";

    public static final String CREATE_CATEGORY_TABLE = "create table if not exists " +
            CATEOGTY_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
            NOTES_ID + " text primary key ," +
            NOTES_TITLE + " text not null ," +
            NOTES_DATES + " integer ," +
            NOTES_CONTENTS + " text ,"+
            NOTES_PHOTO_URI+" text  )";

    public static final String DROP_QUERY = "Drop table if exists " + CATEOGTY_TABLE;
    /**
     * Get the global instance of the SQLiteDatabase.
     *
     * @param pApplication
     *
     */
    public NotesTable(BaseApplication pApplication) {
        super(pApplication, CATEOGTY_TABLE);
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        NotesModel notesModel = (NotesModel) pModel;
        ContentValues _contentValue = new ContentValues();
        _contentValue.put(NOTES_ID, (notesModel).getId());
        _contentValue.put(NOTES_TITLE, (notesModel).getmTitle());
        _contentValue.put(NOTES_DATES, (notesModel).getmDate());
        _contentValue.put(NOTES_CONTENTS, ((notesModel).getmContents()));
        _contentValue.put(NOTES_PHOTO_URI, ((notesModel).getmPhotoUri()));
        return _contentValue;
    }

    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
        ArrayList<BaseModel> categoryList = new ArrayList<BaseModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + CATEOGTY_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                NotesModel notesModel = new NotesModel();
                notesModel.setmTitle(_cursor.getString(_cursor.getColumnIndex(NOTES_TITLE)));
                notesModel.setmDate(_cursor.getInt(_cursor.getColumnIndex(NOTES_DATES)));
                categoryList.add(notesModel);
            }
        } catch (Exception e) {
            Log.e(CATEOGTY_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return categoryList;
    }
    public  ArrayList<NotesModel> getAllNotesData() {
        ArrayList<NotesModel> categoryList = new ArrayList<NotesModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + CATEOGTY_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                NotesModel notesModel = new NotesModel();
                notesModel.setId(_cursor.getString(_cursor.getColumnIndex(NOTES_ID)));
                notesModel.setmTitle(_cursor.getString(_cursor.getColumnIndex(NOTES_TITLE)));
                notesModel.setmDate(_cursor.getInt(_cursor.getColumnIndex(NOTES_DATES)));
                notesModel.setmContents(_cursor.getString(_cursor.getColumnIndex(NOTES_CONTENTS)));
                notesModel.setmPhotoUri(_cursor.getString(_cursor.getColumnIndex(NOTES_PHOTO_URI)));
                categoryList.add(notesModel);
            }
        } catch (Exception e) {
            Log.e(CATEOGTY_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return categoryList;
    }
    public  boolean isRecordExistByDate(long date) {
        ArrayList<NotesModel> categoryList = new ArrayList<NotesModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + CATEOGTY_TABLE+" where "+NOTES_DATES+"="+date;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            if(_cursor.getCount() > 0) {
                _cursor.moveToFirst();
                return true;
            }
        } catch (Exception e) {
            Log.e(CATEOGTY_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return false;
    }
    public  boolean isRecordExistById(String id) {
        ArrayList<NotesModel> categoryList = new ArrayList<NotesModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + CATEOGTY_TABLE+" where "+NOTES_ID+"="+id;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            if(_cursor.getCount() > 0) {
                _cursor.moveToFirst();
                return true;
            }
        } catch (Exception e) {
            Log.e(CATEOGTY_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return false;
    }
    public void updateRecord(NotesModel notesModel,String id)
    {
        ContentValues _contentValue = new ContentValues();
        _contentValue.put(NOTES_ID, (notesModel).getId());
        _contentValue.put(NOTES_DATES, (notesModel).getmDate());
        _contentValue.put(NOTES_TITLE, (notesModel).getmTitle());
        _contentValue.put(NOTES_CONTENTS, ((notesModel).getmContents()));
        _contentValue.put(NOTES_PHOTO_URI,((notesModel).getmPhotoUri()));
        int i=mWritableDatabase.update(CATEOGTY_TABLE,_contentValue,"notesId ='" + id + "'",null);
        if(i>0)
        {
            Log.d("check","success");
        }
        else
        {
            Log.d("check","failure");
        }
    }
    public void deleteRecord(String id)
    {
        int c = mWritableDatabase.delete(CATEOGTY_TABLE, NOTES_ID + "= ? ",
                new String[] { id });
        Log.w("deletedrow", "no. of row deleted  " + c + "mid " + id);
//        mWritableDatabase.close();
    }
}
