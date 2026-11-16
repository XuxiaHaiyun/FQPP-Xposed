# Changelog

所有版本变更记录。格式参考 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)。

## [1.0] - 2026-11-16

首个公开版本。

### ✨ 新增

**接口转发**

- 内置 NanoHTTPD HTTP 服务器（默认端口 `9999`），将番茄小说内部 RPC 转换为标准 HTTP API
- 支持搜索 / 详情 / 批量详情 / 目录 / 正文 / 批量正文 / 有声书播放地址 / 书架 / 书城 / 分类 / 评论（段评、章评、书评、子评论、气泡）等 15+ 接口
- 搜索结果自动重排 `searchTabs`，保证开源阅读书源规则直接可用
- 一键导出开源阅读（Legado）书源 JSON（`/booksource`），支持 `legado://` 协议唤起导入
- 局域网内任意设备（手机、平板、电脑、电子书阅读器）均可访问

**本地会员与去广告**

- 拦截用户信息（`AcctManager`）、会员特权判定（`PrivilegeManager`）、VIP 信息模型注入
- 本地会员开启时强制展示 VIP 卡片，隐藏章末会员广告入口

**界面净化**（总开关 + 12 个默认开启细分项 + 3 个默认关闭细分项）

- 隐藏全屏广告弹窗、搜索框轮播、红点角标、「赚钱 / 短剧」TAB、常玩游戏、金币信息、热搜榜 / AI 板块等

**解封书籍**

- Hook `BookUtils.isUnsafeBook` / `isOverallOffShelf`，解除部分下架 / 封禁书籍阅读限制

**激励秒领**

- 激励视频页创建后立即关闭（等价"看完自动关闭"），SDK 正常发奖上报，奖励真实到账

**运行环境加固**

- 阻止更新（`UpdateServiceImpl.checkUpdate` 全部重载）
- 禁用热更新（Reparo 三层阻断：初始化 / 远程补丁 / 框架开关）
- 禁用插件加载（Mira 三层阻断，慎用，可能导致崩溃）
- 禁用崩溃上报（`NpthCore` + `CrashReportConfig`）

**阅读时长 5x**

- Hook `ReadingTiming.g()`，上报前将时长增量 ×5

**调试能力**

- ImGui 悬浮日志层：日志分级（DEBUG/INFO/WARN/ERROR）、关键词过滤、暂停/恢复、跟随 Activity
- 调试模式下 HTTP 响应返回格式化 JSON

**其他**

- 随番茄小说启动自动拉起 HTTP 服务
- 配置存储于模块自身 SharedPreferences，经 libxposed API 102 `getRemotePreferences()` 跨进程只读同步
- DexKit 混淆定位 + 版本绑定持久化缓存 + 主页延迟重定位兜底
- 设置页标题栏 FQ++ 入口（弹窗只读展示 + 跳转模块配置页）

### 📋 使用前提

- LSPosed（推荐）框架，作用域勾选番茄免费小说
- 番茄免费小说 **7.0.9.32**（其他版本可能因混淆变动失效）
- Android 8.0 (API 26) 及以上

### 🚧 已知 TODO

- 有声书音频解密接口（`/reading/bookapi/audio/decrypt`）暂未实现
- 漫画接口、短剧接口规划中

### 📝 SHA256

```
25f25c9eb0f4999708b9ea8794d55ee139a383b2f0a544eeffa4cd2b7b475284  FQPP-v1.0-debug.apk
```

[1.0]: https://github.com/XuxiaHaiyun/FQPP-Xposed/releases/tag/v1.0
