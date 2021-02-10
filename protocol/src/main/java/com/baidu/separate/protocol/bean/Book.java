package com.baidu.separate.protocol.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO
 *
 * @author meijie05
 * @since 2020/11/31 7:13 PM
 */

public class Book implements Parcelable {

    private String name; // 书名
    private int no; // 编号
    private boolean isAvailable;  // 可借状态

    public Book() {

    }

    protected Book(Parcel in) {
        name = in.readString();
        no = in.readInt();
        isAvailable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(no);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", no=" + no +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
