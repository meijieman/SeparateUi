package com.baidu.separate.protocol;

import java.util.List;

import com.baidu.separate.protocol.bean.Book;
import com.baidu.separate.protocol.callback.OnBookListener;

/**
 * TODO
 *
 * @author meijie05
 * @since 2020/11/31 7:13 PM
 */
public interface BookService extends NameService {

    boolean addBook(Book book);

    void removeBook(int no);

    Book getBook(int id);

    int getCount();

    List<Book> getBooks(String json);


    //FIXME 方法参数不能传递普通接口？
    void register(OnBookListener listener);

    void onUnregister(OnBookListener listener);

}
