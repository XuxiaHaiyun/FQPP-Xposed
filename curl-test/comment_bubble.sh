# 评论气泡接口 - 获取章节各段落评论气泡数量
# 参数: item_id(必填), book_id(推荐传入,用于自动获取itemVersion), item_version(可选)
curl "http://localhost:9999/novel/commentapi/idea/list?item_id=6884187689541698056&book_id=6883748331202284558" > comment_bubble.json
cat comment_bubble.json
