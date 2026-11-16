# 开发指南

## 环境准备

- Android Studio（Hedgehog 2023.1.1 或更高）
- Gradle 9.0.0 / Android Gradle Plugin 8.13.0
- JDK 17
- `compileSdk 36` / `minSdk 26` / `targetSdk 34`

## 主要依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| libxposed API | 102 | Xposed Hook 接口（`compileOnly`，含 RemotePreferences 跨进程配置） |
| NanoHTTPD | 2.3.1 | 内置 HTTP 服务器 |
| Gson | 2.13.2 | JSON 序列化 |
| joor | 0.9.15 | 反射简化工具 |
| DexKit | 2.2.0 | 动态查找混淆类/方法 |
| ImGui | — | 调试悬浮层（调试模式） |

## 快速开始

```bash
git clone https://github.com/XuxiaHaiyun/FQplusplus.git
cd FQplusplus
./gradlew assembleDebug
```

APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

## 项目结构

```
app/src/main/java/com/xxhy/fqpp/
├── App.kt                      # 模块 App Application（LSPosed 服务连接）
├── ui/activity/
│   ├── MainActivity.kt         # 模块首页（激活状态卡片、作用域授权）
│   └── ConfigActivity.kt       # 配置页（全部功能开关，写入模块自身 SP）
└── xposed/
    ├── XposedEntry.kt          # Xposed 入口（IXposedHookLoadPackage）
    ├── HookManager.kt          # Hook 总调度 + 兜底类名/方法名常量表
    ├── ImGuiOverlay.kt         # 调试悬浮日志层（分级、过滤、暂停）
    ├── config/
    │   └── ConfigManager.kt    # 配置读写（模块 SP ↔ 远程 RemotePreferences）
    ├── constant/
    │   └── SPConstants.kt      # SP 键名与默认值常量
    ├── global/                 # 全局 Application/ClassLoader（DragonGlobals、XposedGlobals）
    ├── locate/                 # 混淆目标定位
    │   ├── LocatorService.kt   # DexKit 异步扫描调度
    │   ├── DexKitResolver.kt   # DexKit 特征匹配
    │   ├── DragonTargets.kt    # 目标 ID 与定位特征定义
    │   ├── TargetSpec.kt       # 目标规格（类名/方法特征 hint）
    │   └── LocationCache.kt    # 定位结果持久化缓存（版本号绑定）
    ├── utils/                  # 工具类（日志、SP、网络、反射、JSON 等）
    ├── web/                    # HTTP 服务
    │   ├── HttpServer.kt       # NanoHTTPD 服务器 + 路由分发（endsWith 匹配）
    │   ├── DragonController.kt # 路由控制器（参数解析/校验 + 书源模板）
    │   └── DragonService.kt    # RPC 转发业务逻辑
    └── xcallback/              # 各 Hooker 实现
        ├── SettingActivityHooker.kt    # 设置页标题栏注入 FQ++ 入口
        ├── MainApplicationHooker.kt    # Application 启动（HTTP 服务拉起）
        ├── UpdateManagerHooker.kt      # 阻止更新
        ├── AcctManagerHooker.kt        # 用户信息/VIP 拦截（本地会员）
        ├── PrivilegeManagerHooker.kt   # 会员特权判定 → true
        ├── VipInfoModelHooker.kt       # VIP 信息模型注入
        ├── BookUtilsHooker.kt          # 解封书籍
        ├── UiCleanHooker.kt            # 界面净化
        └── InstantRewardHooker.kt      # 激励秒领
```

仓库根目录的 `curl-test/`（接口测试脚本与样例响应）与 `dec/`（反编译分析文档：混淆映射、接口分析）可作为适配新版本的参考资料。

## 架构机制

### 混淆定位（DexKit + 缓存 + 延迟兜底）

1. **缓存优先**：上次定位成功且番茄版本号（versionCode）匹配 → 直接使用持久化映射表，毫秒级启动，不初始化 DexKit；
2. **异步扫描**：无缓存或版本变化时，`XposedEntry` 触发 DexKit 异步扫描（`LocatorService`），`HookManager.ensureLocated()` 等待结果；
3. **常量表兜底**：定位失败的目标回退到 `HookManager.knownFallback`（7.0.9.32 反编译验证的类名），再失败则跳过对应 hook 并记录日志；
4. **延迟兜底**：额外 hook `Instrumentation.callActivityOnCreate` —— 进入设置页时运行时反射定位注入；进入首个业务 Activity（主页）时后台强制重定位 + 幂等重注入，此时目标类已全部加载，定位成功率最高。

### 配置同步

配置保存在模块 App 自身 SharedPreferences，番茄进程通过 libxposed API 102 的 `getRemotePreferences()` 跨进程只读读取；`MainActivity` 会预热创建配置 SP 文件，确保 hook 进程可读取。

### Hook 开关

开关判断集中在 `HookManager.hookAll()`：功能关闭时根本不挂载对应 hook，避免空挂载开销；界面净化/激励秒领的目标类多在业务页面加载后才存在，因此每个 Activity 创建时幂等重装（`hookedMethods` 集合防重复）。

## 适配新版本

番茄小说每次版本更新可能改变混淆类名/方法名：

1. 先参考 `dec/` 目录下的混淆映射文档与 `curl-test/` 样例响应；
2. 更新 [DragonTargets.kt](../app/src/main/java/com/xxhy/fqpp/xposed/locate/DragonTargets.kt) 中的定位特征（方法签名/字段 hint），DexKit 会自动重新定位；
3. 混淆类名兜底常量（`HookManager` 顶部常量区）与已验证方法名需按新版反编译结果核对；
4. 清除应用数据或升级版本号以触发定位缓存失效。

## 贡献指南

- 欢迎提交 Issue 或 PR；
- 保持代码清晰，遵循 [Android 编码规范](https://source.android.com/docs/setup/contribute/code-style)；
- 如果添加新接口，请同时更新书源模板（[DragonController.bookSource](../app/src/main/java/com/xxhy/fqpp/xposed/web/DragonController.kt)）与 [API 文档](API.md)；
- 新增功能开关时，请同步更新 [SPConstants.kt](../app/src/main/java/com/xxhy/fqpp/xposed/constant/SPConstants.kt)、[ConfigManager](../app/src/main/java/com/xxhy/fqpp/xposed/config/ConfigManager.kt)、配置页 UI 与 [使用说明](USAGE.md)；
- 版本变更请记录到 [更新日志](CHANGELOG.md)。
