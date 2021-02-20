# 数据请求与数据显示在不同进程方案

## 原理
使用 service 实现跨进程通讯，使用接口代理实现数据和显示分离。

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



## 缺陷
1. com.baidu.provider.Provider.get 方法存在缺陷
如果传入的是接口，它有多个实现，而实现的方式又不一样，如何确定调用哪个实现？
-需要根据调用方法的对象来确定对应的实现，在 Provider#get 添加识别具体实例对象的参数。

2. 目前能从实现类主动调用到应用层上面么？能否在 BookServiceImpl 主动调用方法，然后回调到 MainActivity

3. 普通方法参数能够定义为 interface 的么





















