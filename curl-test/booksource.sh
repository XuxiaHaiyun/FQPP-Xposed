# 书源导入接口 - 获取阅读APP书源配置JSON
# 返回包含当前服务端口的书源配置，可直接导入阅读APP
curl "http://localhost:9999/booksource" > booksource.json
cat booksource.json
