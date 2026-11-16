# 子评论（回复）列表接口
# 参数: comment_id(必填), group_id(可选,章节ID/书籍ID), book_id(可选), comment_type(可选,默认0)
# comment_type: 0=章评, 1=段评, 2=书评
# count: 请求数量(可选,默认20), cursor: 分页游标(可选)

# 章评子评论（comment_id 从 comment_list_chapter.json 的 data.comments[0].id 获取）
curl "http://localhost:9999/novel/commentapi/reply/list?comment_id=7347022147882893323&group_id=6883755304903770632&book_id=6883748331202284558&comment_type=0&count=20" > reply_list_chapter.json
echo "=== 章评子评论 ==="
cat reply_list_chapter.json

# 段评子评论
curl "http://localhost:9999/novel/commentapi/reply/list?comment_id=7347022147882893323&group_id=6883755304903770632&book_id=6883748331202284558&comment_type=1&count=20" > reply_list_para.json
echo "=== 段评子评论 ==="
cat reply_list_para.json

# 书评子评论（group_id 传 bookId）
curl "http://localhost:9999/novel/commentapi/reply/list?comment_id=7347022147882893323&group_id=6883748331202284558&comment_type=2&count=20" > reply_list_book.json
echo "=== 书评子评论 ==="
cat reply_list_book.json
