package com.baidu.separate.protocol.bean;

import com.baidu.separate.protocol.Staff;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/15 8:55 AM
 */

public class Teacher implements Staff, Parcelable {

    public Teacher() {

    }

    protected Teacher(Parcel in) {
    }

    public static final Creator<Teacher> CREATOR = new Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel in) {
            return new Teacher(in);
        }

        @Override
        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };

    @Override
    public int days() {
        return 30;
    }

    @Override
    public int limit() {
        return 10;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
