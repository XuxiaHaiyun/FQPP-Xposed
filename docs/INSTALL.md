# 安装与激活

## 环境要求

| 条件 | 说明 |
|------|------|
| Android 版本 | 8.0 (API 26) 及以上 |
| 系统框架 | Xposed / EdXposed / LSPosed（**推荐 LSPosed**，模块基于 libxposed API 102 开发） |
| 目标 App | 番茄免费小说 `com.dragon.read`，适配验证版本 **7.0.9.32** |
| 可选依赖 | [开源阅读 (Legado)](https://github.com/gedoor/legado) 或其他支持自定义书源的阅读器 |

> ⚠️ 番茄小说每次版本更新可能改变混淆类名/方法名。模块已内置 DexKit 动态定位与兜底类名常量表（见 [HookManager.kt](../app/src/main/java/com/xxhy/fqpp/xposed/HookManager.kt) 注释）。其余版本（如 7.2.7）部分混淆已有变动，升级版本后需重新定位。

## 安装步骤

1. 确保设备已安装 **LSPosed**（或 Xposed / EdXposed）框架。
2. 下载并安装最新版 APK（`applicationId: com.xxhy.fqpp`）：
   - [Releases 下载](https://github.com/XuxiaHaiyun/FQplusplus/releases)
   - 或自行编译，见 [开发指南](DEVELOPMENT.md)
3. 打开框架管理器，在模块列表中勾选 `FQ++`。
4. **作用域**：模块已通过 `xposedscope` 限定为 `com.dragon.read`，仅需勾选「番茄免费小说」。
5. 强制停止番茄小说 App（或重启系统）使模块生效。

## 验证激活

有两种方式确认模块已激活：

1. **FQ++ 模块 App**：打开 FQ++ 应用，首页状态卡片显示「模块已激活」（含框架连接状态与作用域 `com.dragon.read`）；未激活时可点击卡片上的按钮请求作用域授权。
2. **番茄小说内**：打开番茄小说 → 进入「我的」→ **设置**，标题栏右上角将出现 **FQ++** 按钮，点击弹出配置弹窗。

若未激活，请检查：

- LSPosed 中模块是否已启用、作用域是否勾选番茄小说；
- 是否已强制停止番茄小说并重新打开；
- 番茄小说版本是否过新导致 Hook 失效（参考上方版本说明，建议开启「阻止更新」防止自动升级覆盖模块效果）。
