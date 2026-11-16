<div align="center">

# 🍅 FQ++

**把阅读还给自由**

基于 Xposed 的番茄小说增强模块 · RPC → HTTP API · 全端阅读

[![License](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![Xposed](https://img.shields.io/badge/Xposed-LSPosed%20%E6%8E%A8%E8%8D%90-success)](https://github.com/LSPosed/LSPosed)
[![API](https://img.shields.io/badge/Android-8.0%2B-brightgreen)]()
[![Target](https://img.shields.io/badge/%E7%95%AA%E8%8C%84%E5%B0%8F%E8%AF%B4-7.x-orange)]()
[![Release](https://img.shields.io/badge/%E4%B8%8B%E8%BD%BD-Releases-blue)](https://github.com/XuxiaHaiyun/FQplusplus/releases)

[安装](docs/INSTALL.md) · [使用说明](docs/USAGE.md) · [API 文档](docs/API.md) · [开发指南](docs/DEVELOPMENT.md) · [更新日志](docs/CHANGELOG.md)

</div>

---

## 简介

**FQ++** 是一个面向「番茄免费小说」（`com.dragon.read`）的 Xposed 模块。它将 App 内部的 **RPC 接口转换为标准 HTTP API**，让局域网内任意设备 —— 手机、平板、电脑、电子书阅读器 —— 都能自由访问番茄的小说与有声书资源。

> 🧩 内置一键导入的 [开源阅读 (Legado)](https://github.com/gedoor/legado) 书源，一次配置，全端阅读。

## ✨ 特性

| | 功能 | 说明 |
|---|---|---|
| 🌐 | **RPC → HTTP API** | 内置轻量 HTTP 服务器（NanoHTTPD），搜索 / 详情 / 目录 / 正文 / 有声书等接口一览俱全 |
| 📚 | **对接开源阅读** | 预设书源模板，`legado://` 协议一键导入，无广告、无限流 |
| 🛡️ | **本地会员模拟** | 模拟 VIP 身份、隐藏会员广告入口（需登录） |
| 🧹 | **界面净化** | 一键隐藏全屏广告、会员特权卡、红点角标、「赚钱 / 短剧」导航等 15+ 骚扰板块，支持细分开关 |
| 🔓 | **解封书籍** | 解除部分下架 / 封禁书籍的阅读限制 |
| 🎁 | **激励秒领** | 跳过激励视频广告，奖励直接到账 |
| 🔊 | **有声书支持** | 音频播放地址获取（音频解密 TODO） |
| 🛠️ | **运行环境加固** | 屏蔽更新、禁用热更新（Reparo）、禁用插件加载、禁用崩溃上报 |
| ⏱️ | **阅读时长 5x** | 阅读计时按 5 倍速度累积，快速刷满每日时长奖励 |
| ⚙️ | **更多辅助** | 调试悬浮层（分级日志 / 关键词过滤）、随应用启动等 |

> 📸 完整功能开关与说明见 [使用说明](docs/USAGE.md)。

## 📱 截图展示

<div align="center">

![FQ++ 截图预览](screenshots/preview.png)

*控制面板 · 书城净化效果*

</div>

## ⚠️ 功能提醒

- 🔄 **配置需重启生效**：所有配置保存在模块内部，修改后需 **强制停止并重启番茄小说** 才会加载。
- 🎯 **版本适配**：当前适配番茄小说 **7.0.9.32**，其余版本可能因混淆变动而失效；若模块失效，优先检查 App 是否自动更新（建议开启「阻止更新」）。
- 🧩 **禁用插件加载慎用**：开启「禁用插件加载」可能导致 App 崩溃，遇到问题请先关闭该项。
- 📖 **「隐藏浏览历史」暂勿开启**：该净化细分项存在已知问题，会影响正常使用，请保持关闭。
- 🔓 **解封书籍有限制**：仅部分书籍有效（作者页 / 推书页入口可用），不保证 100% 解封。

## 🚀 快速开始

1. 安装 [LSPosed](https://github.com/LSPosed/LSPosed) 框架
2. 下载并安装 [最新 Release](https://github.com/XuxiaHaiyun/FQplusplus/releases) APK
3. 在 LSPosed 中启用模块，作用域勾选 **番茄免费小说**
4. 强制停止番茄小说后重新打开 → **我的 → 设置** → 点击标题栏右上角 **FQ++** 按钮查看配置并跳转模块配置页

详细步骤与常见问题 → [安装文档](docs/INSTALL.md)

## 📖 文档

| 文档 | 说明 |
|------|------|
| [安装与激活](docs/INSTALL.md) | 环境要求、安装步骤、激活与故障排查 |
| [使用说明](docs/USAGE.md) | 控制面板、功能开关、书源导入 |
| [API 接口](docs/API.md) | HTTP API 端点、参数、请求示例 |
| [开发指南](docs/DEVELOPMENT.md) | 编译构建、项目结构、贡献指南 |
| [更新日志](docs/CHANGELOG.md) | 版本变更记录 |

## ⚠️ 免责声明

本模块仅供 **技术研究与个人学习** 使用，严禁用于商业用途。使用本模块可能违反番茄免费小说用户协议，由此产生的账号风险与法律责任由使用者自行承担。请尊重知识产权，**支持正版**。

## 📄 许可证

[Apache License 2.0](LICENSE) © 2025 FQ++ Contributors
