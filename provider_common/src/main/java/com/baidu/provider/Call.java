package com.baidu.provider;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.provider.common.Slog;

import java.util.Arrays;

/**
 * Java中如何动态创建接口的实现
 * https://www.cnblogs.com/clonen/p/6735011.html
 *
 * @author meijie05
 * @since 2020/11/31 10:33 PM
 */

public class Call implements Parcelable {

    private static final String TAG = "Call";

    private String className; // 类名或接口名
    private String methodName; // 调用的方法名
    private Class<?>[] paramTypes; // 方法参数类型
    private Object[] params; // 方法参数值
    private Object[] result; // 方法的执行结果，index 为 0。如果正常执行，为方法的返回值；如果方法抛异常，则为该异常

    public Call() {

    }

    protected Call(Parcel in) {
        className = in.readString();
        methodName = in.readString();
        paramTypes = (Class<?>[]) in.readSerializable();
        params = in.readArray(getClass().getClassLoader());
        result = in.readArray(getClass().getClassLoader());
    }

    public Call(String className, String methodName, Class<?>[] paramTypes, Object[] params) {
        this.className = className;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.params = params;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(className);
        dest.writeString(methodName);
        try {
            dest.writeSerializable(paramTypes);
            dest.writeArray(params);
            dest.writeArray(result);
        } catch (Exception e) {
            Slog.e(TAG, "write ex " + e);
        }
    }

    public void readFromParcel(Parcel in) {
        className = in.readString();
        methodName = in.readString();
        paramTypes = (Class<?>[]) in.readSerializable();
        params = in.readArray(getClass().getClassLoader());
        result = in.readArray(getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Call> CREATOR = new Creator<Call>() {
        @Override
        public Call createFromParcel(Parcel in) {
            return new Call(in);
        }

        @Override
        public Call[] newArray(int size) {
            return new Call[size];
        }
    };

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Object getResult() {
        return (result != null && result.length == 1) ? result[0] : null;
    }

    public void setResult(Object result) {
        this.result = new Object[]{result};
    }

    @Override
    public String toString() {
        return "Call{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", params=" + Arrays.toString(params) +
                ", result=" + Arrays.toString(result) +
                "} " + hashCode();
    }
}
