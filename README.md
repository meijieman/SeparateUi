# 数据请求与数据显示在不同进程方案

## 原理
使用 service 实现跨进程通讯，使用接口代理实现数据和显示分离。

对于注册回调，因为跨进程需要使用 aidl，定义一个通用的 CallbackProxy，注册回调监听，然后通过动态代理实现各种监听的注册和回调

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
1. 注册的回调方法需要以 register 开头，反注册需要以 unregister 开头
2. 方法的参数如果包含对象，需要实现 Parcelable
3. 注册的回调接口不需要定义为 aidl，在实现类中使用不需要使用 RemoteCallbackList

## 缺陷
1. com.baidu.provider.Provider.get 方法存在缺陷
如果传入的是接口，它有多个实现，而实现的方式又不一样，如何确定调用哪个实现？
-需要根据调用方法的对象来确定对应的实现，在 Provider#get 添加识别具体实例对象的参数。

2. 目前能从实现类主动调用到应用层上面么？能否在 BookServiceImpl 主动调用方法，然后回调到 MainActivity
- 已完成此功能，借助 CallbackProxy 跨进程完成回调

3. 普通方法参数能够定义为 interface 的么

4. aidl 被断开的时候，需要触发重连机制。而且之前调用的注册监听的逻辑可能会失效，需要想办法去反注册掉


## 原理 demo
com.baidu.provider.server.Test3


















