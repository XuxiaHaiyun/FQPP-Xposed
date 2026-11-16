# 番茄小说 Android 网络接口分析文档

## 概述

本文档基于 Google sdk_gphone64_x86_64 模拟器抓包数据，分析番茄小说 Android 应用（版本 7.0.9.32）中 fqnovel.com、snssdk.com、zijieapi.com 三个域名的网络接口。

## 数据来源

- **设备**: Google sdk_gphone64_x86_64 (Android 16)
- **应用**: 番茄小说 (novelapp) v7.0.9.32
- **抓包时间**: 2026-06-06
- **总请求数**: 1007 条

---

## 一、fqnovel.com 接口分析

### 1.1 域名概述

fqnovel.com 是番茄小说的核心业务域名，主要承载小说阅读、评论、用户VIP、消息等核心功能。

| 子域名 | 请求数 | 主要功能 |
|--------|--------|----------|
| api5-normal-sinfonlinea.fqnovel.com | 646 | 核心API服务 |
| log0-applog-lq.fqnovel.com | 30 | 日志上报 |
| mon11-misc-lq.fqnovel.com | 13 | 监控服务 |
| api5-normal-lq.fqnovel.com | 10 | API备用节点 |

### 1.2 核心接口列表

#### 书籍与章节

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/directory/all_infos/v` | 43 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /luckycat/novel/v1/resource/end_chapter` | 14 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/multi-detail/v` | 13 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /reading/bookapi/read_history/update/v` | 11 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /reading/bookapi/read_progress/get/v` | 9 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/search/cue/v` | 8 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/bookmall/tab/v` | 7 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /reading/bookapi/bookshelf/banner/v` | 6 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/bookmall/cell/change/v` | 6 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/search/suggest/v` | 6 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/read_history/list/v` | 5 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/reader/book/recommend_data_plan/v` | 5 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/plan/v` | 4 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/directory/all_items/v` | 4 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/bookapi/audio/toneinfo/` | 4 | 200 | api5-normal-sinfonlinea.fqnovel.com |

#### 评论系统

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/comment/list/7532019656236747801/v1` | 19 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/comment/list/7143038691944959011/v1` | 15 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/comment/list/6883748331202284558/v1` | 14 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/user_config/get/v1/` | 4 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/idea/list/7176259295829623296/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/idea/list/7176601650042241568/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/idea/list/7176259067000652320/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/idea/list/7176601374652793396/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/idea/list/7532781844043924030/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/idea/list/7532870881249346110/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/idea/list/6883754705973936653/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/idea/list/6883755139623191048/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/idea/list/6883754939718304264/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/comment/list/7584393010956209176/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/commentapi/comment/list/7632639015547587646/v1` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

#### 用户与VIP

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /reading/user/vip/promotion/strategy_list/v` | 21 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/user/info/v` | 13 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /luckycat/novel/v1/task/new_user_signin/detail` | 9 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /reading/tradeapi/vip/multi_product/info/v1/` | 8 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /reading/user/event/report` | 7 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /luckycat/novel/v1/user/loss_status` | 4 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/tradeapi/vip/product/info/v1/` | 3 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /luckycat/novel/v1/user/page_tab` | 3 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/user/ad_free/pre_save/panel/v:version/` | 3 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/user/share/info/v` | 3 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /reading/user/recommend_feature/report/v` | 2 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/ugc/follow/get_user_relation/v` | 2 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/user/vip/privilege/info/v` | 2 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/user/shop_mall/user_info/v` | 2 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /luckycat/novel/v1/popup/referral_user_popup` | 2 | 200 | api5-normal-sinfonlinea.fqnovel.com |

#### 消息通知

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/msgapi/getnewmsggroupcount/v` | 16 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /reading/msgapi/syncAll/v` | 12 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/msgapi/notifylist/v` | 4 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/msgapi/msg_group_list/v` | 2 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/msgapi/get_red_dot_notify/v` | 1 | 200 | api5-normal-sinfonlinea.fqnovel.com |

#### 日志上报

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /service/2/app_log/` | 28 | 200 | log0-applog-lq.fqnovel.com |

#### 其他

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `CONNECT ` | 25 | 0 | rtlog3-applog-lq.fqnovel.com, api3-normal-lq.fqnovel.com, api3-normal-sinfonlinea.fqnovel.com, log0-applog-lq.fqnovel.com, api5-normal-sinfonlinea.fqnovel.com, log3-applog-lq.fqnovel.com, mon11-misc-lq.fqnovel.com, mon3-misc-lq.fqnovel.com, api5-normal-lq.fqnovel.com, rtlog5-applog-lq.fqnovel.com, frontier100-toutiao-lq.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /reading/ugc/ab/config/v` | 16 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/commerceapi/order/status/v1/` | 9 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/player/multi_video_model/v1/` | 8 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/reader/audio/timepoint/` | 8 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /novel/player/multi_video_detail/v1/` | 7 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/ugc/writer_center/unread_count/v` | 7 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/ugc/topic/get_unread_count/v` | 7 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /luckycat/novel/v1/v_lab/get_single_ab` | 7 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /luckycat/novel/v1/v_lab/get_ab_info` | 7 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /luckycat/novel/v1/task/list` | 6 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /luckycat/novel/v1/task/get_ad_info` | 6 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading/reader/bonus/ad_reward/v` | 6 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /luckycat/novel/v1/consume/consumption_strategy` | 6 | 200 | api5-normal-sinfonlinea.fqnovel.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /monitor/collect/batch/` | 6 | 200 | mon11-misc-lq.fqnovel.com |

### 1.3 详细接口说明

#### 获取章节目录信息

**接口路径**: `GET /reading/bookapi/directory/all_infos/v`

**请求示例**:
```
URL: https://api5-normal-sinfonlinea.fqnovel.com/reading/bookapi/directory/all_infos/v?item_ids=6890136922304283143%2C7008897111747985957%2C7463748909999850009%2C7463956867614507545%2C7623370522562150937%2...
```

**请求参数**:
- `item_ids`: 6890136922304283143,7008897111747985957,7463748909999850009,7463956867614507545,7623370522562150937,7623476421758812734,7623743153568219673,7623828151470129726,7624112704768180761,7624215733643264537,7624487514971046425,7624590119978156569,7624839624887632409,7624877229956137497,7625270894893482521,7625307572915929625,7625612176031498777,7625702432831439385,7625944796942565913,7626077779523535385,7626329296272900633,7626444256814580249,7626721911333995033,7626831702265954841,7627091454988403225,7627182807978082878,7627451810252341785,7627570944654967320,7627846056151106073,7627943959750263358,7628199731746849304,7628307679068307992,7628585446745834046,7628672903705264702,7628938493766418968,7629050723430318617,7629318047408325144,7629422147835789849,7629675739557659198,7629796269220692505,7630060060659434046,7630168374596600345,7630425750629253694,7630541130974626366,7630803104153207320,7630914181222384190,7631174694435029566,7631228588322193982,7631549664952582718,7631602377392407102,7631923889169236542,7631978605244858905,7632297450316775998,7632362135791600153,7632677743800369689,7632729825727234585,7633049851537654334,7633100280606769688,7633423190089286169,7633475975518355992,7633778245036425790,7633834960780001854,7634191174621741630,7634215705600066073,7634540555392778776,7634579008268550681,7634912459832181310,7634953671955989054,7635285087096947225,7635325072411984409,7635655976846099006,7635696402525323838,7636032749777715736,7636071470858240536,7636385480832991769,7636429842480644632,7636770286422999577,7636810439287898648,7637148515705045528,7637180331094704664,7637523607752540696,7637564088398119486,7637852661332591128,7637960992780993086,7638231801571312153,7638336833364967998,7638594603125326361,7638705514007773758,7638960998258328126,7639076552008794686,7639348266588439064,7639452568103420441,7639746184742961689,7639838746237485630,7640101093883527704,7640213402987545112,7640457976582504984,7640550465960149529,7640821986876211774,7640924196305912382,7641204754936234521,7641294869830304318,7641555082114236952,7641673807781954110,7641943572379337240,7642049608935277081,7642315304030175768,7642418557761896984,7642696317524771390,7642749892816814654,7643059491163882046,7643119606223077913,7643443878846005785,7643489513318122046,7643815107054535193,7643868685148373528,7644189657722274329,7644254507341185561,7644551745888076313,7644604090005078552,7644938523325252121,7644975686649643545,7645326040818008638,7645373857133511230,7645651107053584920,7645743295108235800,7646021359583101502,7646118503379894809,7646378229548204569,7646490416946102809,7646759256741397054,7646858649062621758,7647138758910804542,7647227388555706904,7647492311588274750,7647595805984965144,7647885823630639640,7647967447760257560
- `with_virtual_directory`: false
- `with_chapter_abstract`: false
- `without_tone_infos`: true
- `iid`: 2877876566592665
- `device_id`: 2877876567030921
- `ac`: mobile
- `channel`: vivo_1967_64
- `aid`: 1967
- `app_name`: novelapp
- `version_code`: 70932
- `version_name`: 7.0.9.32
- `device_platform`: android
- `os`: android
- `ssmix`: a
- `device_type`: sdk_gphone64_x86_64
- `device_brand`: google
- `language`: zh
- `os_api`: 36
- `os_version`: 16

**响应体** (前500字符):
```
{"code":0,"message":"SUCCESS","data":[{"book_id":"6883748331202284558","group_id":"6890136922304283143","item_id":"6890136922304283143","origin_chapter_title":"","title":"第50章 慕容来访","vid":"has-vid","order":"50","version":"0229aa28f1bfd4fd1fa506741f50412c_1_167c90e3ceb8826","real_chapter_order":"","first_pass_time":"1604235023","volume_name":"第一卷：默认","content_md5":"0229aa28f1bfd4fd1fa506741f50412c","chapter_word_number":3194,"publish_time":"","chapter_thumb_url":"","audio_match_infos":[{"match_it
```

---

#### 应用日志上报

**接口路径**: `POST /service/2/app_log/`

**请求示例**:
```
URL: https://log0-applog-lq.fqnovel.com/service/2/app_log/?version_code=70932&device_platform=android&device_id=2877876567030921&aid=1967&iid=2877876566592665&tt_data=a...
```

**请求参数**:
- `version_code`: 70932
- `device_platform`: android
- `device_id`: 2877876567030921
- `aid`: 1967
- `iid`: 2877876566592665
- `tt_data`: a

**响应示例**:
```json
{
  "blocklist": {
    "v1": [],
    "v3": [
      "luckydog_plugin_aar_loaded",
      "lynx_ad_request_monitor"
    ]
  },
  "magic_tag": "ss_app_log",
  "message": "success",
  "server_time": 1780708375
}
```

---

#### VIP促销策略列表

**接口路径**: `POST /reading/user/vip/promotion/strategy_list/v`

**请求示例**:
```
URL: https://api5-normal-sinfonlinea.fqnovel.com/reading/user/vip/promotion/strategy_list/v?iid=2877876566592665&device_id=2877876567030921&ac=mobile&channel=vivo_1967_64&aid=1967&app_name=novelapp&version...
```

**请求参数**:
- `iid`: 2877876566592665
- `device_id`: 2877876567030921
- `ac`: mobile
- `channel`: vivo_1967_64
- `aid`: 1967
- `app_name`: novelapp
- `version_code`: 70932
- `version_name`: 7.0.9.32
- `device_platform`: android
- `os`: android
- `ssmix`: a
- `device_type`: sdk_gphone64_x86_64
- `device_brand`: google
- `language`: zh
- `os_api`: 36
- `os_version`: 16
- `manifest_version_code`: 70932
- `resolution`: 1080*2400
- `dpi`: 420
- `update_version_code`: 70932

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "from": 6,
      "show": false
    },
    {
      "from": 2,
      "show": true,
      "listening_strategy": [
        {
          "scene": 9,
          "show": true,
          "extra_info": {
            "sub_type": 3,
            "coupon_strategy": "not_show"
          }
        }
      ]
    },
    {
      "from": 1,
      "show": false
    },
    {
      "from": 3,
      "show": false
    },
    {
      "from": 8,
      "show": true,
      "ad_strategy": [
        {
          "position": 1,
          "entrance_text": "",
          "sub_scene": 5,
          "coupon_strategy": "not_show"
        },
        {
          "position": 1,
          "entrance_text": "",
          "sub_scene": 6,
          "coupon_strategy": "not_show"
        }
      ]
   
```

---

---

## 二、snssdk.com 接口分析

### 2.1 域名概述

snssdk.com 是字节跳动的公共服务域名，主要用于推送服务、设置同步、广告服务等。

| 子域名 | 请求数 | 主要功能 |
|--------|--------|----------|
| reading-hl.snssdk.com | 11 | 阅读相关推送 |
| is-hl.snssdk.com | 10 | 设置服务 |
| i-hl.snssdk.com | 10 | 信息服务 |
| is.snssdk.com | 2 | 设置同步 |

### 2.2 核心接口列表

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /cloudpush/pull_alter/sdk_strategy` | 9 | 200 | reading-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /service/settings/v3/` | 7 | 200 | is-hl.snssdk.com, i-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /service/settings/v3/` | 7 | 200 | is-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /api/neptune/v3/sdk/PackLiveSDK` | 4 | 200 | i-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /service/2/app_alert_check/` | 2 | 200 | ichannel.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /location/info/` | 2 | 200 | i-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /api/ad/v1/scheme/query/` | 1 | 200 | i-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /api/ad/v1/scheme/report/` | 1 | 200 | i-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /activity/carrier_flow/query_flow/` | 1 | 200 | i-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `CONNECT ` | 1 | 0 | is.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading_offline/drlynx/search-tab-full-text/template.js` | 1 | 200 | reading-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /reading_offline/drlynx/search-tab-user/template.js` | 1 | 200 | reading-hl.snssdk.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /service/settings/v2/` | 1 | 200 | is.snssdk.com |

### 2.3 详细接口说明

#### POST /cloudpush/pull_alter/sdk_strategy

**请求示例**:
```
URL: https://reading-hl.snssdk.com/cloudpush/pull_alter/sdk_strategy?country=CN&push_sdk_version=30922&iid=2877876566592665&channel=vivo_1967_64&device_type=sdk_gphone64_x86_64&language=zh&os_detail_type=android&device_manufacturer=Google&update_version_code=70932&rom=14574095&os_api=36&tz_name=Asia%2FSh...
```

**响应示例**:
```json
{
  "error_code": 0,
  "message": "success",
  "data": {
    "form_data": "",
    "frequency_control": 1,
    "group_id": 0,
    "model_score": 0,
    "rid": 0,
    "threshold": 0
  },
  "strategy_version": 1
}
```

#### GET /service/settings/v3/

**请求示例**:
```
URL: https://i-hl.snssdk.com/service/settings/v3/?iid=2877876566592665&sdk_version_name=0.3.4-rc.7&is_foreground=true&device_type=sdk_gphone64_x86_64&language=zh&widget_window_w=1080&os_detail_type=android&device_manufacturer=Google&update_version_code=70932&rom=14574095&caller_name=UserEngagement&widget...
```

**响应示例**:
```json
{
  "data": {
    "__logid": "20260606091255AA3982F19AC3B01F2536",
    "settings_time": 1780708375,
    "settings": {
      "sdk_key_UserEngagement": {
        "enable_icon_widget": false,
        "icon_widget_ui_config": {
          "enable_icon_widget": false
        },
        "refactor_build_remote_view_method": "V1",
        "widget_add_config": {
          "dialog_show_time_out": 1000,
          "support_widget_add_ability_type_list": [
            1
          ],
          "default_widget_add_ability_type": 1,
          "widget_add_callback_timeout": 1000,
          "disable_dialog_back_
```

#### POST /service/settings/v3/

**请求示例**:
```
URL: https://is-hl.snssdk.com/service/settings/v3/?aid=1967&iid=2877876566592665&device_id=2877876567030921&channel=vivo_1967_64&device_platform=android&version_code=7.0.9.32&caller_name=interaction_sdk&ctx_infos&os_version=36&device_type=sdk_gphone64_x86_64&luckydog_base=vAHxBG0cSuuahta4QRNw9_fAiOUJy399...
```

**响应示例**:
```json
{
  "data": {
    "__logid": "2026060609130633444C50E9651B0B4E2F",
    "settings_time": 1780708386,
    "settings": {
      "predefine": {
        "open_js_predefine": true
      },
      "bugfix_config": {
        "should_use_async": 1
      }
    },
    "vid_info": {},
    "downstream_status": {
      "ab_loader_status": true
    },
    "ctx_infos": "eyJlIjoiUGtxcFE1OHdJUFJMTlJNTjNKUFplZz09IiwidiI6NDY0MjkyMH0="
  },
  "message": "success"
}
```

#### POST /api/neptune/v3/sdk/PackLiveSDK

**请求示例**:
```
URL: https://i-hl.snssdk.com/api/neptune/v3/sdk/PackLiveSDK?iid=2877876566592665&device_id=2877876567030921&ac=mobile&channel=vivo_1967_64&aid=1967&app_name=novelapp&version_code=70932&version_name=7.0.9.32&device_platform=android&os=android&ssmix=a&device_type=sdk_gphone64_x86_64&device_brand=google&lan...
```


#### GET /service/2/app_alert_check/

**请求示例**:
```
URL: https://ichannel.snssdk.com/service/2/app_alert_check/?device_id=2877876567030921&aid=1967&tt_info=dGMFEAAAsU2G7CAMtdZ9WEZeKHdaBUwwYduyx8nYoBgWbaveeVzX29mAtJDETBnRXYOQn9s0Bk-r-Z2TErfA6qTAq8uNlQ66TqkGG7yCtWoze1C8qe8JSAGU_pB3obC1F6HTfDoFBxT_wlSXWPFnGTWDHvzBVQo02vQsh1oTaGluuwocki1VVdg4wMwU22hYPk5FO8zQf...
```

**响应示例**:
```json
{
  "message": "success",
  "data": {
    "is_activated": 1
  }
}
```

---

## 三、zijieapi.com 接口分析

### 3.1 域名概述

zijieapi.com 是字节跳动内部服务域名，主要用于监控上报、埋点采集、SDK配置、广告服务等。

| 子域名 | 请求数 | 主要功能 |
|--------|--------|----------|
| mcs.zijieapi.com | 79 | 增长埋点采集 |
| mon.zijieapi.com | 54 | 监控上报 |
| gecko5-hj.zijieapi.com | 31 | Gecko SDK |
| ads5-normal-lq.zijieapi.com | 16 | 广告服务 |
| mssdk3-normal-hl.zijieapi.com | 14 | MSSDK服务 |

### 3.2 核心接口列表

#### 埋点与监控

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /v1/list` | 54 | 200 | mcs.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /monitor_browser/collect/batch/` | 50 | 204 | mon.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /list` | 25 | 200 | mcs.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /ri/report` | 10 | 200 | mssdk3-normal-hl.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /monitor_pc/collect/api/pc_log` | 7 | 204 | pc-mon.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /api/ad/v1/report/` | 2 | 200 | ads5-normal-lq.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /monitor/collect/c/crash` | 1 | 200 | mon.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /monitor/collect/c/logcollect` | 1 | 200 | mon.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /monitor/collect/c/exception/dump_collection` | 1 | 200 | mon.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /monitor/collect/batch/` | 1 | 200 | mon.zijieapi.com |

#### SDK配置

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /gkx/api/settings/v3` | 12 | 200 | gecko5-hj.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /gkx/api/resource/v7/coldstart` | 11 | 200 | gecko5-hj.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /gkx/api/resource/v7/custom` | 7 | 200 | gecko5-hj.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /sdi/get_token` | 2 | 200 | mssdk3-normal-hl.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /luckycat/activity/settings/get_static_settings/` | 2 | 200 | polaris3-normal-lq.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /luckycat/activity/settings/get_dynamic_settings/` | 1 | 200 | polaris3-normal-lq.zijieapi.com |

#### 反馈服务

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /api/feedback/v1/newest_reply/` | 7 | 200 | feedback-c.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /api/ad/feedback/privacy/` | 1 | 200 | ads5-normal-lq.zijieapi.com |

#### 其他

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `CONNECT ` | 24 | 0 | ads5-normal-lq.zijieapi.com, gecko5-hj.zijieapi.com, ads3-normal-lq.zijieapi.com, tnc0-aliec2.zijieapi.com, tnc0-alisc1.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /get_domains/v5/` | 9 | 200 | tnc3-bjlgy.zijieapi.com, tnc0-alisc1.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /common` | 5 | 200 | abtest3-misc-lq.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /v2/bytesync/api/pipeline` | 4 | 200 | bsync3-normal-lq.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /pilvinen/v1/sseelfk/haku` | 2 | 200 | mssdk3-normal-hl.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /api/timon_config_proxy/config/get` | 2 | 200 | timon.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /api/plugin/config/v2/` | 2 | 200 | saveu5-normal-lq.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `GET /data/v1/life/dolphin/info` | 2 | 200 | life-service-open.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /src/server/v2/package` | 1 | 200 | gecko5-lq.zijieapi.com |

| 接口 | 调用次数 | 状态码 | 子域名 |
|------|----------|--------|--------|
| `POST /vc/setting` | 1 | 200 | vcs.zijieapi.com |

### 3.3 详细接口说明

#### POST /v1/list

**请求示例**:
```
URL: https://mcs.zijieapi.com/v1/list...
```

**请求体** (前200字符):
```
[{"server_time":1780679691,"caller":"","user":{"user_unique_id":"4197289135465178","device_id":"4197289135465178","user_is_login":true,"user_id":"4443578880233225"},"header":{"custom":"{\"icube_uid\":
```

#### POST /monitor_browser/collect/batch/

**请求示例**:
```
URL: https://mon.zijieapi.com/monitor_browser/collect/batch/?biz_id=trae_cn...
```

**请求体** (前200字符):
```
{"ev_type":"batch","list":[{"ev_type":"custom","payload":{"name":"icube_ai_command_execute","type":"event","metrics":{"cost_time":12},"categories":{"status":"Success","command":"icube.common.commands.
```

#### POST /list

**请求示例**:
```
URL: https://mcs.zijieapi.com/list?aid=711126&sdk_version=5.3.7&device_platform=electron...
```

**请求体** (前200字符):
```
[{"events":[{"event":"abtest_exposure","ab_sdk_version":"91978476","params":"{\"app_id\":711126,\"ab_url\":\"vscode-file://vscode-app/d:/Program%20Files/Trae%20CN/resources/app/out/vs/code/electron-br
```

#### CONNECT 

**请求示例**:
```
URL: https://ads5-normal-lq.zijieapi.com...
```


#### POST /gkx/api/settings/v3

**请求示例**:
```
URL: https://gecko5-hj.zijieapi.com/gkx/api/settings/v3?iid=2877876566592665&device_id=2877876567030921&ac=mobile&channel=vivo_1967_64&aid=1967&app_name=novelapp&version_code=70932&version_name=7.0.9.32&de...
```

**请求体** (前200字符):
```
{"local":{"version":"1149834619"},"auth":{"random":"1780679583","sign":"514f0a483631f2b0d4cf802cfc581a80"},"common":{"app_id":1967,"app_version":"7.0.9","device_id":"2877876567030921","device_model":"
```

#### POST /gkx/api/resource/v7/coldstart

**请求示例**:
```
URL: https://gecko5-hj.zijieapi.com/gkx/api/resource/v7/coldstart?iid=2877876566592665&device_id=2877876567030921&ac=mobile&channel=vivo_1967_64&aid=1967&app_name=novelapp&version_code=70932&version_name=7...
```

**请求体** (前200字符):
```
{"deployments":{},"local":{"5fb33cde3ebff01c8433ddc22aac0816":{"caijing_lynx_wallet_home_v4":{"hash_version":"0","prepare_meta_id":7647738824243675187},"ecommerce_aftersale_h5_pia":{"available_meta_id
```

#### POST /ri/report

**请求示例**:
```
URL: https://mssdk3-normal-hl.zijieapi.com/ri/report?lc_id=1394812046&platform=android&device_platform=android&sdk_ver=v04.09.09-ml-android&sdk_ver_code=67700992&app_ver=7.0.9.32&version_code=70932&aid=196...
```

**请求体** (前200字符):
```
CMSQgIIEEAIigEff4WpfERawgr0F2hh7pdUuIA+EGiItavYCPFk2y11pvXBP8VjIP+K+QiIx+BFgNzSJ7yHMfuRlknpIOONNtnUZ3gITn+OlB7hOylrmQfSSrpzUuexMpFdxb36szlW2mao9VnLDBIAtDJ5o5SNm2gJrr8OYJ6NQC1Azg4+GeMcLnSogmwCNO+nMH0By
```

#### GET /get_domains/v5/

**请求示例**:
```
URL: https://tnc0-alisc1.zijieapi.com/get_domains/v5/?aid=1967&device_id=2877876567030921&device_platform=android&version_code=70932&channel=vivo_1967_64&update_version_code=70932&user_id=883628161107575&a...
```


---

## 四、公共请求参数

所有接口均包含以下公共参数：

| 参数名 | 说明 | 示例值 |
|--------|------|--------|
| `aid` | 应用ID | 1967 |
| `app_name` | 应用名称 | novelapp |
| `version_code` | 版本代码 | 70932 |
| `version_name` | 版本名称 | 7.0.9.32 |
| `device_id` | 设备ID | 2877876567030921 |
| `iid` | 安装ID | 2877876566592665 |
| `device_type` | 设备类型 | sdk_gphone64_x86_64 |
| `device_brand` | 设备品牌 | google |
| `os` | 操作系统 | android |
| `os_version` | 系统版本 | 16 |
| `os_api` | API级别 | 36 |
| `channel` | 渠道 | vivo_1967_64 |
| `language` | 语言 | zh |
| `region` | 地区 | cn |
| `ac` | 网络 | mobile |

---

## 五、请求签名机制

### 5.1 签名相关Header

| Header | 说明 |
|--------|------|
| `x-argus` | Argus签名 |
| `x-gorgon` | Gorgon签名 |
| `x-helios` | Helios签名 |
| `x-medusa` | Medusa签名 |
| `x-khronos` | 时间戳 |
| `x-ladon` | Ladon签名 |
| `x-ss-stub` | 请求体摘要 |

### 5.2 签名流程

1. 生成请求参数字典
2. 按特定规则排序参数
3. 计算参数签名
4. 生成时间戳
5. 组合生成最终签名Header

### 5.3 libmetasec_ml.so 签名参数分析

基于对 `libmetasec_ml.so` (字节跳动安全库) 的逆向分析，发现以下签名相关参数和机制。

#### 5.3.1 库基本信息

| 属性 | 值 |
|------|------|
| 库名 | libmetasec_ml.so |
| 架构 | arm64-v8a |
| 主要功能 | 安全签名生成、设备信息收集、风险检测 |

#### 5.3.2 发现的Header参数

| Header | 地址 | 说明 |
|--------|------|------|
| `X-METASEC-MODE` | 0x383bbe | 安全模式标识 |
| `X-BD-CLIENT-KEY` | 0x383bce/0x387c50 | 客户端密钥 |
| `X-BD-KMSV` | 0x11f98b | KMS版本标识 |
| `SignIT` | 0x383bb6 | 签名标识 |

#### 5.3.3 请求参数格式

从库中提取的参数格式字符串：

**完整参数格式**:
```
platform=%s&device_platform=%s&sdk_ver=%s&sdk_ver_code=%u&app_ver=%s&version_code=%s&aid=%s&sdkid=%s&subaid=%s&iid=%s&did=%s&bd_did=%s&client_type=%s&region_type=%s&mode=%d&full_update_version_code=%s
```

**参数说明表**:

| 参数名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| `platform` | string | 平台标识 | android |
| `device_platform` | string | 设备平台 | android |
| `sdk_ver` | string | SDK版本 | 5.0.0 |
| `sdk_ver_code` | uint | SDK版本代码 | 50000 |
| `app_ver` | string | 应用版本 | 7.0.9.32 |
| `version_code` | string | 版本代码 | 70932 |
| `aid` | string | 应用ID | 1967 |
| `sdkid` | string | SDK ID | - |
| `subaid` | string | 子应用ID | - |
| `iid` | string | 安装ID | 2877876566592665 |
| `did` | string | 设备ID | 2877876567030921 |
| `bd_did` | string | 字节跳动设备ID | - |
| `client_type` | string | 客户端类型 | android |
| `region_type` | string | 区域类型 | cn |
| `mode` | int | 模式标识 | 1 |
| `full_update_version_code` | string | 完整更新版本代码 | 70932 |
| `form` | string | 表单类型 | - |

#### 5.3.4 内部接口路径

| 接口路径 | 说明 |
|----------|------|
| `config/v1/info` | 配置信息接口 |
| `config/v1/info_tob` | 配置信息接口(头条版) |
| `https://%s` | HTTPS URL模板 |
| `http://` | HTTP URL前缀 |

#### 5.3.5 数据存储标识

| 标识名 | 地址 | 说明 |
|--------|------|------|
| `app_db` | 0x6ecf1 | 应用数据库 |
| `sdk_riskapp_db` | 0x6ece9 | SDK风险应用数据库 |
| `ssdk_riskapp_db` | 0x6ece8 | SSDK风险应用数据库 |
| `sdk_risk_report` | 0x6f08f | SDK风险报告 |
| `riskapp_db` | 0x6eced | 风险应用数据库 |
| `risk_report` | 0x6f093 | 风险报告 |

#### 5.3.6 报告相关标识

| 标识名 | 地址 | 说明 |
|--------|------|------|
| `report` | 0x6f098/0x6f46f | 报告 |
| `report_tob` | 0x6f97b | 报告(头条版) |
| `report_ext` | 0x6fe69 | 扩展报告 |
| `report_list` | 0x70143 | 报告列表 |
| `setting` | 0x7021a | 设置 |
| `setting_tob` | 0x6fbee | 设置(头条版) |

#### 5.3.7 核心函数分析

库中发现的主要大函数(可能为签名生成核心):

| 函数地址 | 大小 | 指令数 | 说明 |
|----------|------|--------|------|
| sub_16A8E4 | 0x7b7c (31644字节) | - | 最大函数，可能为签名主逻辑 |
| sub_20CA88 | 0x6698 (26264字节) | - | 大型函数，可能为加密逻辑 |
| sub_2DCC38 | 0x5e58 (24216字节) | - | 大型函数 |
| sub_1734F8 | 0x57c8 (22472字节) | - | 大型函数 |
| sub_29CF58 | 0x330 (816字节) | 343 | X-BD-KMSV引用函数(已混淆) |

**函数 sub_29CF58 分析**:
- 该函数引用了 `X-BD-KMSV` 字符串
- 函数被控制流平坦化混淆(flattened)
- 包含26个调用目标
- 有35个基本块，107个常量

#### 5.3.8 JNI接口

| 接口 | 地址 | 说明 |
|------|------|------|
| JNI_OnLoad | 0x18e9 | JNI加载入口 |
| java/lang/String | 0x6e9a2/0x6e9b5 | Java字符串类型引用 |

#### 5.3.9 加密相关字符串

| 字符串 | 地址 | 说明 |
|--------|------|------|
| `aesbM*` | 0x387e20 | AES相关 |
| `key_create` | 0x1b76 | 密钥创建 |
| `key_delete` | 0x1c03 | 密钥删除 |
| `MD5` | 0x11f99d | MD5算法标识 |
| `token` | 0x70012 | 令牌 |
| `token_tob` | 0x6f2e8 | 令牌(头条版) |

#### 5.3.10 签名生成流程推测

基于分析，签名生成流程如下：

1. **初始化阶段**
   - JNI_OnLoad加载库
   - 初始化密钥(key_create)
   - 获取设备信息

2. **参数收集阶段**
   - 收集设备平台、SDK版本、应用版本等信息
   - 获取设备ID(did)、安装ID(iid)
   - 获取区域类型、客户端类型

3. **签名计算阶段**
   - 参数按特定格式拼接
   - 使用AES/MD5等算法计算
   - 生成X-BD-CLIENT-KEY、X-BD-KMSV等Header

4. **上报阶段**
   - 通过config/v1/info接口上报配置
   - 通过report接口上报风险信息

#### 5.3.12 参数生成过程还原

基于对核心函数 `sub_1B4AB4` 的逆向分析，参数生成过程如下：

##### 5.3.12.1 核心函数入口

```c
__int64 __usercall sub_1B4AB4@<X0>(unsigned int n7@<W0>, __int64 a2@<X2>, __int64 *a3@<X8>)
```

| 参数 | 说明 |
|------|------|
| n7 | 接口类型标识(0-7) |
| a2 | 上下文/配置数据 |
| a3 | 输出参数，用于存储生成的URL |

##### 5.3.12.2 API路径选择 (sub_1B4E54)

根据 `n7` 参数选择不同的API接口：

| n7值 | 非TOB接口 | TOB接口 |
|------|-----------|---------|
| 0 | `/ri/report` | `/ri/report_tob` |
| 1 | `/sdi/get_token` | `/sdi/get_token_tob` |
| 2 | `/dc/rnd` | `/dc/rnd_tob` |
| 3 | `/mscc/common_setting` | `/mscc/common_setting_tob` |
| 4 | `/common_config/v1/info` | `/common_config/v1/info_tob` |
| 5 | `/ri/neo` | `/ri/neo_tob` |
| 6 | `/ri/report_ext` | (无) |
| 7 | `/ri/report_list` | (无) |

**TOB判断逻辑**: 检查传入参数的第一个DWORD值，非零表示TOB版本。

##### 5.3.12.3 参数来源追踪

完整参数格式：
```
%s?lc_id=%s&platform=%s&device_platform=%s&sdk_ver=%s&sdk_ver_code=%u&app_ver=%s&version_code=%s&aid=%s&sdkid=%s&subaid=%s&iid=%s&did=%s&bd_did=%s&client_type=%s&region_type=%s&mode=%d&full_update_version_code=%s
```

**参数来源表**:

| 参数位置 | 参数名 | 获取函数/来源 | 地址偏移 |
|----------|--------|--------------|----------|
| 1 | API路径 | sub_1B4E54 | - |
| 2 | lc_id | v46[0]+16 → sub_2633A4 | 0x1b4ca4 |
| 3 | platform | sub_172DBC | 0x1b4cb0 |
| 4 | device_platform | v48[0]+16 → sub_248344 | 0x1b4cb4 |
| 5 | sdk_ver | sub_17E38C | 0x1b4cbc |
| 6 | sdk_ver_code | sub_172AF4 (int) | 0x1b4cc8 |
| 7 | app_ver | v42[0]+16 → sub_1CF95C | 0x1b4cdc |
| 8 | version_code | v41[0]+16 → sub_15F580 | 0x1b4ce0 |
| 9 | aid | v40[0]+16 → sub_15F580 | 0x1b4d08 |
| 10 | sdkid | v44 → sub_17E61C + sub_291510 | 0x1b4d04 |
| 11 | subaid | v43 → sub_15F580 + sub_291510 | 0x1b4d14 |
| 12 | iid | v39[0]+16 → sub_17E5A0 | 0x1b4d2c |
| 13 | did | v38[0]+16 → sub_161168 | 0x1b4da8 |
| 14 | bd_did | v45 → sub_17F758 + sub_2633A4 | 0x1b4d58 |
| 15 | client_type | sub_17E698 | 0x1b4b74 |
| 16 | region_type | sub_256330 | 0x1b4b84 |
| 17 | mode | 根据n7计算 | 0x1b4bd0 |
| 18 | full_update_version_code | v47[0]+16 → loc_1E0260 | 0x1b4da8 |

##### 5.3.12.4 参数提取核心逻辑

**数据结构访问模式**:
```c
// Rust String/ArcStr 模式
if ( (v44[0] & 1) != 0 )
    v23 = (const char *)v44[2];  // Heap allocated
else
    v23 = (char *)v44 + 1;       // Inline (Small String Optimization)
```

**mode参数计算逻辑**:
```c
n2 = 2;
if ( n7 == 7 ) {
    if ( v35[0] )
        n2 = 4;
    else
        n2 = 2;
}
else if ( !n7 ) {
    if ( v35[0] && !*v11 )
        n2 = 3;
    else
        n2 = 2;
}
```

| n7值 | mode值 | 条件 |
|------|--------|------|
| 0 | 3 | v35[0]存在且*v11为0 |
| 0 | 2 | 其他 |
| 7 | 4 | v35[0]存在 |
| 7 | 2 | 其他 |
| 其他 | 2 | 默认 |

##### 5.3.12.5 参数格式化输出

最终通过 `sub_248908` (vsnprintf包装) 格式化输出：

```c
sub_248908(
    v31,  // 输出缓冲区
    "%s?lc_id=%s&platform=%s&device_platform=%s&sdk_ver=%s&sdk_ver_code=%u&app_ver=%s&version_code=%s&aid=%s&sdkid=%s&subaid=%s&iid=%s&did=%s&bd_did=%s&client_type=%s&region_type=%s&mode=%d&full_update_version_code=%s",
    v30,   // API路径
    v29,   // lc_id
    v17,   // platform
    v18,   // device_platform
    v19,   // sdk_ver
    v20,   // sdk_ver_code (uint)
    v21,   // app_ver
    v22,   // version_code
    v24,   // aid
    v23,   // sdkid
    v25,   // subaid
    v26,   // iid
    *(v38[0]+16),  // did
    v27,   // bd_did
    v34,   // client_type
    v33,   // region_type
    n2_1,  // mode (int)
    *(v47[0]+16)   // full_update_version_code
);
```

##### 5.3.12.6 字符串解密机制

多个参数获取函数使用相同的解密模式：

```c
char *sub_172DBC() {
    // 计算解密偏移
    v0 = (~(unsigned __int64)sub_172DBC & 0x10400040400LL | 0xA060400A021040LL)
       + ((unsigned __int64)sub_172DBC & 0x10400040400LL);
    
    // 原子检查初始化
    if ( !atomic_load((unsigned int *)((char *)off_379948 + offset)) ) {
        // 解密: sub_167E54(dest, encrypted, key)
        sub_167E54((char *)off_379950 + offset, 
                   (char *)off_379940 + offset, 
                   (char *)off_379958 + offset);
        atomic_store(1u, (unsigned int *)((char *)off_379948 + offset));
    }
    return &v1[offset];
}
```

**解密特征**:
- 使用 `sub_167E54` 作为解密函数
- 采用原子初始化确保线程安全
- 偏移计算使用函数地址进行异或/或运算

##### 5.3.12.7 完整执行流程图

```
┌─────────────────────────────────────────────────────────────────────┐
│                      sub_1B4AB4 参数生成流程                        │
├─────────────────────────────────────────────────────────────────────┤
│  输入参数: n7 (接口类型), a2 (配置), a3 (输出)                      │
│                                                                    │
│  ┌──────────────┐                                                 │
│  │ sub_1B4E54   │ → 根据n7选择API路径 (/ri/report等)               │
│  └──────┬───────┘                                                 │
│         ↓                                                          │
│  ┌──────────────┐                                                 │
│  │ 数据结构初始化│ → v35, v36, v37等临时结构                       │
│  └──────┬───────┘                                                 │
│         ↓                                                          │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    参数收集阶段                              │  │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │  │
│  │  │platform │ │sdk_ver  │ │app_ver  │ │  aid    │           │  │
│  │  │172DBC   │ │17E38C   │ │1CF95C   │ │15F580   │           │  │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │  │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │  │
│  │  │iid      │ │did      │ │bd_did   │ │mode     │           │  │
│  │  │17E5A0   │ │161168   │ │17F758   │ │计算得出 │           │  │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │  │
│  └──────────────────────────────────────────────────────────────┘  │
│         ↓                                                          │
│  ┌──────────────┐                                                 │
│  │ sub_248908   │ → vsnprintf格式化拼接                           │
│  │  (vsnprintf) │                                                 │
│  └──────┬───────┘                                                 │
│         ↓                                                          │
│  ┌──────────────┐                                                 │
│  │   输出URL    │ → 写入 a3 指向的缓冲区                           │
│  └──────────────┘                                                 │
│                                                                    │
│  输出格式: /api/path?lc_id=xxx&platform=xxx&...&mode=N            │
└─────────────────────────────────────────────────────────────────────┘
```

##### 5.3.12.8 内存管理

函数结尾处进行资源清理：

```c
sub_15E1A8(v38);  // 释放临时字符串
sub_15E1A8(v39);
sub_15E1A8(v40);
sub_15E1A8(v41);
sub_15E1A8(v42);
sub_32AA70(v43);  // 释放Arc/Mutex
sub_32AA70(v44);
sub_32AA70(v45);
sub_15E1A8(v46);
sub_15E1A8(v47);
sub_15E1A8(v48);
sub_15E268(v35);
return sub_32AA70(v37);
```

**资源类型**:
- `sub_15E1A8`: 释放Rust String
- `sub_32AA70`: 释放Arc/Mutex智能指针

#### 5.3.11 混淆技术分析

库中使用了以下混淆技术：

| 技术 | 说明 |
|------|------|
| 控制流平坦化 | 函数sub_29CF58等被flatten |
| 字符串加密 | 签名相关字符串(gorgon/argus等)未直接存储 |
| 符号剥离 | 所有函数名为sub_XXXXXX形式 |
| 常量混淆 | 大量常量值分散在代码中 |

---

## 六、总结

| 域名 | 主要功能 | 接口数量 | 请求数量 |
|------|----------|----------|----------|
| fqnovel.com | 核心业务API | 182 | 706 |
| snssdk.com | 公共服务 | 13 | 38 |
| zijieapi.com | 内部服务 | 36 | 263 |

**文档生成时间**: 2026-06-06
