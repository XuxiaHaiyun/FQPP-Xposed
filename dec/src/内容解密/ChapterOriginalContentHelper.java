package com.dragon.read.reader.utils;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.bytedance.article.common.monitor.stack.ExceptionMonitor;
import com.bytedance.covode.number.Covode;
import com.bytedance.framwork.core.monitor.MonitorUtils;
import com.dragon.read.base.util.JSONUtils;
import com.dragon.read.base.util.LogHelper;
import com.dragon.read.base.util.LogWrapper;
import com.dragon.read.local.CacheWrapper;
import com.dragon.read.network.ErrorCodeException;
import com.dragon.read.reader.DecryptKey;
import com.dragon.read.reader.download.ChapterInfo;
import com.dragon.read.reader.ui.ReaderActivity;
import com.dragon.read.util.NetworkUtils;
import com.dragon.read.util.NumberUtils;
import com.dragon.reader.lib.ReaderClient;
import com.dragon.reader.lib.datalevel.model.ChapterItem;
import com.dragon.reader.lib.monitor.CommonConst$STATUS;
import com.ss.android.ugc.bytex.taskmonitor.proxy.SingleDelegate;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt___CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt__StringsJVMKt;
import me.ele.lancet.base.annotations.Proxy;
import me.ele.lancet.base.annotations.TargetClass;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import readersaas.com.dragon.read.saas.rpc.model.FullReqType;
import readersaas.com.dragon.read.saas.rpc.model.FullRequest;
import readersaas.com.dragon.read.saas.rpc.model.FullResponse;
import readersaas.com.dragon.read.saas.rpc.model.ItemContent;
import readersaas.com.dragon.read.saas.rpc.model.ReaderApiERR;

/* JADX INFO: loaded from: classes9.dex */
public final class ChapterOriginalContentHelper {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final a f188238c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final int f188239d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final LogHelper f188240e;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final HashSet<String> f188241a = new HashSet<>();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private int f188242b;

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    public static final class ReportType {
        private static final /* synthetic */ EnumEntries $ENTRIES;
        private static final /* synthetic */ ReportType[] $VALUES;
        public static final ReportType ALL;
        public static final ReportType LOCAL;
        public static final ReportType NETWORK;
        private final String contentLengthKey;
        private final String durationKey;

        private static final /* synthetic */ ReportType[] $values() {
            return new ReportType[]{LOCAL, NETWORK, ALL};
        }

        public static EnumEntries<ReportType> getEntries() {
            return $ENTRIES;
        }

        public final String getContentLengthKey() {
            return this.contentLengthKey;
        }

        public final String getDurationKey() {
            return this.durationKey;
        }

        public static ReportType[] values() {
            return (ReportType[]) $VALUES.clone();
        }

        static {
            Covode.recordClassIndex(604112);
            LOCAL = new ReportType("LOCAL", 0, "reader_chapter_info_local_time", "reader_chapter_info_local_length");
            NETWORK = new ReportType("NETWORK", 1, "reader_chapter_info_network_time", "reader_chapter_info_network_length");
            ALL = new ReportType("ALL", 2, "reader_chapter_info_load_time", "reader_chapter_info_load_length");
            ReportType[] reportTypeArr$values = $values();
            $VALUES = reportTypeArr$values;
            $ENTRIES = EnumEntriesKt.enumEntries(reportTypeArr$values);
        }

        public static ReportType valueOf(String str) {
            return (ReportType) Enum.valueOf(ReportType.class, str);
        }

        private ReportType(String str, int i17, String str2, String str3) {
            this.durationKey = str2;
            this.contentLengthKey = str3;
        }
    }

    static {
        Covode.recordClassIndex(604110);
        f188238c = new a(null);
        f188239d = 8;
        f188240e = new LogHelper(g2.d("Content"));
    }

    private static final void J0(Object obj) {
        com.dragon.read.reader.depend.a.f185562b.y("reader_full_trace", obj);
    }

    private final void m1(ChapterInfo chapterInfo) {
        this.f188242b = 20004;
        f188238c.f(chapterInfo);
    }

    public final boolean g1(String chapterId) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        return this.f188241a.contains(chapterId);
    }

    private final boolean Y(int i17) {
        if (i17 != ReaderApiERR.ARTICLE_NOT_FOUND_ERROR.getValue() && i17 != ReaderApiERR.NOVEL_DATA_GET_ERROR.getValue() && i17 != ReaderApiERR.BOOK_NOT_EXIST_ERROR.getValue()) {
            return false;
        }
        return true;
    }

    private final void i1(int i17) {
        try {
            MonitorUtils.monitorEvent("reader_chapter_decrypt_status", new JSONObject().putOpt("status", String.valueOf(i17)), null, null);
        } catch (Exception e17) {
            ExceptionMonitor.ensureNotReachHere(e17);
        }
    }

    public final Single<ChapterInfo> c0(final ChapterInfo chapterInfo) {
        Intrinsics.checkNotNullParameter(chapterInfo, "chapterInfo");
        this.f188242b = 20003;
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        Single<ChapterInfo> singleDefer = SingleDelegate.defer(new Callable() { // from class: com.dragon.read.reader.utils.k1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return ChapterOriginalContentHelper.d0(chapterInfo, jElapsedRealtime, this);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleDefer, "defer(...)");
        return singleDefer;
    }

    private final Single<ChapterInfo> P0(com.dragon.read.reader.model.d dVar) {
        final String strA = dVar.a();
        Intrinsics.checkNotNull(strA);
        final String strB = dVar.b();
        Intrinsics.checkNotNull(strB);
        final int iA = rm4.r.f307554a.a(strA);
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        Single singleX0 = X0(this, strA, strB, iA, dVar.d(), null, 16, null);
        final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.s
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.R0(jElapsedRealtime, strA, strB, iA, (ChapterInfo) obj);
            }
        };
        Single singleDoOnSuccess = singleX0.doOnSuccess(new Consumer() { // from class: com.dragon.read.reader.utils.t
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                ChapterOriginalContentHelper.S0(function1, obj);
            }
        });
        final Function1 function12 = new Function1() { // from class: com.dragon.read.reader.utils.u
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.T0(jElapsedRealtime, strA, strB, iA, (Throwable) obj);
            }
        };
        Single singleDoOnError = singleDoOnSuccess.doOnError(new Consumer() { // from class: com.dragon.read.reader.utils.v
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                ChapterOriginalContentHelper.U0(function12, obj);
            }
        });
        final Function1 function13 = new Function1() { // from class: com.dragon.read.reader.utils.w
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.V0(this.f188436a, (ChapterInfo) obj);
            }
        };
        Single<ChapterInfo> singleFlatMap = singleDoOnError.flatMap(new Function() { // from class: com.dragon.read.reader.utils.x
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return ChapterOriginalContentHelper.Q0(function13, obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleFlatMap, "flatMap(...)");
        return singleFlatMap;
    }

    public final String b0(ChapterInfo chapterInfo) {
        Intrinsics.checkNotNullParameter(chapterInfo, "chapterInfo");
        Elements elementsY0 = h56.a.b(chapterInfo.content).y0("p");
        StringBuilder sb6 = new StringBuilder();
        Iterator<Element> it6 = elementsY0.iterator();
        Intrinsics.checkNotNullExpressionValue(it6, "iterator(...)");
        while (it6.hasNext()) {
            Element next = it6.next();
            if (next.A0()) {
                String strV0 = next.V0();
                Intrinsics.checkNotNullExpressionValue(strV0, "text(...)");
                String strReplace$default = StringsKt__StringsJVMKt.replace$default(StringsKt__StringsJVMKt.replace$default(strV0, "\\n", "", false, 4, (Object) null), "\u3000", "", false, 4, (Object) null);
                if (!TextUtils.isEmpty(next.V0())) {
                    sb6.append("\u3000\u3000");
                    sb6.append(strReplace$default);
                    sb6.append("\n");
                }
            }
        }
        String string = sb6.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    public static final class a {
        static {
            Covode.recordClassIndex(604111);
        }

        private a() {
        }

        public /* synthetic */ a(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final void f(ChapterInfo chapterInfo) {
            String json;
            Intrinsics.checkNotNullParameter(chapterInfo, "chapterInfo");
            n1.b().d(chapterInfo.bookId, chapterInfo.chapterId, false);
            if (chapterInfo.keyVersion == Integer.MIN_VALUE) {
                LogWrapper.info("experience", ChapterOriginalContentHelper.f188240e.getTag(), "明文章节不缓存到本地，bookId = %s ,chapterId = %s", new Object[]{chapterInfo.bookId, chapterInfo.chapterId});
                return;
            }
            Integer numValueOf = null;
            if (TextUtils.isEmpty(chapterInfo.content)) {
                json = null;
            } else {
                json = JSONUtils.toJson(chapterInfo);
            }
            LogHelper logHelper = ChapterOriginalContentHelper.f188240e;
            StringBuilder sb6 = new StringBuilder();
            sb6.append("【saveChapterInfo】保存章节内容，bookId:");
            sb6.append(chapterInfo.bookId);
            sb6.append(", chapterId:");
            sb6.append(chapterInfo.chapterId);
            sb6.append(", content length:");
            String str = chapterInfo.content;
            if (str != null) {
                numValueOf = Integer.valueOf(str.length());
            }
            sb6.append(numValueOf);
            LogWrapper.info("experience", logHelper.getTag(), sb6.toString(), new Object[0]);
            CacheWrapper.w(chapterInfo.bookId, chapterInfo.chapterId, json, (int) uk4.i0.f321893b.e(), true);
        }

        @Proxy("removeCurrentUserObject")
        @TargetClass("com.dragon.read.local.CacheWrapper")
        public static void a(String str, String str2) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            q85.a.b(str, str2);
            q85.a.a(str, str2);
            LogWrapper.info("experience", q85.a.f301633a.getTag(), "检测书籍删除耗时：%d", new Object[]{Long.valueOf(SystemClock.elapsedRealtime() - jElapsedRealtime)});
            CacheWrapper.s(str, str2);
        }

        public final boolean c(String str, String str2) {
            boolean z17;
            boolean z18 = true;
            if (str != null && str.length() != 0) {
                z17 = false;
            } else {
                z17 = true;
            }
            if (!z17) {
                if (str2 != null && str2.length() != 0) {
                    z18 = false;
                }
                if (!z18) {
                    return CacheWrapper.q(str, str2);
                }
            }
            return false;
        }

        public final void e(String bookId, String chapterId) {
            Intrinsics.checkNotNullParameter(bookId, "bookId");
            Intrinsics.checkNotNullParameter(chapterId, "chapterId");
            LogWrapper.info("experience", ChapterOriginalContentHelper.f188240e.getTag(), "【removeChapterInfo】删除章节内容，bookId:" + bookId + ", chapterId:" + chapterId, new Object[0]);
            a(bookId, chapterId);
        }

        public final ChapterInfo d(String str, String str2) {
            boolean z17;
            boolean z18;
            int length;
            String str3;
            LogWrapper.info("experience", ChapterOriginalContentHelper.f188240e.getTag(), "【loadChapterInfo】bookId=" + str + ", chapterId=" + str2, new Object[0]);
            boolean z19 = true;
            if (str != null && str.length() != 0) {
                z17 = false;
            } else {
                z17 = true;
            }
            if (!z17) {
                if (str2 != null && str2.length() != 0) {
                    z18 = false;
                } else {
                    z18 = true;
                }
                if (!z18) {
                    ChapterInfo chapterInfo = (ChapterInfo) JSONUtils.fromJson((String) CacheWrapper.e(str, str2, true), ChapterInfo.class);
                    LogHelper logHelper = ChapterOriginalContentHelper.f188240e;
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("【loadChapterInfo】读取本地缓存数据成功, bookId: ");
                    sb6.append(str);
                    sb6.append(", chapterId:");
                    sb6.append(str2);
                    sb6.append(", chapterInfo is null?:");
                    if (chapterInfo != null) {
                        z19 = false;
                    }
                    sb6.append(z19);
                    sb6.append(", content length:");
                    if (chapterInfo != null && (str3 = chapterInfo.content) != null) {
                        length = str3.length();
                    } else {
                        length = 0;
                    }
                    sb6.append(length);
                    LogWrapper.info("experience", logHelper.getTag(), sb6.toString(), new Object[0]);
                    return chapterInfo;
                }
                return null;
            }
            return null;
        }
    }

    public final synchronized Single<ChapterInfo> A0(final com.dragon.read.reader.model.d fullArgs) {
        Object objM1374constructorimpl;
        final boolean z17;
        Intrinsics.checkNotNullParameter(fullArgs, "fullArgs");
        if (!fullArgs.e()) {
            Single<ChapterInfo> singleError = Single.error(new IllegalArgumentException("book id or chapter id is null, args=" + fullArgs));
            Intrinsics.checkNotNullExpressionValue(singleError, "error(...)");
            return singleError;
        }
        if (CollectionsKt___CollectionsKt.contains(this.f188241a, fullArgs.b())) {
            Single<ChapterInfo> singleError2 = Single.error(new ErrorCodeException(110, "当前版本不安全"));
            Intrinsics.checkNotNullExpressionValue(singleError2, "error(...)");
            return singleError2;
        }
        String strA = fullArgs.a();
        Intrinsics.checkNotNull(strA);
        String strB = fullArgs.b();
        Intrinsics.checkNotNull(strB);
        try {
            Result.Companion companion = Result.Companion;
            objM1374constructorimpl = Result.m1374constructorimpl(h1(strA, strB));
        } catch (Throwable th6) {
            Result.Companion companion2 = Result.Companion;
            objM1374constructorimpl = Result.m1374constructorimpl(ResultKt.createFailure(th6));
        }
        Throwable thM1377exceptionOrNullimpl = Result.m1377exceptionOrNullimpl(objM1374constructorimpl);
        if (thM1377exceptionOrNullimpl != null) {
            ExceptionMonitor.ensureNotReachHere(thM1377exceptionOrNullimpl);
            objM1374constructorimpl = null;
        }
        final ChapterInfo chapterInfo = (ChapterInfo) objM1374constructorimpl;
        boolean Z = Z(fullArgs, strB, chapterInfo);
        boolean zIsNetworkAvailable = NetworkUtils.isNetworkAvailable();
        if ((n1.b().c(strA, strB) || !Z) && zIsNetworkAvailable) {
            z17 = true;
        } else {
            z17 = false;
        }
        Single<ChapterInfo> singleDefer = SingleDelegate.defer(new Callable() { // from class: com.dragon.read.reader.utils.r
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return ChapterOriginalContentHelper.B0(z17, chapterInfo, this, fullArgs);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleDefer, "defer(...)");
        return singleDefer;
    }

    private static final void I0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void M0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void O0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void S0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void U0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void f1(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void h0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void r0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void x0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void z0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final SingleSource D0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final ObservableSource G0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (ObservableSource) function1.invoke(p07);
    }

    private static final SingleSource Q0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final SingleSource V0(ChapterOriginalContentHelper chapterOriginalContentHelper, ChapterInfo info) {
        Intrinsics.checkNotNullParameter(info, "info");
        chapterOriginalContentHelper.m1(info);
        return chapterOriginalContentHelper.c0(info);
    }

    private static final SingleSource b1(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final SingleSource c1(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final SingleSource d1(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final SingleSource f0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final ChapterInfo k0(ChapterOriginalContentHelper chapterOriginalContentHelper, ChapterInfo info) {
        Intrinsics.checkNotNullParameter(info, "info");
        chapterOriginalContentHelper.m1(info);
        return info;
    }

    private static final ChapterInfo l0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (ChapterInfo) function1.invoke(p07);
    }

    private static final SingleSource n0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final SingleSource o0(ChapterOriginalContentHelper chapterOriginalContentHelper, ChapterInfo info) {
        Intrinsics.checkNotNullParameter(info, "info");
        return chapterOriginalContentHelper.c0(info);
    }

    private static final SingleSource p0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final SingleSource t0(ChapterOriginalContentHelper chapterOriginalContentHelper, ChapterInfo info) {
        Intrinsics.checkNotNullParameter(info, "info");
        chapterOriginalContentHelper.m1(info);
        return chapterOriginalContentHelper.c0(info);
    }

    private static final SingleSource u0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final SingleSource v0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private final boolean X(String str, String str2) {
        if (!NetworkUtils.isNetworkAvailable() && !uk4.i0.f321893b.c(str, str2)) {
            return false;
        }
        return true;
    }

    private static final Unit H0(long j17, Throwable th6) {
        uk4.p0 p0Var = uk4.p0.f321912b;
        if (!p0Var.f(th6)) {
            LogWrapper.info("experience", f188240e.getTag(), "doOnError report", new Object[0]);
            jm4.g.k(j17, z1.a(th6), p0Var.b(th6));
        } else {
            LogWrapper.info("experience", f188240e.getTag(), "doOnError ignore", new Object[0]);
        }
        return Unit.INSTANCE;
    }

    private final void l1(int i17, String str) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.putOpt("reader_type", Integer.valueOf(rm4.r.f307554a.a(str)));
            jSONObject.putOpt("status", Integer.valueOf(i17));
            MonitorUtils.monitorEvent("reader_chapter_info_loading_status", jSONObject, null, null);
        } catch (Exception unused) {
        }
    }

    private final ChapterInfo h1(String str, String str2) {
        if (X(str, str2)) {
            LogWrapper.info("experience", f188240e.getTag(), "【loadChapterInfoFileData】同意加载章节内容，bookId=" + str + ", chapterId=" + str2, new Object[0]);
            return f188238c.d(str, str2);
        }
        LogWrapper.info("experience", f188240e.getTag(), "【loadChapterInfoFileData】拒绝加载章节内容，bookId=" + str + ", chapterId=" + str2, new Object[0]);
        return null;
    }

    private static final Unit q0(ReaderClient readerClient, String str, ChapterInfo chapterInfo) {
        ReaderActivity readerActivity;
        com.dragon.read.reader.ui.w0 w0Var;
        Context context = readerClient.getContext();
        if (context instanceof ReaderActivity) {
            readerActivity = (ReaderActivity) context;
        } else {
            readerActivity = null;
        }
        if (readerActivity != null && (w0Var = readerActivity.f187797x) != null) {
            w0Var.p(str);
        }
        return Unit.INSTANCE;
    }

    private final boolean Z(com.dragon.read.reader.model.d dVar, String str, ChapterInfo chapterInfo) {
        boolean z17;
        if (chapterInfo == null) {
            return true;
        }
        String str2 = chapterInfo.version;
        String strC = dVar.c();
        if (strC != null && strC.length() != 0) {
            z17 = false;
        } else {
            z17 = true;
        }
        if (z17) {
            LogWrapper.info("experience", f188240e.getTag(), "expectVersion为空，不检查版本号，chapterId=%s", new Object[]{str});
            return true;
        }
        LogWrapper.info("experience", f188240e.getTag(), "章节版本号是否一致，chapterId=%s，expectVersion:%s, chapterInfo version:%s", new Object[]{str, dVar.c(), str2});
        return TextUtils.equals(dVar.c(), str2);
    }

    private final boolean a0(ReaderClient readerClient, String str, ChapterInfo chapterInfo) {
        if (chapterInfo == null) {
            return true;
        }
        String str2 = chapterInfo.version;
        ChapterItem chapterItemE0 = readerClient.getCatalogProvider().e0(str);
        if (chapterItemE0 != null && !q.e(chapterItemE0)) {
            LogWrapper.info("experience", f188240e.getTag(), "章节版本号是否一致，chapterId=%s，catalog version:%s, chapterInfo version:%s", new Object[]{str, chapterItemE0.getVersion(), str2});
            return TextUtils.equals(chapterItemE0.getVersion(), str2);
        }
        LogWrapper.info("experience", f188240e.getTag(), "目录数据为空，不检查版本号，chapterId=%s", new Object[]{str});
        return true;
    }

    private final Single<ItemContent> E0(final String str, final String str2, FullReqType fullReqType) {
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        FullRequest fullRequest = new FullRequest();
        fullRequest.itemId = str2;
        fullRequest.bookId = str;
        fullRequest.keyRegisterTs = w85.i.C(uk4.a1.f321862b.b());
        if (NumberUtils.parse(str, 0L) != 0 && NumberUtils.parse(str2, 0L) != 0) {
            if (fullReqType != null) {
                fullRequest.reqType = fullReqType;
            }
            com.dragon.read.reader.depend.a aVar = com.dragon.read.reader.depend.a.f185562b;
            final Object objU = aVar.u("reader_full_trace");
            final Object objV = aVar.v(objU, "/reading/reader/full/v");
            Observable observableTimeout = u56.a.n(fullRequest).compose(uk4.s0.f321919b.c()).timeout(7L, TimeUnit.SECONDS);
            final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.d0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return ChapterOriginalContentHelper.F0(objU, objV, this, str, str2, jElapsedRealtime, (FullResponse) obj);
                }
            };
            Observable observableFlatMap = observableTimeout.flatMap(new Function() { // from class: com.dragon.read.reader.utils.e0
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj) {
                    return ChapterOriginalContentHelper.G0(function1, obj);
                }
            });
            final Function1 function12 = new Function1() { // from class: com.dragon.read.reader.utils.f0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return ChapterOriginalContentHelper.H0(jElapsedRealtime, (Throwable) obj);
                }
            };
            Single<ItemContent> singleFromObservable = Single.fromObservable(observableFlatMap.doOnError(new Consumer() { // from class: com.dragon.read.reader.utils.g0
                @Override // io.reactivex.functions.Consumer
                public final void accept(Object obj) {
                    ChapterOriginalContentHelper.I0(function12, obj);
                }
            }).doFinally(new Action() { // from class: com.dragon.read.reader.utils.h0
                @Override // io.reactivex.functions.Action
                public final void run() {
                    ChapterOriginalContentHelper.J0(objU);
                }
            }).subscribeOn(Schedulers.io()));
            Intrinsics.checkNotNullExpressionValue(singleFromObservable, "fromObservable(...)");
            return singleFromObservable;
        }
        LogWrapper.error("experience", f188240e.getTag(), "使用本地书的bookId或者chapterId加载章节内容", new Object[0]);
        Single<ItemContent> singleError = Single.error(new ErrorCodeException(-4007, "使用本地书的bookId或者chapterId加载章节内容"));
        Intrinsics.checkNotNullExpressionValue(singleError, "error(...)");
        return singleError;
    }

    private static final SingleSource d0(final ChapterInfo chapterInfo, final long j17, final ChapterOriginalContentHelper chapterOriginalContentHelper) throws ErrorCodeException {
        if (!TextUtils.isEmpty(chapterInfo.content)) {
            if (chapterInfo.keyVersion == Integer.MIN_VALUE) {
                LogWrapper.info("experience", f188240e.getTag(), "[ReaderSDKBiz] 解析章节内容耗时:" + (SystemClock.elapsedRealtime() - j17) + ", bookId:" + chapterInfo.bookId + ", chapterId:" + chapterInfo.chapterId, new Object[0]);
                jm4.u.f267803d.a(0, chapterInfo.bookId, chapterInfo.chapterId, SystemClock.elapsedRealtime() - j17, chapterInfo.content.length());
                return Single.just(chapterInfo);
            }
            Single<DecryptKey> singleO = w85.i.o(uk4.a1.f321862b.b(), chapterInfo.keyVersion);
            final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.y
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return ChapterOriginalContentHelper.e0(chapterInfo, j17, chapterOriginalContentHelper, (DecryptKey) obj);
                }
            };
            Single<R> singleFlatMap = singleO.flatMap(new Function() { // from class: com.dragon.read.reader.utils.z
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj) {
                    return ChapterOriginalContentHelper.f0(function1, obj);
                }
            });
            final Function1 function12 = new Function1() { // from class: com.dragon.read.reader.utils.a0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return ChapterOriginalContentHelper.g0(chapterInfo, chapterOriginalContentHelper, j17, (Throwable) obj);
                }
            };
            return singleFlatMap.doOnError(new Consumer() { // from class: com.dragon.read.reader.utils.b0
                @Override // io.reactivex.functions.Consumer
                public final void accept(Object obj) {
                    ChapterOriginalContentHelper.h0(function12, obj);
                }
            });
        }
        throw new ErrorCodeException(-4002, "no content to decode");
    }

    private static final SingleSource C0(ChapterInfo chapterInfo, ChapterOriginalContentHelper chapterOriginalContentHelper, com.dragon.read.reader.model.d dVar, Throwable it6) {
        Intrinsics.checkNotNullParameter(it6, "it");
        chapterInfo.content = null;
        return chapterOriginalContentHelper.P0(dVar);
    }

    private static final SingleSource B0(boolean z17, final ChapterInfo chapterInfo, final ChapterOriginalContentHelper chapterOriginalContentHelper, final com.dragon.read.reader.model.d dVar) {
        if (!z17 && chapterInfo != null && !TextUtils.isEmpty(chapterInfo.content)) {
            Single<ChapterInfo> singleC0 = chapterOriginalContentHelper.c0(chapterInfo);
            final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.i1
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return ChapterOriginalContentHelper.C0(chapterInfo, chapterOriginalContentHelper, dVar, (Throwable) obj);
                }
            };
            Single<ChapterInfo> singleOnErrorResumeNext = singleC0.onErrorResumeNext(new Function() { // from class: com.dragon.read.reader.utils.j1
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj) {
                    return ChapterOriginalContentHelper.D0(function1, obj);
                }
            });
            Intrinsics.checkNotNull(singleOnErrorResumeNext);
            return singleOnErrorResumeNext;
        }
        return chapterOriginalContentHelper.P0(dVar);
    }

    private static final SingleSource e0(ChapterInfo chapterInfo, long j17, ChapterOriginalContentHelper chapterOriginalContentHelper, DecryptKey key) throws ErrorCodeException {
        Intrinsics.checkNotNullParameter(key, "key");
        String strA = o.a(chapterInfo, key);
        LogWrapper.info("experience", f188240e.getTag(), "[ReaderSDKBiz] 解析章节内容耗时:" + (SystemClock.elapsedRealtime() - j17) + ", bookId:" + chapterInfo.bookId + ", chapterId:" + chapterInfo.chapterId, new Object[0]);
        if (!TextUtils.isEmpty(strA)) {
            chapterInfo.content = strA;
            chapterOriginalContentHelper.i1(0);
            jm4.u.f267803d.a(0, chapterInfo.bookId, chapterInfo.chapterId, SystemClock.elapsedRealtime() - j17, chapterInfo.content.length());
            return Single.just(chapterInfo);
        }
        throw new ErrorCodeException(-4003, "empty content after parsing");
    }

    private static final Unit g0(ChapterInfo chapterInfo, ChapterOriginalContentHelper chapterOriginalContentHelper, long j17, Throwable th6) {
        int iA = z1.a(th6);
        LogWrapper.error("experience", f188240e.getTag(), "解密章节数据出错: " + chapterInfo + ", code = " + iA, new Object[0]);
        chapterOriginalContentHelper.i1(iA);
        jm4.u.f267803d.a(iA, chapterInfo.bookId, chapterInfo.chapterId, SystemClock.elapsedRealtime() - j17, chapterInfo.content.length());
        return Unit.INSTANCE;
    }

    private static final SingleSource a1(Function3 function3, String str, ItemContent itemContent, ItemContent chapterResp) throws ErrorCodeException {
        Intrinsics.checkNotNullParameter(chapterResp, "chapterResp");
        short s17 = chapterResp.cryptStatus;
        if (s17 != 2) {
            if (s17 == 1) {
                ChapterInfo chapterInfoA = ChapterInfo.a(chapterResp, false);
                if (chapterInfoA != null) {
                    if (function3 != null) {
                        function3.invoke(str, Boolean.FALSE, itemContent);
                    }
                    return Single.just(chapterInfoA);
                }
                LogWrapper.warn("experience", f188240e.getTag(), "章节没有加密，但是content 却是空的", new Object[0]);
                throw new ErrorCodeException(-4002, "chapter content is empty");
            }
            if (s17 == 0) {
                LogWrapper.warn("experience", f188240e.getTag(), "章节已被加密", new Object[0]);
                ChapterInfo chapterInfoA2 = ChapterInfo.a(chapterResp, true);
                if (chapterInfoA2 != null) {
                    if (function3 != null) {
                        function3.invoke(str, Boolean.FALSE, itemContent);
                    }
                    return Single.just(chapterInfoA2);
                }
                throw new ErrorCodeException(-4002, "加密章节内容为空");
            }
            throw new ErrorCodeException(-4004, "无效内容");
        }
        LogWrapper.warn("experience", f188240e.getTag(), "已经注册密钥成功, 后台却返回密钥失效，为避免循环，终止请求", new Object[0]);
        throw new ErrorCodeException(-4006, "invalid status");
    }

    private static final Unit T0(long j17, String str, String str2, int i17, Throwable th6) {
        jm4.u.f267803d.b(z1.a(th6), str, str2, i17, -1, false, SystemClock.elapsedRealtime() - j17, 0, "net");
        return Unit.INSTANCE;
    }

    private static final Unit L0(long j17, ReaderClient readerClient, String str, String str2, ChapterInfo chapterInfo) {
        ReaderActivity readerActivity;
        km4.g readerSession;
        jm4.u uVarE;
        long jElapsedRealtime = SystemClock.elapsedRealtime() - j17;
        Context context = readerClient.getContext();
        if (context instanceof ReaderActivity) {
            readerActivity = (ReaderActivity) context;
        } else {
            readerActivity = null;
        }
        if (readerActivity != null && (readerSession = readerActivity.getReaderSession()) != null && (uVarE = readerSession.E()) != null) {
            uVarE.j(0, str, str2, false, jElapsedRealtime, chapterInfo.content.length(), "net");
        }
        return Unit.INSTANCE;
    }

    private static final Unit N0(long j17, ReaderClient readerClient, String str, String str2, Throwable th6) {
        ReaderActivity readerActivity;
        km4.g readerSession;
        jm4.u uVarE;
        long jElapsedRealtime = SystemClock.elapsedRealtime() - j17;
        Context context = readerClient.getContext();
        if (context instanceof ReaderActivity) {
            readerActivity = (ReaderActivity) context;
        } else {
            readerActivity = null;
        }
        if (readerActivity != null && (readerSession = readerActivity.getReaderSession()) != null && (uVarE = readerSession.E()) != null) {
            uVarE.j(z1.a(th6), str, str2, false, jElapsedRealtime, 0, "net");
        }
        return Unit.INSTANCE;
    }

    private static final Unit R0(long j17, String str, String str2, int i17, ChapterInfo chapterInfo) {
        jm4.u.f267803d.b(0, str, str2, i17, chapterInfo.genreType, false, SystemClock.elapsedRealtime() - j17, chapterInfo.content.length(), "net");
        return Unit.INSTANCE;
    }

    private final Single<ChapterInfo> K0(final ReaderClient readerClient, final String str, final String str2, FullReqType fullReqType, Function3<? super String, ? super Boolean, ? super ItemContent, Unit> function3) {
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        Single<ChapterInfo> singleW0 = W0(str, str2, readerClient.getReaderConfig().getReaderType(str), fullReqType, function3);
        final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.d1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.L0(jElapsedRealtime, readerClient, str, str2, (ChapterInfo) obj);
            }
        };
        Single<ChapterInfo> singleDoOnSuccess = singleW0.doOnSuccess(new Consumer() { // from class: com.dragon.read.reader.utils.e1
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                ChapterOriginalContentHelper.M0(function1, obj);
            }
        });
        final Function1 function12 = new Function1() { // from class: com.dragon.read.reader.utils.f1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.N0(jElapsedRealtime, readerClient, str, str2, (Throwable) obj);
            }
        };
        Single<ChapterInfo> singleDoOnError = singleDoOnSuccess.doOnError(new Consumer() { // from class: com.dragon.read.reader.utils.g1
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                ChapterOriginalContentHelper.O0(function12, obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleDoOnError, "doOnError(...)");
        return singleDoOnError;
    }

    private static final SingleSource m0(String str, String str2, ChapterInfo chapterInfo, ChapterOriginalContentHelper chapterOriginalContentHelper, Throwable it6) {
        Intrinsics.checkNotNullParameter(it6, "it");
        int iA = z1.a(it6);
        LogWrapper.info("experience", f188240e.getTag(), "强制刷新加载内容出错，bookId:" + str + ", chapterId:" + str2 + ", chapterInfo:" + chapterInfo + ", code:" + iA, new Object[0]);
        if (chapterInfo != null && !chapterOriginalContentHelper.Y(iA)) {
            return Single.just(chapterInfo);
        }
        return Single.error(it6);
    }

    public final Single<ChapterInfo> W0(final String bookId, final String chapterId, final int i17, final FullReqType fullReqType, final Function3<? super String, ? super Boolean, ? super ItemContent, Unit> function3) {
        Intrinsics.checkNotNullParameter(bookId, "bookId");
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        this.f188242b = 20002;
        Single<ItemContent> singleE0 = E0(bookId, chapterId, fullReqType);
        final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.c0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.Y0(function3, chapterId, this, bookId, fullReqType, (ItemContent) obj);
            }
        };
        Single<R> singleFlatMap = singleE0.flatMap(new Function() { // from class: com.dragon.read.reader.utils.n0
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return ChapterOriginalContentHelper.d1(function1, obj);
            }
        });
        final Function1 function12 = new Function1() { // from class: com.dragon.read.reader.utils.y0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.e1(jElapsedRealtime, this, bookId, chapterId, i17, (ChapterInfo) obj);
            }
        };
        Single<ChapterInfo> singleDoOnSuccess = singleFlatMap.doOnSuccess(new Consumer() { // from class: com.dragon.read.reader.utils.h1
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                ChapterOriginalContentHelper.f1(function12, obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleDoOnSuccess, "doOnSuccess(...)");
        return singleDoOnSuccess;
    }

    public final synchronized Single<ChapterInfo> i0(final ReaderClient client, final String bookId, final String chapterId, final FullReqType fullReqType, final Function3<? super String, ? super Boolean, ? super ItemContent, Unit> function3) {
        Intrinsics.checkNotNullParameter(client, "client");
        Intrinsics.checkNotNullParameter(bookId, "bookId");
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        if (this.f188241a.contains(chapterId)) {
            Single<ChapterInfo> singleError = Single.error(new ErrorCodeException(110, "当前版本不安全"));
            Intrinsics.checkNotNullExpressionValue(singleError, "error(...)");
            return singleError;
        }
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        Single singleDefer = SingleDelegate.defer(new Callable() { // from class: com.dragon.read.reader.utils.k0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return ChapterOriginalContentHelper.j0(bookId, chapterId, function3, this, client, fullReqType);
            }
        });
        final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.l0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.w0(jElapsedRealtime, this, bookId, chapterId, client, (ChapterInfo) obj);
            }
        };
        Single singleDoOnSuccess = singleDefer.doOnSuccess(new Consumer() { // from class: com.dragon.read.reader.utils.m0
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                ChapterOriginalContentHelper.x0(function1, obj);
            }
        });
        final Function1 function12 = new Function1() { // from class: com.dragon.read.reader.utils.o0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.y0(this.f188381a, chapterId, bookId, client, jElapsedRealtime, (Throwable) obj);
            }
        };
        Single<ChapterInfo> singleDoOnError = singleDoOnSuccess.doOnError(new Consumer() { // from class: com.dragon.read.reader.utils.p0
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                ChapterOriginalContentHelper.z0(function12, obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleDoOnError, "doOnError(...)");
        return singleDoOnError;
    }

    private static final Unit e1(long j17, ChapterOriginalContentHelper chapterOriginalContentHelper, String str, String str2, int i17, ChapterInfo chapterInfo) {
        long jElapsedRealtime = SystemClock.elapsedRealtime() - j17;
        LogWrapper.info("experience", f188240e.getTag(), "[ReaderSDKBiz] 加载网络章节数据成功: %s, 耗时%dms.", new Object[]{chapterInfo, Long.valueOf(jElapsedRealtime)});
        k1(chapterOriginalContentHelper, ReportType.NETWORK, str, str2, i17, j17, chapterInfo.parseMode, chapterInfo.content.length(), false, 128, null);
        return Unit.INSTANCE;
    }

    private static final Unit y0(ChapterOriginalContentHelper chapterOriginalContentHelper, String str, String str2, ReaderClient readerClient, long j17, Throwable th6) {
        ReaderActivity readerActivity;
        km4.g readerSession;
        jm4.u uVarE;
        int iA = z1.a(th6);
        if (iA != 110) {
            if (iA == 100000000) {
                iA = chapterOriginalContentHelper.f188242b;
            }
        } else {
            chapterOriginalContentHelper.f188241a.add(str);
        }
        LogWrapper.error("experience", f188240e.getTag(), "加载章节数据出错: bookId = %s, chapterId = %s, error = %s", new Object[]{str2, str, Log.getStackTraceString(th6)});
        chapterOriginalContentHelper.l1(iA, str2);
        readerClient.getReaderMonitor().b("ssreader_chapter_content_load_duration", false, j17);
        Context context = readerClient.getContext();
        if (context instanceof ReaderActivity) {
            readerActivity = (ReaderActivity) context;
        } else {
            readerActivity = null;
        }
        if (readerActivity != null && (readerSession = readerActivity.getReaderSession()) != null && (uVarE = readerSession.E()) != null) {
            uVarE.j(iA, str2, str, false, SystemClock.elapsedRealtime() - j17, 0, "all");
        }
        return Unit.INSTANCE;
    }

    private static final SingleSource Y0(final Function3 function3, final String str, final ChapterOriginalContentHelper chapterOriginalContentHelper, final String str2, final FullReqType fullReqType, final ItemContent resp) throws ErrorCodeException {
        Intrinsics.checkNotNullParameter(resp, "resp");
        short s17 = resp.cryptStatus;
        if (s17 == 1) {
            ChapterInfo chapterInfoA = ChapterInfo.a(resp, false);
            if (chapterInfoA != null) {
                if (function3 != null) {
                    function3.invoke(str, Boolean.FALSE, resp);
                }
                return Single.just(chapterInfoA);
            }
            LogWrapper.warn("experience", f188240e.getTag(), "章节没有加密，但是content 却是空的", new Object[0]);
            throw new ErrorCodeException(-4002, "chapter content is empty");
        }
        if (s17 == 2) {
            LogWrapper.warn("experience", f188240e.getTag(), "章节密钥不存在或者密钥已过期，重新请求", new Object[0]);
            Single<DecryptKey> singleF = w85.i.F(uk4.a1.f321862b.b());
            final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.l1
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return ChapterOriginalContentHelper.Z0(this.f188369a, str2, str, fullReqType, function3, resp, (DecryptKey) obj);
                }
            };
            return singleF.flatMap(new Function() { // from class: com.dragon.read.reader.utils.m1
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj) {
                    return ChapterOriginalContentHelper.c1(function1, obj);
                }
            });
        }
        if (s17 == 0) {
            LogWrapper.warn("experience", f188240e.getTag(), "章节已被加密", new Object[0]);
            ChapterInfo chapterInfoA2 = ChapterInfo.a(resp, true);
            if (chapterInfoA2 != null) {
                if (!TextUtils.isEmpty(resp.content)) {
                    if (function3 != null) {
                        function3.invoke(str, Boolean.FALSE, resp);
                    }
                    return Single.just(chapterInfoA2);
                }
                throw new ErrorCodeException(-4002, "加密章节内容为空");
            }
            throw new ErrorCodeException(-4002, "加密章节内容为空");
        }
        throw new ErrorCodeException(-4004, "无效内容");
    }

    private static final Unit w0(long j17, ChapterOriginalContentHelper chapterOriginalContentHelper, String str, String str2, ReaderClient readerClient, ChapterInfo chapterInfo) {
        ReaderActivity readerActivity;
        km4.g readerSession;
        jm4.u uVarE;
        LogWrapper.info("experience", f188240e.getTag(), "[ReaderSDKBiz] 加载章节内容总体耗时: " + (SystemClock.elapsedRealtime() - j17), new Object[0]);
        k1(chapterOriginalContentHelper, ReportType.ALL, str, str2, readerClient.getReaderConfig().getReaderType(str), j17, chapterInfo.parseMode, chapterInfo.content.length(), false, 128, null);
        Context context = readerClient.getContext();
        if (context instanceof ReaderActivity) {
            readerActivity = (ReaderActivity) context;
        } else {
            readerActivity = null;
        }
        if (readerActivity != null && (readerSession = readerActivity.getReaderSession()) != null && (uVarE = readerSession.E()) != null) {
            uVarE.j(0, str, str2, false, SystemClock.elapsedRealtime() - j17, chapterInfo.content.length(), "all");
        }
        chapterOriginalContentHelper.l1(0, str);
        com.dragon.read.reader.extend.booklink.i iVar = com.dragon.read.reader.extend.booklink.i.f185697a;
        Intrinsics.checkNotNull(chapterInfo);
        iVar.j(chapterInfo);
        readerClient.getReaderMonitor().b("ssreader_chapter_content_load_duration", true, j17);
        return Unit.INSTANCE;
    }

    private static final SingleSource j0(final String str, final String str2, final Function3 function3, final ChapterOriginalContentHelper chapterOriginalContentHelper, final ReaderClient readerClient, final FullReqType fullReqType) {
        ChapterOriginalContentHelper chapterOriginalContentHelper2;
        Object objM1374constructorimpl;
        Boolean boolValueOf;
        boolean z17;
        boolean z18;
        String str3;
        boolean z19;
        ReaderActivity readerActivity;
        km4.g readerSession;
        jm4.u uVarE;
        ReaderActivity readerActivity2;
        km4.g readerSession2;
        jm4.u uVarE2;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        try {
            Result.Companion companion = Result.Companion;
            chapterOriginalContentHelper.f188242b = 20001;
            ChapterInfo chapterInfoH1 = chapterOriginalContentHelper.h1(str, str2);
            if (chapterInfoH1 != null) {
                Context context = readerClient.getContext();
                if (context instanceof ReaderActivity) {
                    readerActivity2 = (ReaderActivity) context;
                } else {
                    readerActivity2 = null;
                }
                if (readerActivity2 != null && (readerSession2 = readerActivity2.getReaderSession()) != null && (uVarE2 = readerSession2.E()) != null) {
                    uVarE2.j(0, str, str2, false, SystemClock.elapsedRealtime() - jElapsedRealtime, chapterInfoH1.content.length(), "cache");
                }
                chapterOriginalContentHelper2 = chapterOriginalContentHelper;
                try {
                    k1(chapterOriginalContentHelper, ReportType.LOCAL, str, str2, readerClient.getReaderConfig().getReaderType(str), jElapsedRealtime, chapterInfoH1.parseMode, chapterInfoH1.content.length(), false, 128, null);
                } catch (Throwable th6) {
                    th = th6;
                    Result.Companion companion2 = Result.Companion;
                    objM1374constructorimpl = Result.m1374constructorimpl(ResultKt.createFailure(th));
                }
            } else {
                chapterOriginalContentHelper2 = chapterOriginalContentHelper;
            }
            objM1374constructorimpl = Result.m1374constructorimpl(chapterInfoH1);
        } catch (Throwable th7) {
            th = th7;
            chapterOriginalContentHelper2 = chapterOriginalContentHelper;
        }
        Object obj = objM1374constructorimpl;
        Throwable thM1377exceptionOrNullimpl = Result.m1377exceptionOrNullimpl(obj);
        boolean z27 = false;
        if (thM1377exceptionOrNullimpl != null) {
            ExceptionMonitor.ensureNotReachHere(thM1377exceptionOrNullimpl);
            LogWrapper.error("experience", f188240e.getTag(), "加载缓存章节数据出错, error = " + Log.getStackTraceString(thM1377exceptionOrNullimpl), new Object[0]);
            chapterOriginalContentHelper.j1(ReportType.LOCAL, str, str2, readerClient.getReaderConfig().getReaderType(str), jElapsedRealtime, -1, -1, false);
            if (chapterOriginalContentHelper2.X(str, str2) && f188238c.c(str, str2)) {
                Context context2 = readerClient.getContext();
                if (context2 instanceof ReaderActivity) {
                    readerActivity = (ReaderActivity) context2;
                } else {
                    readerActivity = null;
                }
                if (readerActivity != null && (readerSession = readerActivity.getReaderSession()) != null && (uVarE = readerSession.E()) != null) {
                    uVarE.j(-4001, str, str2, false, SystemClock.elapsedRealtime() - jElapsedRealtime, 0, "cache");
                }
            }
            obj = null;
        }
        final ChapterInfo chapterInfo = (ChapterInfo) obj;
        LogHelper logHelper = f188240e;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("[ReaderSDKBiz] 加载本地章节数据: bookId:");
        sb6.append(str);
        sb6.append(", chapterId:");
        sb6.append(str2);
        sb6.append(", 耗时");
        sb6.append(SystemClock.elapsedRealtime() - jElapsedRealtime);
        sb6.append("ms, chapterInfo is avail: ");
        if (chapterInfo != null && (str3 = chapterInfo.content) != null) {
            if (str3.length() > 0) {
                z19 = true;
            } else {
                z19 = false;
            }
            boolValueOf = Boolean.valueOf(z19);
        } else {
            boolValueOf = null;
        }
        sb6.append(boolValueOf);
        LogWrapper.info("experience", logHelper.getTag(), sb6.toString(), new Object[0]);
        final ChapterOriginalContentHelper chapterOriginalContentHelper3 = chapterOriginalContentHelper2;
        if (function3 != null) {
            function3.invoke(str2, Boolean.TRUE, null);
        }
        if (chapterInfo != null) {
            z17 = true;
        } else {
            z17 = false;
        }
        jm4.n.c(str2, z17);
        boolean zIsNetworkAvailable = NetworkUtils.isNetworkAvailable();
        boolean zA0 = chapterOriginalContentHelper3.a0(readerClient, str2, chapterInfo);
        if ((n1.b().c(str, str2) || !zA0) && zIsNetworkAvailable) {
            z18 = true;
        } else {
            z18 = false;
        }
        LogWrapper.info("experience", logHelper.getTag(), "强制刷新: %b,  当前网络连通: %b, bookId:%s, chapterId:%s", new Object[]{Boolean.valueOf(z18), Boolean.valueOf(zIsNetworkAvailable), str, str2});
        if (chapterInfo != null && TextUtils.isEmpty(chapterInfo.content)) {
            z27 = true;
        }
        if (!z18 && chapterInfo != null && !z27) {
            Single<ChapterInfo> singleC0 = chapterOriginalContentHelper3.c0(chapterInfo);
            final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.w0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj2) {
                    return ChapterOriginalContentHelper.q0(readerClient, str2, (ChapterInfo) obj2);
                }
            };
            Single<ChapterInfo> singleDoOnSuccess = singleC0.doOnSuccess(new Consumer() { // from class: com.dragon.read.reader.utils.x0
                @Override // io.reactivex.functions.Consumer
                public final void accept(Object obj2) {
                    ChapterOriginalContentHelper.r0(function1, obj2);
                }
            });
            final Function1 function12 = new Function1() { // from class: com.dragon.read.reader.utils.z0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj2) {
                    return ChapterOriginalContentHelper.s0(chapterInfo, chapterOriginalContentHelper, readerClient, str, str2, fullReqType, function3, (Throwable) obj2);
                }
            };
            return singleDoOnSuccess.onErrorResumeNext(new Function() { // from class: com.dragon.read.reader.utils.a1
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj2) {
                    return ChapterOriginalContentHelper.v0(function12, obj2);
                }
            });
        }
        Single<ChapterInfo> singleK0 = chapterOriginalContentHelper.K0(readerClient, str, str2, fullReqType, function3);
        final Function1 function13 = new Function1() { // from class: com.dragon.read.reader.utils.q0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj2) {
                return ChapterOriginalContentHelper.k0(this.f188391a, (ChapterInfo) obj2);
            }
        };
        Single<R> map = singleK0.map(new Function() { // from class: com.dragon.read.reader.utils.r0
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj2) {
                return ChapterOriginalContentHelper.l0(function13, obj2);
            }
        });
        final Function1 function14 = new Function1() { // from class: com.dragon.read.reader.utils.s0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj2) {
                return ChapterOriginalContentHelper.m0(str, str2, chapterInfo, chapterOriginalContentHelper3, (Throwable) obj2);
            }
        };
        Single singleOnErrorResumeNext = map.onErrorResumeNext((Function<? super Throwable, ? extends SingleSource<? extends R>>) new Function() { // from class: com.dragon.read.reader.utils.t0
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj2) {
                return ChapterOriginalContentHelper.n0(function14, obj2);
            }
        });
        final Function1 function15 = new Function1() { // from class: com.dragon.read.reader.utils.u0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj2) {
                return ChapterOriginalContentHelper.o0(this.f188430a, (ChapterInfo) obj2);
            }
        };
        return singleOnErrorResumeNext.flatMap(new Function() { // from class: com.dragon.read.reader.utils.v0
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj2) {
                return ChapterOriginalContentHelper.p0(function15, obj2);
            }
        });
    }

    private static final SingleSource Z0(ChapterOriginalContentHelper chapterOriginalContentHelper, String str, final String str2, FullReqType fullReqType, final Function3 function3, final ItemContent itemContent, DecryptKey s17) {
        Intrinsics.checkNotNullParameter(s17, "s");
        LogWrapper.warn("experience", f188240e.getTag(), "注册密钥成功, s = %s，重新请求章节", new Object[]{s17});
        Single<ItemContent> singleE0 = chapterOriginalContentHelper.E0(str, str2, fullReqType);
        final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.i0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.a1(function3, str2, itemContent, (ItemContent) obj);
            }
        };
        return singleE0.flatMap(new Function() { // from class: com.dragon.read.reader.utils.j0
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return ChapterOriginalContentHelper.b1(function1, obj);
            }
        });
    }

    private static final ObservableSource F0(Object obj, Object obj2, ChapterOriginalContentHelper chapterOriginalContentHelper, String str, String str2, long j17, FullResponse result) throws ErrorCodeException {
        Intrinsics.checkNotNullParameter(result, "result");
        com.dragon.read.reader.depend.a.f185562b.t(obj, obj2, "/reading/reader/full/v");
        if (chapterOriginalContentHelper.Y(result.code.getValue())) {
            LogWrapper.info("experience", f188240e.getTag(), "章节内容处于下架状态，删除本地缓存，bookId:" + str + ", chapterId=" + str2 + ", code:" + result.code, new Object[0]);
            f188238c.e(str, str2);
        }
        if (result.code == ReaderApiERR.SUCCESS) {
            if (result.data != null) {
                i2.f188343a.f(SystemClock.elapsedRealtime() - j17);
                jm4.g.l(j17);
                return Observable.just(result.data);
            }
            i2.f188343a.g();
            throw new ErrorCodeException(-4005, "chapter info is empty");
        }
        i2.f188343a.g();
        throw new ErrorCodeException(result.code.getValue(), result.message);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ Single X0(ChapterOriginalContentHelper chapterOriginalContentHelper, String str, String str2, int i17, FullReqType fullReqType, Function3 function3, int i18, Object obj) {
        if ((i18 & 16) != 0) {
            function3 = null;
        }
        return chapterOriginalContentHelper.W0(str, str2, i17, fullReqType, function3);
    }

    private final void j1(ReportType reportType, String str, String str2, int i17, long j17, int i18, int i19, boolean z17) {
        CommonConst$STATUS commonConst$STATUS;
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        JSONObject jSONObject3 = new JSONObject();
        try {
            jSONObject.putOpt(reportType.getDurationKey(), Long.valueOf(SystemClock.elapsedRealtime() - j17));
            jSONObject.putOpt(reportType.getContentLengthKey(), Integer.valueOf(i19));
            jSONObject2.putOpt("bookId", str);
            jSONObject2.putOpt("chapterId", str2);
            jSONObject3.putOpt("reader_type", Integer.valueOf(i17));
            if (z17) {
                commonConst$STATUS = CommonConst$STATUS.SUCCESS;
            } else {
                commonConst$STATUS = CommonConst$STATUS.FAIL;
            }
            jSONObject3.putOpt("status", commonConst$STATUS.getValue());
            jSONObject3.putOpt("parse_model", Integer.valueOf(i18));
            MonitorUtils.monitorEvent("reader_chapter_duration_v2", jSONObject3, jSONObject, jSONObject2);
        } catch (Exception unused) {
        }
    }

    private static final SingleSource s0(ChapterInfo chapterInfo, final ChapterOriginalContentHelper chapterOriginalContentHelper, ReaderClient readerClient, String str, String str2, FullReqType fullReqType, Function3 function3, Throwable t17) {
        Intrinsics.checkNotNullParameter(t17, "t");
        LogWrapper.error("experience", f188240e.getTag(), "清除本地缓存，并请求新的内容，因为无法对本地章节信息解密，本地 KeyVer = " + chapterInfo.keyVersion + "，error = " + Log.getStackTraceString(t17), new Object[0]);
        chapterInfo.content = null;
        chapterOriginalContentHelper.m1(chapterInfo);
        Single<ChapterInfo> singleK0 = chapterOriginalContentHelper.K0(readerClient, str, str2, fullReqType, function3);
        final Function1 function1 = new Function1() { // from class: com.dragon.read.reader.utils.b1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return ChapterOriginalContentHelper.t0(this.f188258a, (ChapterInfo) obj);
            }
        };
        return singleK0.flatMap(new Function() { // from class: com.dragon.read.reader.utils.c1
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return ChapterOriginalContentHelper.u0(function1, obj);
            }
        });
    }

    static /* synthetic */ void k1(ChapterOriginalContentHelper chapterOriginalContentHelper, ReportType reportType, String str, String str2, int i17, long j17, int i18, int i19, boolean z17, int i27, Object obj) {
        boolean z18;
        if ((i27 & 128) != 0) {
            z18 = true;
        } else {
            z18 = z17;
        }
        chapterOriginalContentHelper.j1(reportType, str, str2, i17, j17, i18, i19, z18);
    }
}