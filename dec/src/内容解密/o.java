package com.dragon.read.reader.utils;

import android.os.SystemClock;
import com.bytedance.covode.number.Covode;
import com.dragon.read.base.util.LogWrapper;
import com.dragon.read.reader.DecryptKey;
import com.dragon.read.reader.download.ChapterInfo;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

/* JADX INFO: loaded from: classes9.dex */
public final class o {
    static {
        Covode.recordClassIndex(604104);
    }

    public static final String a(ChapterInfo chapterInfo, DecryptKey key) {
        boolean z17;
        Intrinsics.checkNotNullParameter(chapterInfo, "<this>");
        Intrinsics.checkNotNullParameter(key, "key");
        String str = chapterInfo.content;
        if (chapterInfo.compressStatus > 0) {
            z17 = true;
        } else {
            z17 = false;
        }
        return b(str, key, z17, chapterInfo.bookId, chapterInfo.chapterId);
    }

    public static final String b(String str, DecryptKey key, boolean z17, String str2, String str3) throws Throwable {
        boolean z18;
        Intrinsics.checkNotNullParameter(key, "key");
        if (str != null && str.length() != 0) {
            z18 = false;
        } else {
            z18 = true;
        }
        if (z18) {
            return str;
        }
        byte[] bArrL = w85.i.l(str, key);
        if (z17) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            byte[] bArrA = com.dragon.read.util.r1.a(bArrL);
            Intrinsics.checkNotNullExpressionValue(bArrA, "decompress(...)");
            String str4 = new String(bArrA, Charsets.UTF_8);
            StringBuilder sb6 = new StringBuilder();
            sb6.append("[ReaderSDKBiz] 解压章节内容耗时:");
            sb6.append(SystemClock.elapsedRealtime() - jElapsedRealtime);
            sb6.append(", bookId:");
            if (str2 == null) {
                str2 = "";
            }
            sb6.append(str2);
            sb6.append(", chapterId:");
            if (str3 == null) {
                str3 = "";
            }
            sb6.append(str3);
            LogWrapper.i("experience", sb6.toString(), new Object[0]);
            return str4;
        }
        return new String(bArrL, Charsets.UTF_8);
    }
}