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
























