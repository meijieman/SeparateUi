package com.baidu.separate.protocol;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/15 8:54 AM
 */
public interface Staff {

    /**
     * 可借天数
     *
     * @return
     */
    int days();

    /**
     * 最大借书数量
     */
    int limit();
}
