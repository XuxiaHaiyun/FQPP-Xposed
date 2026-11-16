# FQ++ HTTP API 文档

通过内置的 NanoHTTPD 服务器，FQ++ 将番茄小说内部的 RPC 接口暴露为标准 HTTP API，支持局域网内任意设备访问。API 遵循 REST 风格，以 JSON 为主要数据交换格式，并提供跨域（CORS）支持以便浏览器或第三方应用调用。

---

## 基础信息

- **基础 URL**：`http://<设备IP>:<端口>/`
- **默认端口**：`9999`（可在 FQ++ 控制面板中修改，取值 0–65535）
- **HTTP 方法**：`GET` 或 `POST`
- **参数传递**：GET 请求使用 URL Query 参数；POST 请求使用 `application/x-www-form-urlencoded`
- **响应编码**：`UTF-8`（服务端会为缺失 charset 的请求自动补上 UTF-8，避免中文乱码）
- **MIME 类型**：大多数返回 `application/json; charset=UTF-8`；`/status` 返回 `text/plain`，无匹配路由返回 `text/html`（404 页面）
- **CORS**：允许跨域，`Access-Control-Allow-Origin` 为请求头中的 Origin（无 Origin 时为 `*`），`Access-Control-Allow-Methods: GET,POST`
- **路由匹配**：URI **以路由路径结尾**即视为匹配（例如 `/api/reading/bookapi/detail` 也能命中 `/reading/bookapi/detail`）
- **调试模式**：控制面板开启「调试模式」后，JSON 响应以格式化（缩进）形式返回，便于阅读，但会增大传输体积
- **错误处理**：失败时返回 HTTP `500`，响应体为 JSON 序列化的异常信息；无匹配路由返回 **404** HTML 页面

> ⚠️ 本 API 是番茄小说内部 RPC 的直接映射，参数名与原始调用保持一致（蛇形命名如 `book_id`）。建议配合 FQ++ 日志功能观察实际请求参数，便于调试。

---

## 全局状态检查

### `GET /status`

测试服务器是否运行，返回字符串 `ok!`，状态码 `200`。

**示例请求：**
```bash
curl http://192.168.1.100:9999/status
```

**响应：**
```
ok!
```

---

## 搜索

### `GET/POST /reading/bookapi/search/tab`

搜索书籍（聚合搜索）。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `query` | string | 是 | 搜索关键字 |
| `page` | int | 否（默认 1） | 页码 |
| `count` | int | 否（默认 10） | 每页数量 |
| `tab_type` | int | 否（默认 3） | 搜索标签类型：`1`=Common, `2`=Voice, `3`=Book, `4`=Topic, `5`=Content, `8`=Comic, `18`=ShortStory |

**响应说明：**

返回番茄聚合搜索原始 JSON（含 `searchTabs` 数组）。FQ++ 会将目标 `tab_type` 对应的标签**重排到 `searchTabs` 首位**并设置 `selectedTabIdx=0`，保证书源规则 `$.searchTabs[0].data` 能直接取到书籍列表。

---

## 书籍详情

### `GET/POST /reading/bookapi/detail`

获取单个书籍的详细信息。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `book_id` | string | 是 | 书籍 ID |
| `source` | int | 否（默认 0） | 详情来源：`0`=Default, `1`=Cover, `2`=NewVideoTab, `4`=VideoDetail, `6`=AudioPlayer |
| `without_video` | bool | 否（默认 false） | 是否不包含视频信息（`true`/`1` 为真） |

### `GET/POST /reading/bookapi/multi-detail`

批量获取多本书详情。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `book_id` | string | 是 | 逗号分隔的书籍 ID 列表，如 `123,456,789` |

---

## 章节目录

### `GET/POST /reading/bookapi/directory/all_items`

获取一本书的章节目录。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `book_id` | string | 是 | 书籍 ID |

---

## 章节正文

### `GET/POST /reading/reader/full`

获取单个章节的正文内容。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `item_id` | string | 是 | 章节 ID |
| `book_id` | string | 是 | 书籍 ID |
| `novel_text_type` | int | 否（默认 0） | 文本类型：`0`=Normal, `1`=RichText |

### `GET/POST /reading/reader/batch_full`

批量获取多个章节的正文内容。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `book_id` | string | 是 | 书籍 ID |
| `item_ids` | string（多值） | 是 | 章节 ID 列表，**重复多个 `item_ids=xxx` 参数**（非逗号分隔），空值自动跳过 |

---

## 有声书音频

### `GET/POST /reading/bookapi/audio/playurl`

获取有声书章节的音频播放地址。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `book_id` | string | 是 | 书籍 ID |
| `item_id` | string | 是 | 章节 ID |
| `tone_quality` | int | 否（默认 128） | 音质：`32`=LowQuality, `64`=MediumQuality, `128`=HighQuality |
| `tone_id` | long | 否（默认 0） | 音色 ID，`0` 使用默认音色（80=多角色对话升级版） |

### `GET/POST /reading/bookapi/audio/decrypt`

> 🚧 **TODO：暂未支持。** 该接口路由已预留（音频解密，计划返回解密后的 `audio/mpeg` 音频流），功能尚未实现，调用将返回异常响应。请先使用 `/reading/bookapi/audio/playurl` 获取播放地址。

---

## 书架

### `GET/POST /reading/bookapi/bookshelf/info`

获取当前用户的书架列表（**需要已在番茄小说 App 内登录**）。

**请求参数：** 无。

### `GET/POST /reading/bookapi/bookshelf/add`

添加书籍到书架。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `book_id` | string | 是 | 要添加的书籍 ID |
| `type` | string | 否 | 书架类型 |

---

## 书城与分类

### `GET/POST /reading/bookapi/bookmall/cell/change`

获取首页推荐、分类轮播等书城数据，参数透传 RPC 层（如 `cellId` 等，具体键名以番茄原始接口为准）。

### `GET/POST /reading/bookapi/new_category/landing`

获取新分类落地页数据，参数透传 RPC 层（如 `categoryId` 等）。

---

## 评论

### `GET/POST /novel/commentapi/comment/list`

评论列表，支持段评 / 章评 / 书评。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `item_id` | string | 是 | 章节 ID（段评/章评）或书籍 ID（书评） |
| `book_id` | string | 段评/章评必填 | 书籍 ID（用于自动获取章节版本） |
| `comment_type` | int | 否（默认 1） | 评论类型：`0`=章评, `1`=段评, `2`=书评 |
| `para_index` | int | 否（默认 0） | 段落索引（段评时使用） |
| `item_version` | string | 否 | 章节版本号，不传则自动获取 |
| `count` | int | 否（默认 20） | 请求数量 |
| `cursor` | string | 否 | 分页游标 |
| `sort` | int | 否（默认 1） | 排序：`1`=SmartHot, `2`=TimeAsc, `3`=TimeDesc |
| `fold_type` | int | 否（默认 0） | 折叠类型：`0`=Normal, `1`=Unfold, `2`=Fold |
| `tag` | string | 否 | 话题标签（书评时可用） |
| `need_count` | bool | 否（默认 true） | 是否需要计数 |

### `GET/POST /novel/commentapi/idea/list`

评论气泡（段内想法）数据。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `item_id` | string | 是 | 章节 ID |
| `book_id` | string | 推荐传入 | 书籍 ID（用于自动获取 `item_version`） |
| `item_version` | string | 否 | 章节版本号，不传则通过 `book_id` 自动获取 |

### `GET/POST /novel/commentapi/reply/list`

子评论（回复）列表。

**请求参数：**

| 参数 | 类型 | 必要性 | 说明 |
|------|------|--------|------|
| `comment_id` | string | 是 | 父评论 ID |
| `group_id` | string | 否 | 分组 ID（章节 ID / 书籍 ID） |
| `book_id` | string | 否 | 书籍 ID |
| `comment_type` | int | 否（默认 0） | 评论类型：`0`=章评, `1`=段评, `2`=书评 |
| `count` | int | 否（默认 20） | 请求数量 |
| `cursor` | string | 否 | 分页游标 |

---

## 书源导出（支持 Legado 开源阅读）

### `GET /booksource`

返回符合开源阅读（Legado）格式的书源 JSON **数组**，书源名称 **FQ Helper Simple**（分组「番茄」），端口占位符会自动替换为当前配置端口，可直接用于「网络导入」。

**请求参数：** 无（可直接通过浏览器访问）。

**使用方式：**

1. 打开开源阅读 App → 书源管理 → 右上角菜单 → 「网络导入」
2. 输入 `http://<设备IP>:9999/booksource`
3. 点击确定，导入成功后即可搜索和使用（探索页即书架）。

## TODO / 规划中

以下接口尚未实现，欢迎贡献：

| 接口 | 说明 |
|------|------|
| 音频解密 `/reading/bookapi/audio/decrypt` | 下载加密音频并解密，返回 `audio/mpeg` 音频流 |
| 漫画接口 | 漫画内容浏览 / 章节图片获取等 |
| 短剧接口 | 短剧列表 / 播放地址等 |

---

## 错误响应

当服务器内部发生异常（如参数缺失、RPC 调用失败等）时，返回 HTTP 500，响应体为 JSON 序列化的异常信息（含异常类型、message、堆栈等字段）；参数校验失败（如缺少必填参数）时返回中文提示，例如：

```json
{
    "message": "参数 book_id 不能为空"
}
```

当请求的 URI 无对应路由时，返回 **404** HTML 页面。

---

## 注意事项

1. 所有 API 均直接转发自番茄小说 RPC 接口，部分服务（如书架、添加书架）需要先在番茄小说 App 内登录。
2. RPC 响应为番茄原始数据结构，上文未给出固定响应示例；建议开启 FQ++ 的调试模式或日志功能，观察实际返回结构。
3. 服务器运行在番茄小说进程内，监听 `0.0.0.0`，同一局域网内其他设备均可访问，请注意网络安全。
4. 设备 IP 变化后书源需要更新，建议使用 DHCP 静态地址或主机名。
5. HTTP 服务需在番茄小说进程内启动后才可访问（见[使用说明](USAGE.md)）。

---

*FQ++ 尊重版权，本 API 仅供个人学习与研究使用。*
