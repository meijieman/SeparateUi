package com.baidu.separate.protocol.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/6 11:19 PM
 */

public class Result implements Parcelable {

    private int code;
    private String msg;

    public Result() {

    }

    protected Result(Parcel in) {
        code = in.readInt();
        msg = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                "} " + hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel in) {
        code = in.readInt();
        msg = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(msg);
    }
}
