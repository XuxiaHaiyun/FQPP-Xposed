package com.dragon.community.impl.reader;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.bytedance.covode.number.Covode;
import com.dragon.community.api.CSSParagraphCommentApi;
import com.dragon.community.api.model.JumpOpenReaderParaParams;
import com.dragon.community.saas.utils.LogHelper;
import com.dragon.read.R;
import com.ss.android.messagebus.BusProvider;
import com.ss.android.messagebus.Subscriber;
import com.ss.android.ugc.bytex.taskmonitor.proxy.CompletableDelegate;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.BooleanCompanionObject;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$BooleanRef;
import kotlin.jvm.internal.Ref$ObjectRef;
import s94.g;
import s94.s;

/* JADX INFO: loaded from: classes24.dex */
public final class o0 implements dr1.m {

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final int f95555q;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final s94.y f95556a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final s94.z f95557b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final s94.x f95558c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final LogHelper f95559d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private s94.s f95560e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private String f95561f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final Set<String> f95562g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private final Map<String, a> f95563h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private Disposable f95564i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private boolean f95565j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private final ConcurrentHashMap<String, Disposable> f95566k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private dr1.h f95567l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private final ConcurrentHashMap<String, CountDownLatch> f95568m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private final List<zt1.a> f95569n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private final Set<as1.b> f95570o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private s94.u f95571p;

    static {
        Covode.recordClassIndex(562225);
        f95555q = 8;
    }

    @Override // dr1.m
    public void j() {
    }

    public static final class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final boolean f95572a;

        static {
            Covode.recordClassIndex(562226);
        }

        public final boolean a() {
            return this.f95572a;
        }

        public a(boolean z17) {
            this.f95572a = z17;
        }
    }

    @Override // dr1.m
    public s94.y c() {
        return this.f95556a;
    }

    @Override // dr1.m
    public s94.x f() {
        return this.f95558c;
    }

    @Override // dr1.m
    public s94.z g() {
        return this.f95557b;
    }

    private static final Boolean W() {
        BooleanCompanionObject booleanCompanionObject = BooleanCompanionObject.INSTANCE;
        return Boolean.TRUE;
    }

    private final boolean O() {
        return !this.f95556a.j();
    }

    private final void h0() {
        if (this.f95571p == null) {
            b bVar = new b();
            s94.s sVar = this.f95560e;
            if (sVar == null) {
                Intrinsics.throwUninitializedPropertyAccessException("client");
                sVar = null;
            }
            this.f95571p = sVar.b(bVar);
        }
    }

    private final void p0() {
        s94.u uVar = this.f95571p;
        if (uVar != null) {
            s94.s sVar = this.f95560e;
            if (sVar == null) {
                Intrinsics.throwUninitializedPropertyAccessException("client");
                sVar = null;
            }
            sVar.h(uVar);
        }
    }

    @Override // dr1.m
    public void onReaderDestroy() {
        Disposable disposable = this.f95564i;
        if (disposable != null) {
            disposable.dispose();
        }
        BusProvider.unregister(this);
        J();
        p0();
    }

    private final void J() {
        this.f95559d.f("destroyParaHelper", new Object[0]);
        dr1.h hVar = this.f95567l;
        if (hVar != null) {
            hVar.onReaderDestroy();
        }
        CSSParagraphCommentApi cSSParagraphCommentApi = CSSParagraphCommentApi.IMPL;
        s94.s sVar = this.f95560e;
        if (sVar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("client");
            sVar = null;
        }
        cSSParagraphCommentApi.destroyReaderService(sVar.getContext());
        this.f95569n.remove(this.f95567l);
        this.f95567l = null;
    }

    private final boolean N() {
        s94.s sVar = this.f95560e;
        if (sVar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("client");
            sVar = null;
        }
        Activity activity = com.dragon.community.saas.utils.h.getActivity(sVar.getContext());
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return true;
        }
        return false;
    }

    private final boolean P() {
        Intent intent;
        s94.s sVar = this.f95560e;
        JumpOpenReaderParaParams jumpOpenReaderParaParams = null;
        if (sVar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("client");
            sVar = null;
        }
        Activity activity = com.dragon.community.saas.utils.h.getActivity(sVar.getContext());
        if (activity == null || (intent = activity.getIntent()) == null) {
            return false;
        }
        Serializable serializableExtra = intent.getSerializableExtra("openParaCommentDialogParams");
        if (serializableExtra instanceof JumpOpenReaderParaParams) {
            jumpOpenReaderParaParams = (JumpOpenReaderParaParams) serializableExtra;
        }
        if (jumpOpenReaderParaParams == null) {
            return false;
        }
        return true;
    }

    @Override // dr1.m
    public void a() {
        String chapterId;
        dr1.h hVar = this.f95567l;
        if (hVar != null) {
            hVar.a();
        }
        s94.s sVar = this.f95560e;
        String str = null;
        if (sVar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("client");
            sVar = null;
        }
        s94.l lVarD = sVar.g().d();
        if (lVarD == null || (chapterId = lVarD.getChapterId()) == null) {
            chapterId = "";
        }
        r0 r0Var = r0.f95584a;
        String str2 = this.f95561f;
        if (str2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("bookId");
        } else {
            str = str2;
        }
        r0Var.q(str, chapterId, "quit_reader");
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x002a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final void I() {
        boolean z17;
        String str = null;
        if (P()) {
            r0 r0Var = r0.f95584a;
            String str2 = this.f95561f;
            if (str2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("bookId");
                str2 = null;
            }
            if (r0Var.d(str2)) {
                String str3 = this.f95561f;
                if (str3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("bookId");
                    str3 = null;
                }
                if (!r0Var.i(str3)) {
                    z17 = true;
                }
            } else {
                z17 = false;
            }
        }
        if (z17) {
            r0 r0Var2 = r0.f95584a;
            String str4 = this.f95561f;
            if (str4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("bookId");
            } else {
                str = str4;
            }
            r0Var2.t(1, str);
            com.dragon.read.lib.community.inner.c.f173369a.b().b().a().c().showToast(com.dragon.read.lib.community.inner.d.d(R.string.acp));
        }
    }

    private final void K() {
        boolean z17;
        if (this.f95565j) {
            return;
        }
        if (N()) {
            this.f95559d.f("initHelperIfNeed, activity处于销毁状态，不初始化", new Object[0]);
            return;
        }
        I();
        String str = null;
        M(this, false, 1, null);
        LogHelper logHelper = this.f95559d;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("paragraphCommentService:");
        if (this.f95567l == null) {
            z17 = true;
        } else {
            z17 = false;
        }
        sb6.append(z17);
        sb6.append(" bookId:");
        String str2 = this.f95561f;
        if (str2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("bookId");
        } else {
            str = str2;
        }
        sb6.append(str);
        logHelper.f(sb6.toString(), new Object[0]);
        this.f95565j = true;
    }

    public static final class b implements s94.g {
        b() {
        }

        @Override // s94.g
        public void a(int i17) {
            int iA = o0.this.f95556a.a();
            Iterator it6 = o0.this.f95570o.iterator();
            while (it6.hasNext()) {
                ((as1.b) it6.next()).onThemeUpdate(iA);
            }
        }

        @Override // s94.g
        public void c(int i17, int i18) {
            g.a.a(this, i17, i18);
        }
    }

    @Override // dr1.m
    public void b(s94.l pageData) {
        Intrinsics.checkNotNullParameter(pageData, "pageData");
        dr1.h hVar = this.f95567l;
        if (hVar != null) {
            hVar.b(pageData);
        }
    }

    @Override // dr1.m
    public void d(as1.b listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.f95570o.remove(listener);
    }

    @Override // dr1.m
    public void h(as1.b listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.f95570o.add(listener);
    }

    @Subscriber
    public final void onChapterReload(vr1.i event) {
        Intrinsics.checkNotNullParameter(event, "event");
        S(event.a());
    }

    private final Completable q0(final String str) {
        Completable completableSubscribeOn = CompletableDelegate.create(new CompletableOnSubscribe() { // from class: com.dragon.community.impl.reader.f0
            @Override // io.reactivex.CompletableOnSubscribe
            public final void subscribe(CompletableEmitter completableEmitter) throws InterruptedException {
                o0.r0(this.f95507a, str, completableEmitter);
            }
        }).subscribeOn(Schedulers.io());
        Intrinsics.checkNotNullExpressionValue(completableSubscribeOn, "subscribeOn(...)");
        return completableSubscribeOn;
    }

    private final boolean R(String str) {
        if (this.f95569n.isEmpty()) {
            return true;
        }
        Iterator<zt1.a> it6 = this.f95569n.iterator();
        while (it6.hasNext()) {
            if (it6.next().d(str)) {
                return true;
            }
        }
        return false;
    }

    private final void e0(final String str) {
        Intrinsics.checkNotNullExpressionValue(q0(str).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() { // from class: com.dragon.community.impl.reader.d0
            @Override // io.reactivex.functions.Action
            public final void run() {
                o0.f0(this.f95438a, str);
            }
        }), "subscribe(...)");
    }

    private static final Boolean t0(Object[] it6) {
        Intrinsics.checkNotNullParameter(it6, "it");
        boolean z17 = false;
        for (Object obj : it6) {
            if (obj instanceof Boolean) {
                if (!z17 && !((Boolean) obj).booleanValue()) {
                    z17 = false;
                } else {
                    z17 = true;
                }
            }
        }
        return Boolean.valueOf(z17);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0018  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final void L(boolean z17) {
        boolean z18;
        s94.s sVar = null;
        if (!z17) {
            r0 r0Var = r0.f95584a;
            String str = this.f95561f;
            if (str == null) {
                Intrinsics.throwUninitializedPropertyAccessException("bookId");
                str = null;
            }
            if (!r0Var.d(str)) {
                z18 = false;
            } else {
                z18 = true;
            }
        }
        if (z18 && this.f95567l == null) {
            try {
                CSSParagraphCommentApi cSSParagraphCommentApi = CSSParagraphCommentApi.IMPL;
                s94.s sVar2 = this.f95560e;
                if (sVar2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("client");
                } else {
                    sVar = sVar2;
                }
                dr1.h hVarNewReaderService = cSSParagraphCommentApi.newReaderService(sVar, this.f95556a, this.f95557b, this.f95558c);
                this.f95567l = hVarNewReaderService;
                this.f95569n.add(hVarNewReaderService);
                return;
            } catch (Exception unused) {
                throw new RuntimeException("段评模块配置为开，检查下是不是没有引入段评module");
            }
        }
        if (!z18 && this.f95567l != null) {
            J();
        }
    }

    private final void i0(final String str) {
        Disposable disposable = this.f95566k.get(str);
        if (disposable != null) {
            disposable.dispose();
            this.f95566k.remove(str);
            this.f95559d.f("[reloadChapterData]已有重排版任务在进行，关闭上一个任务chapterId = " + str, new Object[0]);
        }
        s94.s sVar = this.f95560e;
        if (sVar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("client");
            sVar = null;
        }
        final s94.n nVarG = sVar.g();
        Observable<s94.l> observableDoFinally = nVarG.c(str, true, "Community-Reload").doFinally(new Action() { // from class: com.dragon.community.impl.reader.g0
            @Override // io.reactivex.functions.Action
            public final void run() {
                o0.j0(this.f95520a, str);
            }
        });
        final Function1 function1 = new Function1() { // from class: com.dragon.community.impl.reader.h0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return o0.k0(nVarG, this, str, (s94.l) obj);
            }
        };
        Consumer<? super s94.l> consumer = new Consumer() { // from class: com.dragon.community.impl.reader.i0
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                o0.m0(function1, obj);
            }
        };
        final Function1 function12 = new Function1() { // from class: com.dragon.community.impl.reader.j0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return o0.n0(this.f95538a, str, (Throwable) obj);
            }
        };
        this.f95566k.put(str, observableDoFinally.subscribe(consumer, new Consumer() { // from class: com.dragon.community.impl.reader.k0
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                o0.o0(function12, obj);
            }
        }));
    }

    @Override // dr1.m
    public void i(s94.s client) {
        Intrinsics.checkNotNullParameter(client, "client");
        boolean zD = false;
        this.f95559d.f("onClientReady", new Object[0]);
        this.f95560e = client;
        this.f95561f = client.m().getBookId();
        p02.q.f296439a.n();
        f1 f1Var = f1.f95509a;
        String str = this.f95561f;
        String str2 = null;
        if (str == null) {
            Intrinsics.throwUninitializedPropertyAccessException("bookId");
            str = null;
        }
        f1Var.G(str);
        if (com.dragon.read.lib.community.inner.c.f173369a.a().c().b()) {
            g1 g1Var = g1.f95522a;
            String str3 = this.f95561f;
            if (str3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("bookId");
                str3 = null;
            }
            if (!g1Var.j(str3)) {
                zD = true;
            } else {
                r0 r0Var = r0.f95584a;
                String str4 = this.f95561f;
                if (str4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("bookId");
                } else {
                    str2 = str4;
                }
                zD = r0Var.d(str2);
            }
        }
        if (zD) {
            L(true);
        }
        h0();
    }

    private final void S(final String str) {
        boolean zA;
        boolean z17;
        s94.z zVar = this.f95557b;
        if (zVar != null) {
            zA = zVar.a("onChapterDataLoaded", new Runnable() { // from class: com.dragon.community.impl.reader.t
                @Override // java.lang.Runnable
                public final void run() {
                    o0.T(this.f95591a, str);
                }
            });
        } else {
            zA = false;
        }
        if (zA) {
            return;
        }
        s94.s sVar = this.f95560e;
        s94.s sVar2 = null;
        if (sVar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("client");
            sVar = null;
        }
        List<s94.l> listD = sVar.d(str);
        if (listD != null && !listD.isEmpty()) {
            z17 = false;
        } else {
            z17 = true;
        }
        if (!z17) {
            this.f95559d.f("章节数据加载成功，即将触发重排版, chapterId = " + str + '.', new Object[0]);
            i0(str);
            return;
        }
        this.f95559d.f("章节数据加载成功， 章节还没有数据，等待数据加载完成，chapterId = " + str + '.', new Object[0]);
        s94.s sVar3 = this.f95560e;
        if (sVar3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("client");
        } else {
            sVar2 = sVar3;
        }
        sVar2.i(true, new Function0() { // from class: com.dragon.community.impl.reader.e0
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return o0.U(this.f95444a, str);
            }
        });
    }

    private static final void T(o0 o0Var, String str) {
        o0Var.S(str);
    }

    private static final void b0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void d0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void f0(o0 o0Var, String str) {
        o0Var.S(str);
    }

    private static final void m0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void o0(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final void j0(o0 o0Var, String str) {
        o0Var.f95566k.remove(str);
    }

    private static final void V(o0 o0Var, CompletableObserver it6) {
        Intrinsics.checkNotNullParameter(it6, "it");
        o0Var.K();
        it6.onComplete();
    }

    private static final SingleSource Y(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (SingleSource) function1.invoke(p07);
    }

    private static final Boolean u0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (Boolean) function1.invoke(p07);
    }

    private final boolean Q(String str, boolean z17) {
        if (z17) {
            return false;
        }
        r0 r0Var = r0.f95584a;
        String str2 = this.f95561f;
        if (str2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("bookId");
            str2 = null;
        }
        if (r0Var.j(str2)) {
            return false;
        }
        this.f95559d.f("命中社区请求优化，当前开关为关，不请求章段评，chapterId=" + str, new Object[0]);
        return true;
    }

    private static final Unit U(o0 o0Var, String str) {
        o0Var.f95559d.f("章节数据加载已完成，即将触发重排版逻辑， chapterId = " + str + '.', new Object[0]);
        o0Var.i0(str);
        return Unit.INSTANCE;
    }

    private final Single<Boolean> s0(String str, boolean z17) {
        Single<Boolean> singleJust;
        if (z17) {
            singleJust = Single.just(Boolean.FALSE);
            Intrinsics.checkNotNull(singleJust);
        } else {
            if (this.f95567l == null) {
                this.f95559d.f("paragraphCommentService is null chapterId:" + str, new Object[0]);
            }
            dr1.h hVar = this.f95567l;
            if (hVar == null || (singleJust = hVar.w(str, this.f95556a.b(str))) == null) {
                singleJust = Single.just(Boolean.FALSE);
                Intrinsics.checkNotNullExpressionValue(singleJust, "just(...)");
            }
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(singleJust);
        final Function1 function1 = new Function1() { // from class: com.dragon.community.impl.reader.b0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return o0.t0((Object[]) obj);
            }
        };
        Single<Boolean> singleZip = Single.zip(arrayList, new Function() { // from class: com.dragon.community.impl.reader.c0
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return o0.u0(function1, obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(singleZip, "zip(...)");
        return singleZip;
    }

    @Override // dr1.m
    public void e(String chapterId, boolean z17) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        this.f95559d.f("notifyOtherChapterDataLoad，chapterId = " + chapterId + ", isHandling = " + z17, new Object[0]);
        if (z17) {
            this.f95568m.put(chapterId, new CountDownLatch(1));
            return;
        }
        CountDownLatch countDownLatchRemove = this.f95568m.remove(chapterId);
        if (countDownLatchRemove != null) {
            countDownLatchRemove.countDown();
        }
    }

    private static final void r0(o0 o0Var, String str, CompletableEmitter emitter) throws InterruptedException {
        Intrinsics.checkNotNullParameter(emitter, "emitter");
        CountDownLatch countDownLatch = o0Var.f95568m.get(str);
        if (countDownLatch != null) {
            countDownLatch.await();
        }
        emitter.onComplete();
    }

    private static final Unit n0(o0 o0Var, String str, Throwable th6) {
        o0Var.f95559d.f("重新加载章节数据出错或取消, chapterId = " + str + ": " + Log.getStackTraceString(th6) + '.', new Object[0]);
        return Unit.INSTANCE;
    }

    public o0(s94.y readerDependency, s94.z zVar, s94.x xVar) {
        Intrinsics.checkNotNullParameter(readerDependency, "readerDependency");
        this.f95556a = readerDependency;
        this.f95557b = zVar;
        this.f95558c = xVar;
        this.f95559d = ct1.c.e(null);
        this.f95562g = Collections.synchronizedSet(new HashSet());
        this.f95563h = Collections.synchronizedMap(new HashMap());
        this.f95566k = new ConcurrentHashMap<>();
        this.f95568m = new ConcurrentHashMap<>();
        this.f95569n = Collections.synchronizedList(new ArrayList());
        this.f95570o = new LinkedHashSet();
        BusProvider.register(this);
    }

    @Override // dr1.m
    public void k(final String chapterId, final boolean z17, final boolean z18) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        if (this.f95560e == null) {
            return;
        }
        if (!O()) {
            this.f95559d.f("无社区的书", new Object[0]);
            return;
        }
        if (this.f95562g.contains(chapterId)) {
            this.f95559d.f("章节 " + chapterId + " 的数据已经在加载中.", new Object[0]);
            Map<String, a> pendingLoadingInfo = this.f95563h;
            Intrinsics.checkNotNullExpressionValue(pendingLoadingInfo, "pendingLoadingInfo");
            pendingLoadingInfo.put(chapterId, new a(z17));
            return;
        }
        this.f95562g.add(chapterId);
        this.f95559d.f("开始加载章节数据, chapterId = " + chapterId, new Object[0]);
        if (!R(chapterId) && !z18) {
            this.f95559d.f("已经有章节数据, chapterId= " + chapterId, new Object[0]);
            return;
        }
        final Ref$BooleanRef ref$BooleanRef = new Ref$BooleanRef();
        f1 f1Var = f1.f95509a;
        String str = this.f95561f;
        if (str == null) {
            Intrinsics.throwUninitializedPropertyAccessException("bookId");
            str = null;
        }
        Single single = f1Var.X(str).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).andThen(new CompletableSource() { // from class: com.dragon.community.impl.reader.l0
            @Override // io.reactivex.CompletableSource
            public final void subscribe(CompletableObserver completableObserver) {
                o0.V(this.f95547a, completableObserver);
            }
        }).observeOn(Schedulers.io()).toSingle(new Callable() { // from class: com.dragon.community.impl.reader.m0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return o0.W();
            }
        });
        final Function1 function1 = new Function1() { // from class: com.dragon.community.impl.reader.n0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return o0.X(ref$BooleanRef, this, chapterId, z17, (Boolean) obj);
            }
        };
        Single singleObserveOn = single.flatMap(new Function() { // from class: com.dragon.community.impl.reader.u
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return o0.Y(function1, obj);
            }
        }).doFinally(new Action() { // from class: com.dragon.community.impl.reader.v
            @Override // io.reactivex.functions.Action
            public final void run() {
                o0.Z(this.f95595a, chapterId, ref$BooleanRef, z18);
            }
        }).observeOn(AndroidSchedulers.mainThread());
        final Function1 function12 = new Function1() { // from class: com.dragon.community.impl.reader.w
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return o0.a0(this.f95600a, chapterId, z18, (Boolean) obj);
            }
        };
        Consumer consumer = new Consumer() { // from class: com.dragon.community.impl.reader.x
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                o0.b0(function12, obj);
            }
        };
        final Function1 function13 = new Function1() { // from class: com.dragon.community.impl.reader.y
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return o0.c0(this.f95605a, chapterId, z18, (Throwable) obj);
            }
        };
        this.f95564i = singleObserveOn.subscribe(consumer, new Consumer() { // from class: com.dragon.community.impl.reader.z
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                o0.d0(function13, obj);
            }
        });
    }

    static /* synthetic */ void M(o0 o0Var, boolean z17, int i17, Object obj) {
        if ((i17 & 1) != 0) {
            z17 = false;
        }
        o0Var.L(z17);
    }

    private static final Unit c0(o0 o0Var, String str, boolean z17, Throwable th6) {
        o0Var.f95559d.d("请求数据失败, chapterId = " + str + ": " + Log.getStackTraceString(th6), new Object[0]);
        if (z17) {
            o0Var.e0(str);
        }
        return Unit.INSTANCE;
    }

    /* JADX WARN: Type inference failed for: r3v3, types: [T, s94.u] */
    private static final Unit k0(final s94.n nVar, final o0 o0Var, final String str, s94.l lVar) {
        s94.s sVar = null;
        if (nVar.m()) {
            final Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
            s94.s sVar2 = o0Var.f95560e;
            if (sVar2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("client");
                sVar2 = null;
            }
            ref$ObjectRef.element = s.a.a(sVar2, false, new Function0() { // from class: com.dragon.community.impl.reader.a0
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return o0.l0(nVar, o0Var, ref$ObjectRef, str);
                }
            }, 1, null);
        } else {
            s94.s sVar3 = o0Var.f95560e;
            if (sVar3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("client");
            } else {
                sVar = sVar3;
            }
            Intrinsics.checkNotNull(lVar);
            o0Var.g0(sVar, nVar, lVar, str);
        }
        return Unit.INSTANCE;
    }

    private static final Unit l0(s94.n nVar, o0 o0Var, Ref$ObjectRef ref$ObjectRef, String str) {
        s94.l lVarD = nVar.d();
        if (lVarD == null) {
            o0Var.f95559d.f("current pageData is null, 不刷新社区数据", new Object[0]);
            return Unit.INSTANCE;
        }
        s94.u uVar = (s94.u) ref$ObjectRef.element;
        s94.s sVar = null;
        if (uVar != null) {
            s94.s sVar2 = o0Var.f95560e;
            if (sVar2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("client");
                sVar2 = null;
            }
            sVar2.h(uVar);
        }
        s94.s sVar3 = o0Var.f95560e;
        if (sVar3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("client");
        } else {
            sVar = sVar3;
        }
        o0Var.g0(sVar, nVar, lVarD, str);
        return Unit.INSTANCE;
    }

    private static final void Z(o0 o0Var, String str, Ref$BooleanRef ref$BooleanRef, boolean z17) {
        o0Var.f95562g.remove(str);
        a aVarRemove = o0Var.f95563h.remove(str);
        if (aVarRemove != null) {
            boolean zQ = o0Var.Q(str, aVarRemove.a());
            o0Var.f95559d.f("检查请求条件, chapterId = " + str + ", isOptmize = " + zQ, new Object[0]);
            if (zQ != ref$BooleanRef.element) {
                o0Var.f95559d.f("检查到请求条件与之前不同，需要重新触发下请求", new Object[0]);
                o0Var.k(str, aVarRemove.a(), z17);
            }
        }
    }

    private static final Unit a0(o0 o0Var, String str, boolean z17, Boolean bool) {
        o0Var.f95559d.f("请求数据处理回调: chapterId = " + str + ", isSuccess = " + bool, new Object[0]);
        if (o0Var.N()) {
            o0Var.f95559d.f("activity处于销毁状态，不处理数据", new Object[0]);
            return Unit.INSTANCE;
        }
        if (bool.booleanValue()) {
            if (z17) {
                o0Var.e0(str);
            } else {
                o0Var.S(str);
            }
        } else if (z17) {
            o0Var.e0(str);
        }
        return Unit.INSTANCE;
    }

    private final void g0(s94.s sVar, s94.n nVar, s94.l lVar, String str) {
        boolean z17;
        String name;
        boolean z18;
        boolean z19;
        String name2;
        String name3;
        boolean z27;
        boolean z28;
        if (this.f95556a.i(sVar, lVar, str)) {
            return;
        }
        s94.l lVarB = nVar.b(lVar);
        s94.l lVarJ = nVar.j(lVar);
        this.f95559d.f("尝试刷新页面，chapterId: " + str + ", current chapterId is " + lVar.getChapterId() + ", pageIndex is " + lVar.getIndex(), new Object[0]);
        s94.l lVarG = nVar.g();
        String name4 = "null";
        if (lVarG instanceof s94.o) {
            s94.o oVar = (s94.o) lVarG;
            s94.l next = oVar.getNext();
            if (next == null) {
                name2 = "null";
            } else {
                name2 = next.getClass().getName();
            }
            if (lVarJ == null) {
                name3 = "null";
            } else {
                name3 = lVarJ.getClass().getName();
            }
            this.f95559d.f("下一页是插入页面, attachNext is " + lVarG + ", attachNextNext is " + name2 + ", dataNext is " + name3, new Object[0]);
            if (oVar.getNext() != null) {
                if (lVarJ != null && lVarJ.isOriginalPage()) {
                    z27 = true;
                } else {
                    z27 = false;
                }
                if (z27) {
                    while (true) {
                        z28 = next instanceof s94.o;
                        if (!z28) {
                            break;
                        }
                        s94.o oVar2 = (s94.o) next;
                        if (!(oVar2.getNext() instanceof s94.o)) {
                            break;
                        } else {
                            next = oVar2.getNext();
                        }
                    }
                    if (Intrinsics.areEqual(next, oVar.getNext())) {
                        oVar.a(lVarJ);
                    } else if (z28) {
                        s94.o oVar3 = (s94.o) next;
                        if (oVar3.getNext() != null) {
                            oVar3.a(lVarJ);
                        }
                    }
                }
            }
            if (lVarJ instanceof s94.e) {
                this.f95559d.f("页面绑定的下一页是插入页，数据上的下一页是评论页，下一页需要更新为评论页", new Object[0]);
            } else {
                lVarJ = lVarG;
            }
        }
        s94.l lVarE = nVar.e();
        if (lVarE instanceof s94.o) {
            s94.o oVar4 = (s94.o) lVarE;
            s94.l previous = oVar4.getPrevious();
            if (previous == null) {
                name = "null";
            } else {
                name = previous.getClass().getName();
            }
            if (lVarB != null) {
                name4 = lVarB.getClass().getName();
            }
            this.f95559d.f("上一页是插入页面, attachPrevious is " + lVarE + ", attachPreviousPrevious is " + name + ", dataPrevious is " + name4, new Object[0]);
            if (oVar4.getPrevious() != null) {
                if (lVarB != null && lVarB.isOriginalPage()) {
                    z18 = true;
                } else {
                    z18 = false;
                }
                if (z18) {
                    oVar4.c(lVarB);
                    while (true) {
                        z19 = previous instanceof s94.o;
                        if (!z19) {
                            break;
                        }
                        s94.o oVar5 = (s94.o) previous;
                        if (!(oVar5.getPrevious() instanceof s94.o)) {
                            break;
                        } else {
                            previous = oVar5.getPrevious();
                        }
                    }
                    if (Intrinsics.areEqual(previous, oVar4.getPrevious())) {
                        oVar4.c(lVarB);
                    } else if (z19) {
                        s94.o oVar6 = (s94.o) previous;
                        if (oVar6.getPrevious() != null) {
                            oVar6.c(lVarB);
                        }
                    }
                }
            }
            lVarB = lVarE;
        }
        if (lVarJ == null) {
            lVarJ = nVar.g();
        }
        if (lVarB == null) {
            lVarB = nVar.e();
        }
        if (TextUtils.equals(str, lVar.getChapterId())) {
            this.f95559d.f("刷新当前页面。chapterId = %s. , pageIndex is %s", str, Integer.valueOf(lVar.getIndex()));
            nVar.l(lVar);
            nVar.h(lVarB);
            nVar.k(lVarJ);
            return;
        }
        if (lVarB != null && !Intrinsics.areEqual(lVarB, lVarE) && TextUtils.equals(sVar.f().b(lVar.getChapterId()), str) && lVar.getIndex() == 0) {
            this.f95559d.f("刷新上一章的最后一页, chapterId = " + str + '.', new Object[0]);
            nVar.h(lVarB);
        }
        if (lVarJ != null && !Intrinsics.areEqual(lVarJ, lVarG)) {
            String strA = sVar.f().a(lVar.getChapterId());
            s94.s sVar2 = this.f95560e;
            s94.l lVar2 = null;
            if (sVar2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("client");
                sVar2 = null;
            }
            List<s94.l> listD = sVar2.d(lVar.getChapterId());
            if (listD != null && !listD.isEmpty()) {
                z17 = false;
            } else {
                z17 = true;
            }
            if (!z17) {
                int size = listD.size() - 1;
                if (size >= 0) {
                    while (true) {
                        int i17 = size - 1;
                        s94.l lVar3 = listD.get(size);
                        if (lVar3.isReady()) {
                            lVar2 = lVar3;
                            break;
                        } else if (i17 < 0) {
                            break;
                        } else {
                            size = i17;
                        }
                    }
                }
                if (lVar2 != null && TextUtils.equals(strA, str) && lVar.getIndex() == lVar2.getIndex()) {
                    this.f95559d.f("刷新下一章的第一页，chapterId = " + str, new Object[0]);
                    nVar.k(lVarJ);
                }
            }
        }
    }

    private static final SingleSource X(Ref$BooleanRef ref$BooleanRef, o0 o0Var, String str, boolean z17, Boolean it6) {
        Intrinsics.checkNotNullParameter(it6, "it");
        boolean zQ = o0Var.Q(str, z17);
        ref$BooleanRef.element = zQ;
        return o0Var.s0(str, zQ);
    }
}