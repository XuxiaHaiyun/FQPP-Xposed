#!/bin/bash
# FQPlusPlus 一键全部接口测试脚本
# 用法: bash test_all.sh [HOST]
# 默认HOST: localhost:9999

HOST="${1:-localhost:9999}"
PASS=0
FAIL=0
SKIP=0

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

print_header() {
    echo ""
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}========================================${NC}"
}

test_api() {
    local name="$1"
    local url="$2"
    local output_file="$3"
    local check="$4"  # 可选: 检查响应中的关键字

    echo -e "${YELLOW}[TEST]${NC} $name"
    echo -e "  URL: http://$HOST$url"

    local http_code
    http_code=$(curl -s -o "$output_file" -w "%{http_code}" "http://$HOST$url" 2>/dev/null)

    if [ "$http_code" = "200" ]; then
        if [ -n "$check" ]; then
            if grep -q "$check" "$output_file" 2>/dev/null; then
                echo -e "  ${GREEN}[PASS]${NC} HTTP $http_code, 关键字匹配: $check"
                PASS=$((PASS + 1))
            else
                echo -e "  ${RED}[FAIL]${NC} HTTP $http_code, 关键字未匹配: $check"
                FAIL=$((FAIL + 1))
            fi
        else
            echo -e "  ${GREEN}[PASS]${NC} HTTP $http_code"
            PASS=$((PASS + 1))
        fi
    elif [ "$http_code" = "000" ]; then
        echo -e "  ${RED}[FAIL]${NC} 连接失败，请确认服务已启动 (http://$HOST)"
        FAIL=$((FAIL + 1))
    else
        echo -e "  ${RED}[FAIL]${NC} HTTP $http_code"
        FAIL=$((FAIL + 1))
    fi
    echo ""
}

echo -e "${CYAN}=====================================${NC}"
echo -e "${CYAN}  FQPlusPlus API 全部接口测试${NC}"
echo -e "${CYAN}  目标: http://$HOST${NC}"
echo -e "${CYAN}=====================================${NC}"

# 1. 健康检查
print_header "1. 健康检查"
test_api "status" "/status" "result_status.txt" "ok"

# 2. 搜索接口
print_header "2. 搜索接口"
test_api "search" "/reading/bookapi/search/tab?query=十日终焉&page=1" "result_search.json" "bookName"

# 3. 书籍详情
print_header "3. 书籍详情"
test_api "detail" "/reading/bookapi/detail?book_id=7338034838672247833" "result_detail.json" "bookName"
test_api "multi-detail" "/reading/bookapi/multi-detail?book_id=7338034838672247833" "result_multi_detail.json" "bookName"

# 4. 目录接口
print_header "4. 目录接口"
test_api "catalog" "/reading/bookapi/directory/all_items?book_id=7281195245084691518" "result_catalog.json" "itemId"

# 5. 正文内容
print_header "5. 正文内容"
test_api "content" "/reading/reader/full?book_id=7281195245084691518&item_id=7520270157499859518" "result_content.json" "content"
test_api "batch_content" "/reading/reader/batch_full?book_id=7281195245084691518&item_ids=7520270157499859518" "result_batch_content.json" "content"

# 6. 音频接口
print_header "6. 音频接口"
test_api "audio_playurl" "/reading/bookapi/audio/playurl?book_id=7281195245084691518&item_id=7295592952154328116" "result_audio_playurl.json" "url"
echo -e "${YELLOW}[TEST]${NC} audio_decrypt (二进制mp3, 单独测试)"
echo -e "  URL: http://$HOST/reading/bookapi/audio/decrypt?book_id=7281195245084691518&item_id=7295592952154328116"
http_code=$(curl -s -o "result_audio_decrypt.mp3" -w "%{http_code}" "http://$HOST/reading/bookapi/audio/decrypt/?book_id=7281195245084691518&item_id=7295592952154328116" 2>/dev/null)
if [ "$http_code" = "200" ]; then
    file_size=$(wc -c < "result_audio_decrypt.mp3" 2>/dev/null || echo "0")
    echo -e "  ${GREEN}[PASS]${NC} HTTP $http_code, 文件大小: ${file_size} bytes"
    PASS=$((PASS + 1))
else
    echo -e "  ${RED}[FAIL]${NC} HTTP $http_code"
    FAIL=$((FAIL + 1))
fi
echo ""

# 7. 书架接口
print_header "7. 书架接口"
test_api "bookshelf_info" "/reading/bookapi/bookshelf/info" "result_bookshelf_info.json"
test_api "bookshelf_add" "/reading/bookapi/bookshelf/add?book_id=7281195245084691518&type=read" "result_bookshelf_add.json"

# 8. 书城与分类
print_header "8. 书城与分类"
test_api "bookmall" "/reading/bookapi/bookmall/cell/change" "result_bookmall.json"
test_api "new_category_landing" "/reading/bookapi/new_category/landing?parent_enterfrom=novel_channel_category.tab.&aid=1967&offset=0&limit=100&category_id=1&gender=1" "result_new_category_landing.json"

# 9. 评论接口
print_header "9. 评论接口"
test_api "comment_list(段评)" "/novel/commentapi/comment/list?item_id=6883755304903770632&book_id=6883748331202284558&comment_type=1&para_index=30" "result_comment_list_para.json"
test_api "comment_list(章评)" "/novel/commentapi/comment/list?item_id=6883755304903770632&book_id=6883748331202284558&comment_type=0" "result_comment_list_chapter.json"
test_api "comment_list(书评)" "/novel/commentapi/comment/list?item_id=6883748331202284558&comment_type=2" "result_comment_list_book.json"
test_api "comment_bubble" "/novel/commentapi/idea/list?item_id=6884187689541698056&book_id=6883748331202284558" "result_comment_bubble.json"

# 10. 书源配置
print_header "10. 书源配置"
test_api "booksource" "/booksource" "result_booksource.json" "bookSourceName"

# 11. 404测试
print_header "11. 异常测试"
echo -e "${YELLOW}[TEST]${NC} 404 (期望返回404)"
echo -e "  URL: http://$HOST/"
http_code=$(curl -s -o "result_404.html" -w "%{http_code}" "http://$HOST/" 2>/dev/null)
if [ "$http_code" = "404" ]; then
    echo -e "  ${GREEN}[PASS]${NC} HTTP $http_code (符合预期)"
    PASS=$((PASS + 1))
else
    echo -e "  ${RED}[FAIL]${NC} HTTP $http_code (期望404)"
    FAIL=$((FAIL + 1))
fi
echo ""

# 测试结果汇总
echo -e "${CYAN}=====================================${NC}"
echo -e "${CYAN}  测试结果汇总${NC}"
echo -e "${CYAN}=====================================${NC}"
TOTAL=$((PASS + FAIL))
echo -e "  ${GREEN}通过: $PASS${NC}"
echo -e "  ${RED}失败: $FAIL${NC}"
echo -e "  总计: $TOTAL"
echo ""

if [ "$FAIL" -eq 0 ]; then
    echo -e "${GREEN}全部测试通过!${NC}"
else
    echo -e "${RED}存在失败测试，请检查上方输出${NC}"
fi

# 清理临时文件（取消注释以启用）
# rm -f result_*.json result_*.txt result_*.mp3 result_*.html
