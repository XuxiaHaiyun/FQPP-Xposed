# 音频解密接口 - 获取解密后的音频文件（返回二进制mp3数据）
# 参数: book_id(必填), item_id(必填), tone_quality(可选,默认128), tone_id(可选,默认0)
# tone_quality: 32=低音质, 64=中音质, 128=高音质
# tone_id: 0=默认, 80=多角色对话升级版
curl "http://localhost:9999/reading/bookapi/audio/decrypt?book_id=7281195245084691518&item_id=7295592952154328116" -o audio_decrypt.mp3
echo "Saved to audio_decrypt.mp3"
