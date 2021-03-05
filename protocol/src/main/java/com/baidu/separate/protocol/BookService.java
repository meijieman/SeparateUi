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

    /**
     * 借书
     *
     * @param staff 职员
     * @return
     */
    boolean borrowBook(Staff staff);

    //FIXME 方法参数不能传递普通接口？
    // 2021-02-17 16:53:07 可以传递普通接口，但是接口中的参数需要是 Parcelable
    void register(OnBookListener listener);

    void unregister(OnBookListener listener);

    void registerView(OnViewShow show);
}
