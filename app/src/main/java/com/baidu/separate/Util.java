package com.baidu.separate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/6 11:06 PM
 */

public class Util {

    public static void print(String jsonStr) throws JSONException {
        JSONTokener jsonTokener = new JSONTokener(jsonStr);
        Object obj = jsonTokener.nextValue();
        if (obj instanceof JSONObject) {

        } else if (obj instanceof JSONArray) {

        }
    }
}
