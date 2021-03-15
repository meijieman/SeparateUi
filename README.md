# 数据请求与数据显示在不同进程方案

## 原理
使用 service 实现跨进程通讯，使用接口代理实现数据和显示分离。

对于注册回调，因为跨进程需要使用 aidl，定义一个通用的 CallbackProxy，注册回调监听，然后通过动态代理实现各种监听的注册和回调

## 框架集成步骤
1. 在 server app集成 
```
    implementation(name: 'provider_server_debug_20210309_1905', ext: 'aar')
    implementation(name: 'common_debug_20210310_1419', ext: 'aar')
```

2. 在 server Application#onCreate 中初始化实现类
``` java
    DataCenter.getInstance().add(new BookServiceImpl());
```

3. 在 client app 集成
    `api(name: 'provider_debug_20210309_1905', ext: 'aar')`
如果不是同进程，还需要依赖
    `implementation(name: 'common_debug_20210310_1419', ext: 'aar')`
4. 在 client app 初始化
``` java
    Provider.getInstance().init(getApplicationContext(), true);
```
5. 在 client app 需要跨进程调用的时候拿到对应的接口
``` java
    BookService bookService = Provider.getInstance().get(BookService.class);
```

## app
基础服务提供模块，可以为远程提供服务的主要组件



## third_part
第三方调用app，调用 protocol 中提供的接口

## protocol 
提供服务的接口定义，被 app 和 third_part 所依赖，有了 protocol 就不用担心同一个接口在 app 和 third_part 中路径不一样，
以致 ICallImpl#getCallResult 反射调用找不到对应的类

## common 模块
最底层基础库，提供通用基础功能，如 logutil, toast 等




## 两个sdk为通用控制sdk，主要为进行进程间通信
一个为服务端sdk(provider-server)，提供数据获取能力；
一个为客户端sdk(provider)，提供从服务端接收数据的能力


在宿主app中集成服务端sdk（一个手机环境只能集成一个服务端sdk）

对于 register 开头的注册方法，如果参数类型为对象形式，则将其包装为 Call，传过去后再拆出来，封装成一个对象保存到 Map 中，可解决 register 失败的问题

## 注册回调的注意事项
1. 注册的回调方法需要以 register 开头，反注册需要以 unregister 开头，register 方法的参数个数只有一个，参数类型为接口
2. 方法的参数如果包含对象，需要实现 Parcelable
3. 注册的回调接口不需要定义为 aidl，在实现类中使用不需要使用 RemoteCallbackList
4. 远程方法调用参数可以为 Serializable 的对象，其中包含的成员变量如果是对象，需要实现 Serializable
5. 不可以在 Parcelable 中包含 Serializable 对象

## 问题
1. com.baidu.provider.Provider.get 方法存在缺陷
如果传入的是接口，它有多个实现，而实现的方式又不一样，如何确定调用哪个实现？
-需要根据调用方法的对象来确定对应的实现，在 Provider#get 添加识别具体实例对象的参数。

2. 目前能从实现类主动调用到应用层上面么？能否在 BookServiceImpl 主动调用方法，然后回调到 MainActivity
- 已完成此功能，借助 CallbackProxy 跨进程完成回调

3. 普通方法参数能够定义为 interface 的么
- 可以，参考 com.baidu.separate.protocol.BookService.borrowBook

4. binder dead 需要触发重连机制
linkToDeath

5. 当 服务断开 aidl链路崩溃，之前调用的注册监听的逻辑如何处理
- 尽量保证 aidl 断开时能够重连

6. 对于运行的时候，client 重启了， server 没有重启， server 存在注册的回调如何处理
- client 每次注册的时候获取其 pid，将 pid 和 packagename做绑定，回调时加以区别。或相同包名再次注册时清理之前 pid 绑定的监听。

7. 对于运行中，client 没有重启， server 重启了，server 丢失了注册的回调如何处理
- server 每次启动后发送一个动态广播，client 接收此广播，得知 client 重启了

8. 集成 aar 的时候报错 `Duplicate class com.baidu.provider.Call found in modules jetified-provider_debug_20210310_1231-runtime`
- 原因为 provider.aar 和 provider_server.aar 有重复的文件，然后一个app中又集成了这两个 aar。解决办法为手动删除 provider.aar 中的重复的文件，注意此时的 provider.aar
是不能用户跨进程的 client app 中的

9. 增加 service 通过 server 连接 client，这样 server 就可以主动调用 client 了



## 原理 demo
com.baidu.provider.server.Test3




Android通过RemoteViews实现跨进程更新UI
https://blog.csdn.net/chenzheng8975/article/details/54969791


getInterfaceDescriptor

方法描述符，获取方法的唯一描述










