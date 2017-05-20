package com.digi.diary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.digi.diary.BaseModel;

/**
 * Created by anupama.sinha on 25-10-2016.
 */
public class NotesModel extends BaseModel implements Parcelable {
    private String id;
    private long mDate;
    private String mTitle;
    private String mContents;
    private  String mPhotoUri;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public NotesModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getmDate() {
        return mDate;
    }

    public void setmDate(long mDate) {
        this.mDate = mDate;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmContents() {
        return mContents;
    }

    public void setmContents(String mContents) {
        this.mContents = mContents;
    }

    public String getmPhotoUri() {
        return mPhotoUri;
    }

    public void setmPhotoUri(String mPhotoUri) {
        this.mPhotoUri = mPhotoUri;
    }



    protected NotesModel(Parcel in) {
        id = in.readString();
        mDate = in.readLong();
        mTitle = in.readString();
        mContents = in.readString();
        mPhotoUri = in.readString();
        isSelected = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(mDate);
        dest.writeString(mTitle);
        dest.writeString(mContents);
        dest.writeString(mPhotoUri);
        dest.writeByte((byte) (isSelected ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NotesModel> CREATOR = new Parcelable.Creator<NotesModel>() {
        @Override
        public NotesModel createFromParcel(Parcel in) {
            return new NotesModel(in);
        }

        @Override
        public NotesModel[] newArray(int size) {
            return new NotesModel[size];
        }
    };
}

