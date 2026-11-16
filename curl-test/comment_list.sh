# 评论列表接口 - 支持段评/章评/书评
# 参数: item_id(必填), book_id(段评/章评必填), comment_type(可选,默认1)
# comment_type: 0=章评, 1=段评, 2=书评(item_id传bookId)
# para_index: 段落索引(段评时可选,默认0)
# sort: 1=SmartHot, 2=TimeAsc, 3=TimeDesc

# 段评列表
curl "http://localhost:9999/novel/commentapi/comment/list?item_id=6883755304903770632&book_id=6883748331202284558&comment_type=1&para_index=30" > comment_list_para.json
echo "=== 段评列表 ==="
cat comment_list_para.json

# 章评列表
curl "http://localhost:9999/novel/commentapi/comment/list?item_id=6883755304903770632&book_id=6883748331202284558&comment_type=0" > comment_list_chapter.json
echo "=== 章评列表 ==="
cat comment_list_chapter.json

# 书评列表（item_id传bookId）
curl "http://localhost:9999/novel/commentapi/comment/list?item_id=6883748331202284558&comment_type=2" > comment_list_book.json
echo "=== 书评列表 ==="
cat comment_list_book.json
