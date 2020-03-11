# 网络组件：casstime-net-android

#### 仓库地址

>  [https://github.com/Casstime/casstime-net-android](https://github.com/Casstime/casstime-net-android)

#### 介绍
网络请求组件，基于[Okhttp3](https://square.github.io/okhttp/)+[Retrofit2](https://square.github.io/retrofit/)+[Rxjava2](https://github.com/ReactiveX/RxJava/wiki)封装。


#### 安装教程
*引入*

```
implementation 'com.casstime.ec:net:0.1.0'
```
*初始化*

```kotlin tab='Kotlin'
val config = CTNetworkConfigInitHelper.initWithApplication(application, baseUrl, isProduction)
    .apply {
        cacheStateSec = (5 * 1024 * 1024).toLong() //缓存大小
        eadTimeOut = 5* 1000 //读取超时(毫秒)
        connectTimeOut = 5* 1000 //连接超时时间(毫秒)
        interceptors = arrayOf(HttpLoggingInterceptor(),  //拦截器
        CallServerInterceptor(true))
    }
CTOkHttpClient.init(config)
```

```java tab='Java'
CTNetworkConfigInitHelper.Config config =
    CTNetworkConfigInitHelper.INSTANCE.initWithApplication(application, baseUrl, isProduction);

config.setCacheStateSec((8 * 1024 * 1024));//缓存大小
config.setConnectTimeOut(8 * 1000);//连接超时时间(毫秒)
config.setReadTimeOut(8 * 1000); //读取超时(毫秒)
config.setInterceptors (new Interceptor[]{ //拦截器
        new CECCommonInterceptor(),
        new OauthInterceptor(),
});
CTOkHttpClient.Companion.init(config);
```


#### 使用说明
#### 1. CTRetrofitFactory

Retrofit的工厂方法类，用于构建Retrofit对象，Retrofit的用法请参考[Retrofit官网](https://square.github.io/retrofit/)

!!! example
    这里举一个简单的例子

``` kotlin tab='Kotlin' hl_lines="8"
//定义请求接口
interface GitHubService {
    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String): Observable<List<Repo>>
}

//构建Retrofit对象
var retrofit: Retrofit = CTRetrofitFactory.instance
//构建Retrofit服务
var service = retrofit.create(GitHubService::class.java)
//发起请求
var repos = service.listRepos("octocat")
```

```java tab='Java' hl_lines="7"
//定义请求接口
public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}
//构建Retrofit对象
Retrofit retrofit = CTRetrofitFactory.Companion.getInstance();
//构建Retrofit服务
GitHubService service = retrofit.create(GitHubService.class);
//发起请求
Observable<List<Repo>> repos = service.listRepos("octocat");
```

#### 2. CTNoNetworkException

无网络状态下抛出的异常，可以在`io.reactivex.Observer.onError(Throwable e)`中获取到。

``` java
@Override
public void onError(Throwable e) {
    // 处理错误信息
   if (e instanceof CTNoNetworkException) {
        String noNetworkMessage = "网络不可用，请检查网络设置";
        Log.e("TAG",noNetworkMessage);
    }
}
```

#### 3. CTHttpTransformer

主要用于网络请求线程调度，请求过程在IO线程，在主线程处理请求回调。

```kotlin tab='kotlin' hl_lines="4"
CTRetrofitFactory.instance
            .create(GitHubService::class.java)
            .listRepos("octocat")
            .compose(CTHttpTransformer())//添加线程调度
            .subscribe()
```

```java tab='Java' hl_lines="4"
CTRetrofitFactory.Companion.getInstance()
                .create(GitHubService.class)
                .listRepos("octocat")
                .compose(new CTHttpTransformer<List<String>>())//添加线程调度
                .subscribe();
```
#### 4. CTOkHttpClient

OkHttpClient的单例实现，Java中`CTOkHttpClient.Component.getInstance()；`或Kotlin中`CTOkHttpClient.instance`获取实例

!!! tip
    1. 默认情况下，添加了打印日志的拦截器，在生产模式下关闭日志
    2. 默认情况下，使用`CTCookieJarManager`管理Cookie
    3. 默认情况下，设置了20秒连接超时，20秒读取超时，10M缓存大小;

#### 5. CTCookieJarManager

CookieJar管理类，负责CookieJar的创建和清除，该类依赖于[https://github.com/franmontiel/PersistentCookieJar](https://github.com/franmontiel/PersistentCookieJar)

```kotlin tab='Kotlin'
//获取CookieJar实例
val cookieJar = CTCookieJarManager.cookieJar
//清除cookie
CTCookieJarManager.clear()
```

```java tab='Java'
//获取CookieJar实例
PersistentCookieJar cookieJar = CTCookieJarManager.Companion.getCookieJar();

//清除cookie
CTCookieJarManager.Companion.clear();
```

#### 6.多实例实现
可以通过以下方式快速构建区别于全局配置的新实例
```kotlin tab='Kotlin'
CTOkHttpClient.Companion.newInstance((config: CTNetworkConfigInitHelper.Config))
CTRetrofitFactory.Companion.newInstance(baseUrl: String, okHttpClient: OkHttpClient)
```





