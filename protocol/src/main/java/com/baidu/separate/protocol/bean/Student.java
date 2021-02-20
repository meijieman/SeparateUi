package com.baidu.separate.protocol.bean;

import com.baidu.separate.protocol.Staff;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/15 8:55 AM
 */

public class Student implements Staff {

    @Override
    public int days() {
        return 30;
    }

    @Override
    public int limit() {
        return 5;
    }
}
