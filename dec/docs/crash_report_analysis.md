# 番茄小说 崩溃日志上报功能分析

## 概述

本文档分析番茄小说 Android 应用的崩溃日志上报功能，基于字节跳动 Npth（Performance & Tracing Hub）SDK 实现。崩溃上报系统支持多种崩溃类型捕获、日志上传、自定义过滤等核心功能。

## 一、崩溃类型

### 1.1 崩溃类型枚举

**CrashType** - 崩溃类型枚举

**位置**: `com.bytedance.crash.CrashType`

```java
public enum CrashType {
    LAUNCH("launch"),       // 启动崩溃
    JAVA("java"),           // Java崩溃
    NATIVE("native"),       // Native崩溃
    ASAN("asan"),           // ASAN内存检测
    TSAN("tsan"),           // TSan线程检测
    ANR("anr"),             // ANR（应用无响应）
    NATIVE_CUSTOMIZE("ensure_zip"),  // Native自定义崩溃
    BLOCK("block"),         // 卡顿
    ENSURE("ensure"),       // Ensure崩溃
    DART("dart"),           // Dart崩溃
    GAME("game"),           // 游戏崩溃
    CUSTOM_JAVA("custom_java"),  // 自定义Java崩溃
    OOM("oom"),             // 内存溢出
    CJAVA("cjava"),        // C/C++ Java混合崩溃
    COREDUMP("coredump"),   // Core转储
    EXIT("exit"),           // 退出
    EVENT("event"),         // 事件崩溃
    ALL("all"),             // 所有类型
    UNKNOWN("unknown");      // 未知类型
}
```

### 1.2 崩溃类型分类

| 类型 | 值 | 说明 |
|------|------|------|
| LAUNCH | launch | 应用启动阶段崩溃 |
| JAVA | java | Java层崩溃 |
| NATIVE | native | Native（C/C++）层崩溃 |
| ASAN | asan | AddressSanitizer检测到的内存问题 |
| TSAN | tsan | ThreadSanitizer检测到的线程问题 |
| ANR | anr | Application Not Responding |
| BLOCK | block | 主线程卡顿 |
| OOM | oom | Out of Memory |
| DART | dart | Flutter/Dart虚拟机崩溃 |
| GAME | game | 游戏引擎崩溃 |
| CJAVA | cjava | Java与Native混合崩溃 |

## 二、核心类结构

### 2.1 NpthCore - 崩溃监控核心

**位置**: `com.bytedance.crash.NpthCore`

```java
public class NpthCore {
    // 初始化崩溃监控
    public static void k(Context context, File file) {
        a.b(context, file);
    }

    // 异步初始化
    public static void l() {
        b.b();
    }

    // 上报自定义错误
    public static void r(String str) {
        if (o() && !TextUtils.isEmpty(str)) {
            i70.a.a(str);
        }
    }

    // 上报自定义异常
    public static void v(String str, String str2, String str3) {
        if (!o()) {
            return;
        }
        m70.a.a(str, str2, str3);
    }

    // 设置ANR监控
    public static void w(String str, p pVar) {
        e70.f.r(str, pVar);
    }

    // 获取用户数据中心
    public static boolean m() {
        if (!o()) {
            return false;
        }
        return e70.f.m();
    }

    // 注册崩溃回调
    public static void D(t tVar) {
        if (!o()) {
            return;
        }
        m70.b.c(tVar);
    }

    // 设置崩溃上传
    public static void C(com.bytedance.crash.upload.d dVar) {
        CrashUploader.h(dVar);
    }
}
```

### 2.2 MonitorCrash - 崩溃监控配置

**位置**: `com.bytedance.crash.MonitorCrash`

```java
public class MonitorCrash {
    private final Config mConfig = new Config(this);
    private final com.bytedance.crash.monitor.d mCrashListener;

    // 初始化App崩溃监控
    public static MonitorCrash initApp(Context context, String str, long j17, String str2) {
        return new MonitorCrash(com.bytedance.crash.monitor.h.a(context, str, str2, j17));
    }

    // 初始化SDK崩溃监控
    public static MonitorCrash initSDK(Context context, String str, long j17, String str2, String str3, String[] strArr) {
        return new MonitorCrash(com.bytedance.crash.monitor.h.d(context, str, str2, j17, new String[]{str3}, strArr));
    }

    // 上报自定义错误
    public void reportCustomErr(String str, Throwable th6) {
        this.mCrashListener.q(str, th6);
    }

    // 上报自定义错误（带额外信息）
    public void reportCustomErr(String str, String str2, Throwable th6) {
        this.mCrashListener.q(str, th6);
    }

    // 设置渠道
    public Config setChannel(String str) {
        this.mMonitorCrash.setChannel(str);
        return this;
    }

    // 设置设备ID
    public Config setDeviceId(String str) {
        this.mMonitorCrash.setDeviceId(str);
        return this;
    }

    // 设置用户ID
    public Config setUID(long j17) {
        this.mMonitorCrash.setUserId(j17);
        return this;
    }

    // 配置类
    public class Config {
        public Config setPackageName(String str);
        public Config setSoList(String[] strArr);
    }
}
```

### 2.3 CrashBody - 崩溃信息体

**位置**: `com.bytedance.crash.entity.CrashBody`

```java
public class CrashBody {
    private JSONObject mCrashJson;
    private Header mHeader;

    // 获取Header
    public Header getHeader() {
        return this.mHeader;
    }

    // 获取崩溃JSON
    public JSONObject getJson() {
        return this.mCrashJson;
    }

    // 设置Header
    public CrashBody setHeader(Header header) {
        put("header", header.f());
        this.mHeader = header;
        return this;
    }

    // 添加自定义字段
    public CrashBody addCustom(String str, String str2) {
        putInJson(this.mCrashJson, "custom", str, str2);
        return this;
    }

    // 添加过滤器字段
    public CrashBody addFilter(String str, String str2) {
        putInJson(this.mCrashJson, "filters", str, str2);
        return this;
    }

    // 设置应用启动时间
    public CrashBody setAppStartTime(long j17) {
        return setAppStartTime(j17, 0L);
    }

    // 设置应用启动时间（带启动耗时）
    public CrashBody setAppStartTime(long j17, long j18) {
        put("app_start_time", Long.valueOf(j17));
        if (j18 != 0) {
            put("app_start_up_time", Long.valueOf(j18));
        }
        put("app_start_time_readable", new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date(j17)));
        return this;
    }

    // 添加Long类型自定义字段
    public CrashBody addCustomLong(String str, List<String> list) {
        JSONArray jSONArray = new JSONArray();
        Iterator<String> it6 = list.iterator();
        while (it6.hasNext()) {
            jSONArray.put(it6.next());
        }
        addCustomLong(str, jSONArray);
        return this;
    }
}
```

### 2.4 CrashSummary - 崩溃摘要

**位置**: `com.bytedance.crash.crash.CrashSummary`

```java
public abstract class CrashSummary implements Comparable {
    protected final long mCrashTime;         // 崩溃时间
    protected final CrashType mCrashType;   // 崩溃类型
    protected final File mDirectory;        // 崩溃目录
    protected final int mPid;               // 进程ID
    protected final String mProcessName;    // 进程名
    protected final int mTid;               // 线程ID
    protected final String mThreadName;     // 线程名
    protected final long mStartTime;        // 开始时间
    protected final long sAppStartUpTime;   // APP启动时间
    protected String mStackTrace;           // 堆栈信息
}
```

### 2.5 CrashUploader - 崩溃上传器

**位置**: `com.bytedance.crash.upload.CrashUploader`

```java
public class CrashUploader {
    // 压缩类型
    public enum CompressType {
        NONE(0),      // 不压缩
        GZIP(1),      // GZIP压缩
        DEFLATER(2);  // Deflater压缩
    }

    // 创建上传器
    public static d e() {
        return null;
    }

    // 设置上传器
    public static void h(d dVar) {
    }

    // 设置上传策略
    public static void i(e eVar) {
    }

    // 设置上传回调
    public static void j(d80.c cVar) {
    }

    // 创建HTTP连接
    public static HttpURLConnection g(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    // 上传崩溃数据
    public static byte[] a(String str) {
        return c(str, null, "gzip", "GET", f());
    }
}
```

## 三、回调接口

### 3.1 ICrashCallback - 崩溃回调

**位置**: `com.bytedance.crash.ICrashCallback`

```java
public interface ICrashCallback {
    /**
     * 崩溃发生时回调
     * @param crashType 崩溃类型
     * @param str 崩溃信息
     * @param thread 崩溃线程
     */
    void onCrash(CrashType crashType, String str, Thread thread);
}
```

### 3.2 ICrashFilter - 崩溃过滤器

**位置**: `com.bytedance.crash.ICrashFilter`

```java
public interface ICrashFilter {
    /**
     * Java崩溃过滤
     * @param th6 异常
     * @param thread 线程
     * @return true=不上报，false=上报
     */
    boolean onJavaCrashFilter(Throwable th6, Thread thread);

    /**
     * Native崩溃过滤
     * @param str 崩溃信息
     * @param str2 堆栈信息
     * @return true=不上报，false=上报
     */
    boolean onNativeCrashFilter(String str, String str2);
}
```

## 四、崩溃配置

### 4.1 CrashReportConfig - 崩溃上报配置

**位置**: `com.dragon.read.base.ssconfig.template.CrashReportConfig`

```java
public final class CrashReportConfig {
    @SerializedName("enable")
    public final boolean enable;        // 是否启用

    @SerializedName("key_words")
    public final List<String> keyWords;  // 关键字过滤

    // AB实验配置
    static {
        SsConfigMgr.prepareAB("custom_exception_config_v619", CrashReportConfig.class, ICrashReportConfig.class);
    }

    // 获取配置
    public static final CrashReportConfig b() {
        Object aBValue = SsConfigMgr.getABValue("custom_exception_config_v619", CrashReportConfig.f121750c);
        return (CrashReportConfig) aBValue;
    }
}
```

### 4.2 配置项

| 配置项 | 说明 | 示例 |
|-------|------|------|
| custom_exception_config_v619 | 崩溃上报AB实验 | enable=true, keyWords=["keyword1"] |

## 五、未捕获异常处理

### 5.1 KillAppUncaughtExceptionHandler

**位置**: `com.bytedance.android.btm.impl.pageshow.KillAppUncaughtExceptionHandler`

```java
public final class KillAppUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    // 初始化
    public static final class a {
        public final void a() {
            // 保存系统默认的异常处理器
            KillAppUncaughtExceptionHandler.f41840a = Thread.getDefaultUncaughtExceptionHandler();
            // 设置自定义异常处理器
            Thread.setDefaultUncaughtExceptionHandler(new KillAppUncaughtExceptionHandler());
        }
    }

    @Override
    public void uncaughtException(final Thread thread, Throwable th6) {
        // 记录崩溃日志
        com.bytedance.android.btm.api.inner.a.f41432c.h("UncaughtExceptionHandler", true, new Function0<String>() {
            @Override
            public final String invoke() {
                return thread.getName();
            }
        });

        // 记录页面信息
        if (BtmSetting.f41896c.b().d().f() == 1) {
            PageInfoStack pageInfoStackX = BtmPageRecorder.f41630v.x(objD);
            PageInfo pageInfoCurrent = pageInfoStackX.current();
            if (pageInfoCurrent != null) {
                PageShowLog.f41844c.u(pageInfoCurrent.copy(), "crash");
            }
        }

        // 调用系统默认异常处理器
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = f41840a;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th6);
        }
    }
}
```

## 六、日志上传策略

### 6.1 DefaultAlogUploadStrategy

**位置**: `com.bytedance.crash.alog.DefaultAlogUploadStrategy`

```java
public class DefaultAlogUploadStrategy implements IAlogUploadStrategy {

    private String mProcessName;

    @Override
    public List<String> getUploadAlogFiles(String str, long j17) {
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(str)) {
            return arrayList;
        }

        File[] fileArrListFiles = new PolarisFileWrapper(str).listFiles();
        if (fileArrListFiles == null) {
            return arrayList;
        }

        // 判断是否为子进程
        boolean z17 = !this.mProcessName.contains(":");

        // 处理进程名
        String strReplace = this.mProcessName.contains(":")
            ? this.mProcessName.replace(":", "-")
            : this.mProcessName;

        // 遍历文件
        for (File file3 : fileArrListFiles) {
            String name = file3.getName();
            // 查找.hot文件
            if (name.endsWith(".hot") && ...) {
                String[] strArrSplit = name.split("_");
                // 提取时间戳
                long j18 = strArrSplit.length >= 4
                    ? Long.parseLong(strArrSplit[3])
                    : Long.parseLong(strArrSplit[0]);

                // 选择文件
                if (j19 < j18) {
                    file = file2;
                    file2 = file3;
                    j19 = j18;
                }
            }
        }

        // 添加到上传列表
        if (file != null && file.length() > 0) {
            arrayList.add(file.getAbsolutePath());
        }
        if (file2 != null && file2.length() > 0) {
            arrayList.add(file2.getAbsolutePath());
        }

        return arrayList;
    }
}
```

## 七、崩溃上报流程

### 7.1 整体流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                    崩溃上报整体流程                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. 初始化阶段                                                  │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ NpthCore.initApp()                                     │  │
│     │ - k(context, crashDir)                                │  │
│     │ - l() // 异步初始化                                    │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  2. 崩溃捕获阶段                                                │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ KillAppUncaughtExceptionHandler                        │  │
│     │ - uncaughtException(thread, throwable)                │  │
│     │ - 记录页面信息                                         │  │
│     │ - 调用系统默认处理器                                    │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  3. 崩溃处理阶段                                                │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ NpthCore                                               │  │
│     │ - r(String) // 上报自定义错误                           │  │
│     │ - v(String, String, String) // 上报异常                 │  │
│     │ - D(ICrashCallback) // 注册回调                        │  │
│     └────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                              ▼                                   │
│  4. 崩溃上传阶段                                                │
│     ┌────────────────────────────────────────────────────────┐  │
│     │ CrashUploader                                          │  │
│     │ - g(URL) // 创建连接                                   │  │
│     │ - a(String) // 上传数据                                │  │
│     │ - CompressType // 压缩类型                             │  │
│     └────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 Java崩溃处理流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    Java崩溃处理流程                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Thread.uncaughtException(thread, throwable)                     │
│                              │                                   │
│                              ▼                                   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ KillAppUncaughtExceptionHandler                           │   │
│  │ 1. 调用 ICrashCallback.onCrash()                         │   │
│  │ 2. 检查 ICrashFilter.onJavaCrashFilter()                │   │
│  │ 3. 记录页面信息 PageShowLog                              │   │
│  │ 4. 调用系统默认 UncaughtExceptionHandler                │   │
│  └──────────────────────────┬───────────────────────────────┘   │
│                             │                                     │
│                             ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ NpthCore                                                 │   │
│  │ - 崩溃信息写入文件                                        │   │
│  │ - 崩溃数据封装为 CrashBody                               │   │
│  │ - 回调 ICrashCallback.onCrash()                         │   │
│  └──────────────────────────┬───────────────────────────────┘   │
│                             │                                     │
│                             ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ CrashUploader                                            │   │
│  │ - 压缩崩溃数据 (GZIP/DEFLATER)                          │   │
│  │ - 发送HTTP请求                                           │   │
│  │ - 上报到服务端                                           │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 7.3 自定义崩溃上报

```java
// 使用MonitorCrash上报自定义错误
MonitorCrash monitorCrash = MonitorCrash.initApp(context, appKey, uid, channel);

// 设置配置
monitorCrash.config()
    .setChannel("official")
    .setDeviceId("device_id")
    .setUID(123456);

// 上报自定义错误
try {
    // 业务代码
} catch (Throwable e) {
    monitorCrash.reportCustomErr("business_error", e);
}

// 使用NpthCore上报
NpthCore.r("custom_error_message");
NpthCore.v("error_tag", "error_message", "stack_trace");
```

## 八、崩溃信息结构

### 8.1 崩溃JSON结构

```json
{
  "header": {
    "app_key": "string",
    "device_id": "string",
    "channel": "string",
    "version": "string",
    "timestamp": 1234567890
  },
  "custom": {
    "key1": "value1",
    "key2": "value2"
  },
  "filters": {
    "filter_key": "filter_value"
  },
  "crash_type": "java",
  "crash_time": 1234567890,
  "crash_time_readable": "2024_01_01_12_00_00",
  "process_name": "com.dragon.read",
  "thread_name": "main",
  "stack_trace": "...",
  "app_start_time": 1234567800,
  "app_start_up_time": 1000
}
```

### 8.2 Header结构

```java
public class Header implements Serializable {
    public String app_key;           // 应用Key
    public String app_version;       // 应用版本
    public String channel;           // 渠道
    public String device_id;         // 设备ID
    public long timestamp;           // 时间戳
    public String os_version;        // 系统版本
    public String sdk_version;       // SDK版本
    public String package_name;      // 包名
}
```

## 九、ANR监控

### 9.1 ANR处理

```java
// 设置ANR监控
NpthCore.w("anr_tag", new p() {
    @Override
    public void onAnr(String trace, long j17) {
        // ANR发生时的回调
        Log.e("ANR", "ANR detected: " + trace);
    }
});

// 获取ANR信息
boolean hasAnr = NpthCore.m();
```

### 9.2 ANR检测配置

| 配置项 | 说明 |
|-------|------|
| anr_tag | ANR标签 |
| trace | ANR堆栈信息 |
| timestamp | ANR发生时间 |

## 十、关键类清单

| 类名 | 路径 | 功能 |
|------|------|------|
| NpthCore | com.bytedance.crash | 崩溃监控核心类 |
| MonitorCrash | com.bytedance.crash | 崩溃监控配置 |
| CrashType | com.bytedance.crash | 崩溃类型枚举 |
| ICrashCallback | com.bytedance.crash | 崩溃回调接口 |
| ICrashFilter | com.bytedance.crash | 崩溃过滤接口 |
| CrashBody | com.bytedance.crash.entity | 崩溃信息体 |
| CrashSummary | com.bytedance.crash.crash | 崩溃摘要 |
| CrashUploader | com.bytedance.crash.upload | 崩溃上传器 |
| KillAppUncaughtExceptionHandler | com.bytedance.android.btm.impl.pageshow | 未捕获异常处理 |
| CrashReportConfig | com.dragon.read.base.ssconfig.template | 崩溃上报配置 |
| DefaultAlogUploadStrategy | com.bytedance.crash.alog | 日志上传策略 |

## 十一、总结

### 11.1 崩溃监控架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      崩溃监控架构                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐       │
│  │   应用层    │     │   SDK层     │     │  系统层     │       │
│  ├─────────────┤     ├─────────────┤     ├─────────────┤       │
│  │             │     │   NpthCore  │     │             │       │
│  │   业务代码  │────▶│  MonitorCrash│────▶│  Thread.    │       │
│  │             │     │             │     │  Uncaught   │       │
│  │             │     │ ICrashCallback│   │  Exception  │       │
│  │             │     │ ICrashFilter │     │  Handler    │       │
│  └─────────────┘     └─────────────┘     └─────────────┘       │
│                                                                  │
│                          │                                        │
│                          ▼                                        │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    崩溃处理                              │    │
│  │  ┌───────────┐  ┌───────────┐  ┌───────────┐           │    │
│  │  │ CrashBody │  │ CrashSummary│ │ CrashUploader│         │    │
│  │  └───────────┘  └───────────┘  └───────────┘           │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 11.2 崩溃类型支持

| 类型 | 支持 | 说明 |
|------|------|------|
| Java崩溃 | ✅ | try-catch + UncaughtExceptionHandler |
| Native崩溃 | ✅ | NDK + NativeBridge |
| ANR | ✅ | 主线程卡顿检测 |
| OOM | ✅ | 内存溢出检测 |
| 自定义崩溃 | ✅ | MonitorCrash.reportCustomErr() |

### 11.3 上报压缩方式

| 方式 | 值 | 说明 |
|------|------|------|
| NONE | 0 | 不压缩 |
| GZIP | 1 | GZIP压缩 |
| DEFLATER | 2 | Deflater压缩 |

### 11.4 关键配置

| 配置项 | 说明 |
|-------|------|
| custom_exception_config_v619 | 崩溃上报AB实验 |
| custom_exception_key_words | 崩溃关键字过滤 |
