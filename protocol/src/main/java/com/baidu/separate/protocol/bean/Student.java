package com.baidu.separate.protocol.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.separate.protocol.Staff;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/15 8:55 AM
 */

public class Student implements Staff, Parcelable {

    private Teacher teacher;
    private int grade;

    public Student() {
    }

    protected Student(Parcel in) {
        grade = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(grade);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public int days() {
        return 30;
    }

    @Override
    public int limit() {
        return 5;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Student{" +
                "teacher=" + teacher +
                ", grade=" + grade +
                '}';
    }
}
