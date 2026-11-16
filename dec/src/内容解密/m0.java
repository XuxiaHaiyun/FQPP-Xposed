package gn4;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.bytedance.article.common.monitor.stack.ExceptionMonitor;
import com.bytedance.covode.number.Covode;
import com.bytedance.framwork.core.monitor.MonitorUtils;
import com.dragon.read.base.http.exception.ErrorCodeException;
import com.dragon.read.base.util.LogHelper;
import com.dragon.read.base.util.LogWrapper;
import com.dragon.read.component.biz.api.NsReaderServiceApi;
import com.dragon.read.reader.DecryptKey;
import com.dragon.read.reader.download.ChapterInfo;
import com.dragon.read.rpc.model.FullRequest;
import com.dragon.read.rpc.model.FullResponse;
import com.dragon.read.rpc.model.ItemContent;
import com.dragon.read.rpc.model.ReaderApiERR;
import com.dragon.read.user.AcctManager;
import com.dragon.read.util.b8;
import com.dragon.read.util.kotlin.ExceptionKt;
import com.dragon.reader.lib.ReaderClient;
import com.ss.android.ugc.bytex.taskmonitor.proxy.SingleDelegate;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes9.dex */
public final class m0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final a f250791b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final int f250792c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final LogHelper f250793d;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private int f250794a;

    public static final class a {
        static {
            Covode.recordClassIndex(603878);
        }

        private a() {
        }

        public /* synthetic */ a(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    static {
        Covode.recordClassIndex(603877);
        f250791b = new a(null);
        f250792c = 8;
        f250793d = new LogHelper("ChapterOriginalContentHelper");
    }

    private final Single<ChapterInfo> A(final ChapterInfo chapterInfo) {
        this.f250794a = 20003;
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        Single<ChapterInfo> singleDefer = SingleDelegate.defer(new Callable() { // from class: gn4.q
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return m0.B(chapterInfo, jElapsedRealtime, this);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleDefer, "defer(...)");
        return singleDefer;
    }

    private final void f0(int i17) {
        try {
            MonitorUtils.monitorEvent("reader_chapter_decrypt_status", new JSONObject().putOpt("status", String.valueOf(i17)), null, null);
        } catch (Exception e17) {
            ExceptionMonitor.ensureNotReachHere(e17);
        }
    }

    private static final ObservableSource U(FullResponse result) throws ErrorCodeException {
        Intrinsics.checkNotNullParameter(result, "result");
        if (result.code == ReaderApiERR.SUCCESS) {
            ItemContent itemContent = result.data;
            if (itemContent != null) {
                return Observable.just(itemContent);
            }
            throw new ErrorCodeException(-8, "chapter info is empty");
        }
        throw new ErrorCodeException(result.code.getValue(), result.message);
    }

    private static final Unit W(Throwable th6) {
        if (!ExceptionKt.isInterruptedIOException(th6)) {
            LogWrapper.info("default", f250793d.getTag(), "doOnError report", new Object[0]);
            com.dragon.read.util.b1.a(th6);
        } else {
            LogWrapper.info("default", f250793d.getTag(), "doOnError ignore", new Object[0]);
        }
        return Unit.INSTANCE;
    }

    private static final void F(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void I(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void R(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void X(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final SingleSource D(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final ChapterInfo L(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (ChapterInfo) function1.invoke(p07);
    }

    private static final SingleSource M(m0 m0Var, ChapterInfo info) {
        Intrinsics.checkNotNullParameter(info, "info");
        return m0Var.A(info);
    }

    private static final SingleSource N(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final ChapterInfo P(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (ChapterInfo) function1.invoke(p07);
    }

    private static final ObservableSource V(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (ObservableSource) function1.invoke(p07);
    }

    private static final SingleSource c0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final SingleSource d0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final SingleSource e0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final ChapterInfo K(long j17, ChapterInfo info) {
        Intrinsics.checkNotNullParameter(info, "info");
        LogWrapper.i("default", "[ReaderSDKBiz] 加载网络章节数据成功: %s, 耗时%dms.", new Object[]{info, Long.valueOf(SystemClock.elapsedRealtime() - j17)});
        return info;
    }

    private static final ChapterInfo O(long j17, ChapterInfo chapterInfo) {
        Intrinsics.checkNotNullParameter(chapterInfo, "chapterInfo");
        LogWrapper.i("default", "[ReaderSDKBiz] 加载章节内容总体耗时: " + (SystemClock.elapsedRealtime() - j17), new Object[0]);
        return chapterInfo;
    }

    private final void g0(int i17, String str) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.putOpt("reader_type", Integer.valueOf(NsReaderServiceApi.IMPL.readerInitConfigService().n().getReaderType(str)));
            jSONObject.putOpt("status", Integer.valueOf(i17));
            MonitorUtils.monitorEvent("reader_chapter_info_loading_status", jSONObject, null, null);
        } catch (Exception unused) {
        }
    }

    private final Single<ItemContent> T(String str, String str2) {
        SystemClock.elapsedRealtime();
        FullRequest fullRequest = new FullRequest();
        fullRequest.itemId = str2;
        fullRequest.bookId = str;
        zm4.e eVar = NsReaderServiceApi.IMPL.readerChapterService();
        String userId = AcctManager.P().getUserId();
        Intrinsics.checkNotNullExpressionValue(userId, "getUserId(...)");
        fullRequest.keyRegisterTs = eVar.d(userId);
        Observable<R> observableCompose = ro4.f.k(fullRequest).compose(b8.k());
        final Function1 function1 = new Function1() { // from class: gn4.r
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return m0.U((FullResponse) obj);
            }
        };
        Observable observableFlatMap = observableCompose.flatMap(new Function() { // from class: gn4.s
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return m0.V(function1, obj);
            }
        });
        final Function1 function12 = new Function1() { // from class: gn4.t
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return m0.W((Throwable) obj);
            }
        };
        Single<ItemContent> singleFromObservable = Single.fromObservable(observableFlatMap.doOnError(new Consumer() { // from class: gn4.u
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                m0.X(function12, obj);
            }
        }));
        Intrinsics.checkNotNullExpressionValue(singleFromObservable, "fromObservable(...)");
        return singleFromObservable;
    }

    private static final Unit E(ChapterInfo chapterInfo, m0 m0Var, Throwable th6) {
        int iA = com.dragon.read.util.b1.a(th6);
        LogWrapper.error("default", f250793d.getTag(), "解密章节数据出错: " + chapterInfo + ", code = " + iA, new Object[0]);
        m0Var.f0(iA);
        return Unit.INSTANCE;
    }

    public final Single<ChapterInfo> Y(final String bookId, final String chapterId, final Function3<? super String, ? super Boolean, ? super ItemContent, Unit> function3) {
        Intrinsics.checkNotNullParameter(bookId, "bookId");
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        this.f250794a = 20002;
        Single<ItemContent> singleT = T(bookId, chapterId);
        final Function1 function1 = new Function1() { // from class: gn4.o
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return m0.Z(function3, chapterId, this, bookId, (ItemContent) obj);
            }
        };
        Single singleFlatMap = singleT.flatMap(new Function() { // from class: gn4.p
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return m0.e0(function1, obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleFlatMap, "flatMap(...)");
        return singleFlatMap;
    }

    private static final SingleSource B(final ChapterInfo chapterInfo, final long j17, final m0 m0Var) throws ErrorCodeException {
        if (!TextUtils.isEmpty(chapterInfo.content)) {
            if (chapterInfo.keyVersion == Integer.MIN_VALUE) {
                LogWrapper.i("default", "[ReaderSDKBiz] 解析章节内容耗时:" + (SystemClock.elapsedRealtime() - j17) + ", bookId:" + chapterInfo.bookId + ", chapterId:" + chapterInfo.chapterId, new Object[0]);
                return Single.just(chapterInfo);
            }
            Single<DecryptKey> singleB = NsReaderServiceApi.IMPL.readerChapterService().b(AcctManager.P().getUserId(), chapterInfo.keyVersion);
            final Function1 function1 = new Function1() { // from class: gn4.v
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return m0.C(chapterInfo, j17, m0Var, (DecryptKey) obj);
                }
            };
            Single<R> singleFlatMap = singleB.flatMap(new Function() { // from class: gn4.w
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj) {
                    return m0.D(function1, obj);
                }
            });
            final Function1 function12 = new Function1() { // from class: gn4.y
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return m0.E(chapterInfo, m0Var, (Throwable) obj);
                }
            };
            return singleFlatMap.doOnError(new Consumer() { // from class: gn4.z
                @Override // io.reactivex.functions.Consumer
                public final void accept(Object obj) {
                    m0.F(function12, obj);
                }
            });
        }
        throw new ErrorCodeException(-3, "no content to decode");
    }

    private static final SingleSource J(final m0 m0Var, String str, String str2, Function3 function3) {
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        Single<ChapterInfo> singleY = m0Var.Y(str, str2, function3);
        final Function1 function1 = new Function1() { // from class: gn4.j0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return m0.K(jElapsedRealtime, (ChapterInfo) obj);
            }
        };
        Single<R> map = singleY.map(new Function() { // from class: gn4.k0
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return m0.L(function1, obj);
            }
        });
        final Function1 function12 = new Function1() { // from class: gn4.l0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return m0.M(this.f250785a, (ChapterInfo) obj);
            }
        };
        return map.flatMap(new Function() { // from class: gn4.n
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return m0.N(function12, obj);
            }
        });
    }

    private static final SingleSource C(ChapterInfo chapterInfo, long j17, m0 m0Var, DecryptKey key) throws ErrorCodeException {
        boolean z17;
        Intrinsics.checkNotNullParameter(key, "key");
        zm4.e eVar = NsReaderServiceApi.IMPL.readerChapterService();
        String str = chapterInfo.content;
        if (chapterInfo.compressStatus > 0) {
            z17 = true;
        } else {
            z17 = false;
        }
        String strR = eVar.r(str, key, z17, chapterInfo.bookId, chapterInfo.chapterId);
        LogWrapper.i("default", "[ReaderSDKBiz] 解析章节内容耗时:" + (SystemClock.elapsedRealtime() - j17) + ", bookId:" + chapterInfo.bookId + ", chapterId:" + chapterInfo.chapterId, new Object[0]);
        if (!TextUtils.isEmpty(strR)) {
            m0Var.f0(0);
            chapterInfo.content = strR;
            return Single.just(chapterInfo);
        }
        m0Var.f0(-1);
        throw new ErrorCodeException(-5, "empty content after parsing");
    }

    public final synchronized Single<ChapterInfo> G(final ReaderClient readerClient, final String bookId, final String chapterId, final Function3<? super String, ? super Boolean, ? super ItemContent, Unit> function3) {
        Single<ChapterInfo> singleDoOnError;
        Intrinsics.checkNotNullParameter(bookId, "bookId");
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        Single singleDefer = SingleDelegate.defer(new Callable() { // from class: gn4.m
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return m0.J(this.f250787a, bookId, chapterId, function3);
            }
        });
        final Function1 function1 = new Function1() { // from class: gn4.x
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return m0.O(jElapsedRealtime, (ChapterInfo) obj);
            }
        };
        Single map = singleDefer.map(new Function() { // from class: gn4.e0
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return m0.P(function1, obj);
            }
        });
        final Function1 function12 = new Function1() { // from class: gn4.f0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return m0.Q(this.f250737a, bookId, readerClient, jElapsedRealtime, (ChapterInfo) obj);
            }
        };
        Single singleDoOnSuccess = map.doOnSuccess(new Consumer() { // from class: gn4.g0
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                m0.R(function12, obj);
            }
        });
        final Function1 function13 = new Function1() { // from class: gn4.h0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return m0.S(this.f250746a, bookId, chapterId, readerClient, jElapsedRealtime, (Throwable) obj);
            }
        };
        singleDoOnError = singleDoOnSuccess.doOnError(new Consumer() { // from class: gn4.i0
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                m0.I(function13, obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleDoOnError, "doOnError(...)");
        return singleDoOnError;
    }

    private static final SingleSource b0(Function3 function3, String str, ItemContent itemContent, ItemContent chapterResp) throws ErrorCodeException {
        Intrinsics.checkNotNullParameter(chapterResp, "chapterResp");
        short s17 = chapterResp.cryptStatus;
        if (s17 != 2) {
            if (s17 == 1) {
                ChapterInfo chapterInfoA = ChapterInfo.a((readersaas.com.dragon.read.saas.rpc.model.ItemContent) com.dragon.read.util.q.a(chapterResp, readersaas.com.dragon.read.saas.rpc.model.ItemContent.class), false);
                if (chapterInfoA != null) {
                    if (function3 != null) {
                        function3.invoke(str, Boolean.FALSE, itemContent);
                    }
                    Single singleJust = Single.just(chapterInfoA);
                    Intrinsics.checkNotNull(singleJust);
                    return singleJust;
                }
                LogWrapper.warn("default", f250793d.getTag(), "章节没有加密，但是content 却是空的", new Object[0]);
                throw new ErrorCodeException(-1, "chapter content is empty");
            }
            if (s17 == 0) {
                LogWrapper.warn("default", f250793d.getTag(), "章节已被加密", new Object[0]);
                if (!TextUtils.isEmpty(chapterResp.content)) {
                    if (function3 != null) {
                        function3.invoke(str, Boolean.FALSE, itemContent);
                    }
                    Single singleJust2 = Single.just(ChapterInfo.a((readersaas.com.dragon.read.saas.rpc.model.ItemContent) com.dragon.read.util.q.a(chapterResp, readersaas.com.dragon.read.saas.rpc.model.ItemContent.class), true));
                    Intrinsics.checkNotNull(singleJust2);
                    return singleJust2;
                }
                throw new ErrorCodeException(-3, "加密章节内容为空");
            }
            throw new ErrorCodeException(-4, "无效内容");
        }
        LogWrapper.warn("default", f250793d.getTag(), "已经注册密钥成功, 后台却返回密钥失效，为避免循环，终止请求", new Object[0]);
        throw new ErrorCodeException(-2, "invalid status");
    }

    private static final Unit Q(m0 m0Var, String str, ReaderClient readerClient, long j17, ChapterInfo chapterInfo) {
        se5.a readerMonitor;
        m0Var.g0(0, str);
        if (readerClient != null && (readerMonitor = readerClient.getReaderMonitor()) != null) {
            readerMonitor.b("ss_short_story_reader_chapter_content_load_duration", true, j17);
        }
        return Unit.INSTANCE;
    }

    private static final SingleSource Z(final Function3 function3, final String str, final m0 m0Var, final String str2, final ItemContent resp) throws ErrorCodeException {
        Intrinsics.checkNotNullParameter(resp, "resp");
        short s17 = resp.cryptStatus;
        if (s17 == 1) {
            ChapterInfo chapterInfoA = ChapterInfo.a((readersaas.com.dragon.read.saas.rpc.model.ItemContent) com.dragon.read.util.q.a(resp, readersaas.com.dragon.read.saas.rpc.model.ItemContent.class), false);
            if (chapterInfoA != null) {
                if (function3 != null) {
                    function3.invoke(str, Boolean.FALSE, resp);
                }
                Single singleJust = Single.just(chapterInfoA);
                Intrinsics.checkNotNull(singleJust);
                return singleJust;
            }
            LogWrapper.warn("default", f250793d.getTag(), "章节没有加密，但是content 却是空的", new Object[0]);
            throw new ErrorCodeException(-1, "chapter content is empty");
        }
        if (s17 == 2) {
            LogWrapper.warn("default", f250793d.getTag(), "章节密钥不存在或者密钥已过期，重新请求", new Object[0]);
            Single<DecryptKey> singleA = NsReaderServiceApi.IMPL.readerChapterService().a(AcctManager.P().getUserId());
            final Function1 function1 = new Function1() { // from class: gn4.a0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return m0.a0(this.f250686a, str2, str, function3, resp, (DecryptKey) obj);
                }
            };
            SingleSource singleSourceFlatMap = singleA.flatMap(new Function() { // from class: gn4.b0
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj) {
                    return m0.d0(function1, obj);
                }
            });
            Intrinsics.checkNotNull(singleSourceFlatMap);
            return singleSourceFlatMap;
        }
        if (s17 == 0) {
            LogWrapper.warn("default", f250793d.getTag(), "章节已被加密", new Object[0]);
            if (!TextUtils.isEmpty(resp.content)) {
                if (function3 != null) {
                    function3.invoke(str, Boolean.FALSE, resp);
                }
                Single singleJust2 = Single.just(ChapterInfo.a((readersaas.com.dragon.read.saas.rpc.model.ItemContent) com.dragon.read.util.q.a(resp, readersaas.com.dragon.read.saas.rpc.model.ItemContent.class), true));
                Intrinsics.checkNotNull(singleJust2);
                return singleJust2;
            }
            throw new ErrorCodeException(-3, "加密章节内容为空");
        }
        throw new ErrorCodeException(-4, "无效内容");
    }

    private static final Unit S(m0 m0Var, String str, String str2, ReaderClient readerClient, long j17, Throwable th6) {
        se5.a readerMonitor;
        int iA = com.dragon.read.util.b1.a(th6);
        if (iA == 100000000) {
            iA = m0Var.f250794a;
        }
        LogWrapper.error("default", f250793d.getTag(), "加载章节数据出错: bookId = %s, chapterId = %s, error = %s", new Object[]{str, str2, Log.getStackTraceString(th6)});
        m0Var.g0(iA, str);
        if (readerClient != null && (readerMonitor = readerClient.getReaderMonitor()) != null) {
            readerMonitor.b("ss_short_story_reader_chapter_content_load_duration", false, j17);
        }
        return Unit.INSTANCE;
    }

    private static final SingleSource a0(m0 m0Var, String str, final String str2, final Function3 function3, final ItemContent itemContent, DecryptKey s17) {
        Intrinsics.checkNotNullParameter(s17, "s");
        LogWrapper.warn("default", f250793d.getTag(), "注册密钥成功, s = %s，重新请求章节", new Object[]{s17});
        Single<ItemContent> singleT = m0Var.T(str, str2);
        final Function1 function1 = new Function1() { // from class: gn4.c0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return m0.b0(function3, str2, itemContent, (ItemContent) obj);
            }
        };
        return singleT.flatMap(new Function() { // from class: gn4.d0
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return m0.c0(function1, obj);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ Single H(m0 m0Var, ReaderClient readerClient, String str, String str2, Function3 function3, int i17, Object obj) {
        if ((i17 & 8) != 0) {
            function3 = null;
        }
        return m0Var.G(readerClient, str, str2, function3);
    }
}