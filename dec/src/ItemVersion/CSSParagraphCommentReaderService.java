package com.dragon.community.impl.reader;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import as1.f;
import com.bytedance.covode.number.Covode;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.dragon.community.api.CSSReaderApi;
import com.dragon.community.api.model.JumpOpenReaderParaParams;
import com.dragon.community.api.model.ParaIdeaDataModel;
import com.dragon.community.api.model.ParaIdeaDataSource;
import com.dragon.community.api.model.Type;
import com.dragon.community.api.model.VideoEntranceType;
import com.dragon.community.common.contentpublish.l;
import com.dragon.community.common.model.SaaSAiImageOpenParams;
import com.dragon.community.common.model.SaaSComment;
import com.dragon.community.common.model.SaaSReply;
import com.dragon.community.impl.list.content.s2;
import com.dragon.community.impl.model.ParagraphComment;
import com.dragon.community.impl.publish.d0;
import com.dragon.community.impl.reader.CSSParagraphCommentReaderService;
import com.dragon.community.impl.reader.entrance.ParaBubbleData;
import com.dragon.community.saas.utils.LogHelper;
import com.dragon.read.R;
import com.dragon.read.lib.community.config.model.CommentListDialogConfig;
import com.dragon.read.lib.community.config.model.ConfigItem;
import com.dragon.read.lib.community.config.model.ParaBubbleCacheTimeConfig;
import com.dragon.read.lib.community.depend.biz.ICSSParaTextBlock;
import com.dragon.read.lib.community.depend.biz.nps.ResearchSceneType;
import com.dragon.read.saas.ugc.model.GetIdeaListData;
import com.dragon.read.saas.ugc.model.GetIdeaListRequest;
import com.dragon.read.saas.ugc.model.GetIdeaListResponse;
import com.dragon.read.saas.ugc.model.ParaBubble;
import com.dragon.read.saas.ugc.model.ParaIdeaData;
import com.dragon.read.saas.ugc.model.UgcComment;
import com.dragon.read.saas.ugc.model.UgcCommentGroupTypeOutter;
import com.dragon.read.saas.ugc.model.UgcCommentSourceEnum;
import com.dragon.read.saas.ugc.rpc.CommentApiService;
import com.dragon.read.util.kotlin.CollectionKt;
import com.ss.android.messagebus.BusProvider;
import dr1.h;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt__CollectionsJVMKt;
import kotlin.collections.CollectionsKt__CollectionsKt;
import kotlin.collections.CollectionsKt___CollectionsKt;
import kotlin.comparisons.ComparisonsKt__ComparisonsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import m02.a;
import mt1.n;
import mt1.p;
import r94.u;
import s94.r;

/* JADX INFO: loaded from: classes24.dex */
public final class CSSParagraphCommentReaderService implements dr1.h {
    public static final int B;
    private s94.l A;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final s94.s f95385a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final s94.y f95386b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final s94.z f95387c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final s94.x f95388d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final LogHelper f95389e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final String f95390f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final Map<String, l.d> f95391g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private final Map<String, com.dragon.community.kmp.publish.model.g> f95392h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private final ConcurrentHashMap<String, Map<Integer, ParaIdeaDataModel>> f95393i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private final ConcurrentHashMap<String, Boolean> f95394j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private final ConcurrentHashMap<String, Boolean> f95395k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private String f95396l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private final n02.d f95397m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private m02.n f95398n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private m02.m f95399o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private ICSSParaTextBlock f95400p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private final ConcurrentHashMap<String, Boolean> f95401q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private final boolean f95402r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private final boolean f95403s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private final ParaBubbleCacheTimeConfig f95404t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private m02.a f95405u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private m02.k f95406v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private final d f95407w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private final b f95408x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private final a f95409y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private Job f95410z;

    static {
        Covode.recordClassIndex(562224);
        B = 8;
    }

    private final boolean h0(int i17) {
        return i17 != 10000;
    }

    private static final void x0() {
    }

    public final s94.s a0() {
        return this.f95385a;
    }

    public final s94.x b0() {
        return this.f95388d;
    }

    @Override // dr1.h
    public s94.s i() {
        return this.f95385a;
    }

    @Override // dr1.h
    public s94.x s() {
        return this.f95388d;
    }

    @Override // dr1.h
    public s94.y v() {
        return this.f95386b;
    }

    public static final class a implements n.a {
        @Override // mt1.n.a
        public int getPriority() {
            return -1;
        }

        @Override // mt1.p
        public List<UgcCommentGroupTypeOutter> getInterestServiceIds() {
            return CollectionsKt__CollectionsJVMKt.listOf(UgcCommentGroupTypeOutter.Paragraph);
        }

        a() {
        }

        @Override // mt1.a
        public boolean v(b32.c cVar) {
            return n.a.C3680a.a(this, cVar);
        }

        @Override // mt1.a
        public boolean B(b32.c predicateArgs) {
            Intrinsics.checkNotNullParameter(predicateArgs, "predicateArgs");
            return Intrinsics.areEqual(CSSParagraphCommentReaderService.this.f95390f, predicateArgs.d("book_id", ""));
        }

        @Override // mt1.p
        public void J(String str, long j17) {
            n.a.C3680a.h(this, str, j17);
        }

        @Override // mt1.p
        public void T(mt1.o oVar, String str) {
            n.a.C3680a.e(this, oVar, str);
        }

        @Override // mt1.p
        public boolean e(mt1.o syncParams, SaaSComment comment) {
            ParagraphComment paragraphComment;
            String paraSrcContent;
            Intrinsics.checkNotNullParameter(syncParams, "syncParams");
            Intrinsics.checkNotNullParameter(comment, "comment");
            if (ct1.k.c(comment)) {
                ICSSParaTextBlock iCSSParaTextBlock = CSSParagraphCommentReaderService.this.f95400p;
                if (iCSSParaTextBlock != null) {
                    CSSParagraphCommentReaderService cSSParagraphCommentReaderService = CSSParagraphCommentReaderService.this;
                    if (Intrinsics.areEqual(iCSSParaTextBlock.getBookId(), comment.getBookId()) && Intrinsics.areEqual(iCSSParaTextBlock.getChapterId(), comment.getGroupId()) && n32.g.a(iCSSParaTextBlock.getSelectedText())) {
                        String selectedText = iCSSParaTextBlock.getSelectedText();
                        if (comment instanceof ParagraphComment) {
                            paragraphComment = (ParagraphComment) comment;
                        } else {
                            paragraphComment = null;
                        }
                        if (paragraphComment != null) {
                            paraSrcContent = paragraphComment.getParaSrcContent();
                        } else {
                            paraSrcContent = null;
                        }
                        if (Intrinsics.areEqual(selectedText, paraSrcContent) && com.dragon.community.impl.d.f93177a.a().a().v()) {
                            cSSParagraphCommentReaderService.r0(comment, iCSSParaTextBlock, "paragraph_popup_publish");
                        }
                    }
                    cSSParagraphCommentReaderService.f95400p = null;
                }
                s94.x xVarB0 = CSSParagraphCommentReaderService.this.b0();
                if (xVarB0 != null) {
                    xVarB0.d(ResearchSceneType.AfterUseAIGenImage.getValue(), CSSParagraphCommentReaderService.this.a0());
                    return true;
                }
                return false;
            }
            return true;
        }

        @Override // mt1.p
        public void I(mt1.o oVar, String str, boolean z17) {
            n.a.C3680a.d(this, oVar, str, z17);
        }

        @Override // mt1.p
        public void V(mt1.o oVar, String str, boolean z17) {
            n.a.C3680a.c(this, oVar, str, z17);
        }

        @Override // mt1.p
        public void a(mt1.o oVar, String str, String str2) {
            n.a.C3680a.g(this, oVar, str, str2);
        }

        @Override // mt1.p
        public void i(mt1.o oVar, String str, SaaSReply saaSReply) {
            n.a.C3680a.f(this, oVar, str, saaSReply);
        }

        @Override // mt1.p
        public void G(mt1.o oVar, String str, boolean z17, SaaSComment saaSComment) {
            n.a.C3680a.b(this, oVar, str, z17, saaSComment);
        }
    }

    public static final class b implements mt1.p {
        @Override // mt1.p
        public List<UgcCommentGroupTypeOutter> getInterestServiceIds() {
            return CollectionsKt__CollectionsJVMKt.listOf(UgcCommentGroupTypeOutter.Paragraph);
        }

        b() {
        }

        @Override // mt1.a
        public boolean v(b32.c cVar) {
            return p.a.a(this, cVar);
        }

        @Override // mt1.a
        public boolean B(b32.c predicateArgs) {
            Intrinsics.checkNotNullParameter(predicateArgs, "predicateArgs");
            if (predicateArgs.c("is_para_comment_add_from_outside_position") != null) {
                return true;
            }
            return false;
        }

        @Override // mt1.p
        public void J(String str, long j17) {
            p.a.i(this, str, j17);
        }

        @Override // mt1.p
        public void T(mt1.o oVar, String str) {
            p.a.f(this, oVar, str);
        }

        @Override // mt1.p
        public boolean e(mt1.o syncParams, SaaSComment comment) {
            ParagraphComment paragraphComment;
            String groupId;
            Intrinsics.checkNotNullParameter(syncParams, "syncParams");
            Intrinsics.checkNotNullParameter(comment, "comment");
            p.a.b(this, syncParams, comment);
            if (comment instanceof ParagraphComment) {
                paragraphComment = (ParagraphComment) comment;
            } else {
                paragraphComment = null;
            }
            if (paragraphComment == null || (groupId = comment.getGroupId()) == null) {
                return false;
            }
            int paraId = paragraphComment.getParaId();
            Map linkedHashMap = (Map) CSSParagraphCommentReaderService.this.f95393i.get(groupId);
            if (linkedHashMap == null) {
                linkedHashMap = new LinkedHashMap();
            }
            ParaIdeaDataModel paraIdeaDataModel = (ParaIdeaDataModel) linkedHashMap.get(Integer.valueOf(paraId));
            if (paraIdeaDataModel != null) {
                paraIdeaDataModel.getParaIdeaData().userCount++;
                paraIdeaDataModel.getParaIdeaData().count++;
            } else {
                ParaIdeaData paraIdeaData = new ParaIdeaData();
                paraIdeaData.userCount++;
                paraIdeaData.count = 1;
                ParaIdeaDataModel paraIdeaDataModel2 = new ParaIdeaDataModel(paraIdeaData, ParaIdeaDataSource.SYNC);
                CSSParagraphCommentReaderService.this.u(groupId, paraId, paraIdeaDataModel2);
                linkedHashMap.put(Integer.valueOf(paraId), paraIdeaDataModel2);
                paraIdeaDataModel = paraIdeaDataModel2;
            }
            paraIdeaDataModel.updateType();
            return false;
        }

        @Override // mt1.p
        public void I(mt1.o oVar, String str, boolean z17) {
            p.a.e(this, oVar, str, z17);
        }

        @Override // mt1.p
        public void V(mt1.o oVar, String str, boolean z17) {
            p.a.d(this, oVar, str, z17);
        }

        @Override // mt1.p
        public void a(mt1.o oVar, String str, String str2) {
            p.a.h(this, oVar, str, str2);
        }

        @Override // mt1.p
        public void i(mt1.o oVar, String str, SaaSReply saaSReply) {
            p.a.g(this, oVar, str, saaSReply);
        }

        @Override // mt1.p
        public void G(mt1.o oVar, String str, boolean z17, SaaSComment saaSComment) {
            p.a.c(this, oVar, str, z17, saaSComment);
        }
    }

    private final void Y() {
        if (this.f95398n == null) {
            this.f95398n = new m02.n();
        }
    }

    @Override // dr1.h
    public void onReaderDestroy() {
        o02.a.f290838d.a(this.f95385a);
        this.f95397m.k();
    }

    private final void H0() {
        BusProvider.unregister(this);
        oy1.e.f296218c.i(this.f95407w);
        mt1.n nVar = mt1.n.f284522a;
        nVar.O(this.f95408x);
        nVar.O(this.f95409y);
    }

    private final void o0() {
        this.f95385a.e(new Function1() { // from class: com.dragon.community.impl.reader.b
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return CSSParagraphCommentReaderService.p0(this.f95430a, (s94.p) obj);
            }
        });
        this.f95385a.c(new Function0() { // from class: com.dragon.community.impl.reader.h
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return CSSParagraphCommentReaderService.q0(this.f95530a);
            }
        });
        E0();
        z0();
    }

    private final void z0() {
        BusProvider.register(this);
        oy1.e.f296218c.c(this.f95407w);
        mt1.n nVar = mt1.n.f284522a;
        nVar.n(this.f95408x);
        nVar.n(this.f95409y);
    }

    @Override // dr1.h
    public void a() {
        if (TextUtils.isEmpty(this.f95396l)) {
            return;
        }
        n02.g.f(this.f95390f, this.f95396l);
    }

    private final void E0() {
        Intent intent;
        String stringExtra;
        final JumpOpenReaderParaParams jumpOpenReaderParaParams;
        boolean zA;
        boolean z17;
        Activity activity = com.dragon.community.saas.utils.h.getActivity(this.f95385a.getContext());
        if (activity == null || (intent = activity.getIntent()) == null || (stringExtra = intent.getStringExtra("chapterId")) == null) {
            return;
        }
        Serializable serializableExtra = intent.getSerializableExtra("openParaCommentDialogParams");
        if (serializableExtra instanceof JumpOpenReaderParaParams) {
            jumpOpenReaderParaParams = (JumpOpenReaderParaParams) serializableExtra;
        } else {
            jumpOpenReaderParaParams = null;
        }
        if (jumpOpenReaderParaParams == null) {
            return;
        }
        s94.z zVar = this.f95387c;
        if (zVar != null) {
            zA = zVar.a("tryShowIdeaContainerDialog", new Runnable() { // from class: com.dragon.community.impl.reader.i
                @Override // java.lang.Runnable
                public final void run() {
                    CSSParagraphCommentReaderService.F0(this.f95534a);
                }
            });
        } else {
            zA = false;
        }
        if (zA) {
            this.f95389e.f("tryShowIdeaContainerDialog 书籍动画进行中，等待动画结束", new Object[0]);
            return;
        }
        List<s94.l> listD = this.f95385a.d(stringExtra);
        if (listD != null && !listD.isEmpty()) {
            z17 = false;
        } else {
            z17 = true;
        }
        if (z17) {
            this.f95389e.f("tryShowIdeaContainerDialog 排版未完成，等待排版完成", new Object[0]);
            this.f95385a.i(true, new Function0() { // from class: com.dragon.community.impl.reader.j
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return CSSParagraphCommentReaderService.G0(this.f95536a, jumpOpenReaderParaParams);
                }
            });
        } else {
            y0(jumpOpenReaderParaParams);
        }
    }

    private final boolean X() {
        boolean z17;
        boolean z18;
        List<Activity> listG = com.dragon.read.lib.community.inner.c.f173369a.b().b().c().g();
        int size = listG.size() - 1;
        Activity activity = null;
        Activity activity2 = null;
        while (true) {
            if (-1 >= size) {
                break;
            }
            Activity activity3 = listG.get(size);
            if (!activity3.isFinishing() && !activity3.isDestroyed()) {
                if (activity2 != null) {
                    activity = activity3;
                    break;
                }
                activity2 = activity3;
            }
            size--;
        }
        Activity activity4 = n32.b.getActivity(this.f95385a.getContext());
        if (activity2 != null && activity4 != null && activity2.getClass() == activity4.getClass()) {
            z17 = true;
        } else {
            z17 = false;
        }
        if (activity != null && activity4 != null && activity.getClass() == activity4.getClass()) {
            z18 = true;
        } else {
            z18 = false;
        }
        if (z17 || z18) {
            return true;
        }
        return false;
    }

    private final void onDestroy() {
        this.f95389e.f("ReaderService onDestroy", new Object[0]);
        H0();
        n02.g.a(this.f95390f);
        m02.a aVar = this.f95405u;
        if (aVar != null) {
            if (!aVar.isShowing()) {
                aVar = null;
            }
            if (aVar != null) {
                aVar.dismiss();
            }
        }
        this.f95405u = null;
        m02.k kVar = this.f95406v;
        if (kVar != null) {
            if (!kVar.isShowing()) {
                kVar = null;
            }
            if (kVar != null) {
                kVar.dismiss();
            }
        }
        this.f95406v = null;
        Job job = this.f95410z;
        if (job != null) {
            Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
        }
        this.f95410z = null;
    }

    private static final void F0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService) {
        cSSParagraphCommentReaderService.E0();
    }

    public static final class d implements oy1.f {
        @Override // xv1.e
        public int getPriority() {
            return -1;
        }

        d() {
        }

        @Override // xv1.e
        /* JADX INFO: renamed from: e, reason: merged with bridge method [inline-methods] */
        public boolean d(vr1.e data) {
            Intrinsics.checkNotNullParameter(data, "data");
            return CSSParagraphCommentReaderService.this.j0(data);
        }

        @Override // xv1.e
        /* JADX INFO: renamed from: f, reason: merged with bridge method [inline-methods] */
        public boolean b(vr1.f data) {
            Intrinsics.checkNotNullParameter(data, "data");
            return CSSParagraphCommentReaderService.this.m0(data);
        }

        @Override // xv1.e
        /* JADX INFO: renamed from: g, reason: merged with bridge method [inline-methods] */
        public boolean a(vr1.e data) {
            Intrinsics.checkNotNullParameter(data, "data");
            return CSSParagraphCommentReaderService.this.X();
        }

        @Override // xv1.e
        /* JADX INFO: renamed from: h, reason: merged with bridge method [inline-methods] */
        public boolean c(vr1.f data) {
            Intrinsics.checkNotNullParameter(data, "data");
            return CSSParagraphCommentReaderService.this.X();
        }
    }

    private static final Unit q0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService) {
        cSSParagraphCommentReaderService.onDestroy();
        return Unit.INSTANCE;
    }

    private static final GetIdeaListData s0(GetIdeaListResponse it6) throws Exception {
        Intrinsics.checkNotNullParameter(it6, "it");
        jv1.y.a(it6);
        return it6.data;
    }

    public static final class e implements d0.b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ Context f95414a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ CSSParagraphCommentReaderService f95415b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ ICSSParaTextBlock f95416c;

        @Override // com.dragon.community.impl.publish.d0.b
        public boolean b(ParagraphComment data) {
            Intrinsics.checkNotNullParameter(data, "data");
            if (com.dragon.community.impl.d.f93177a.a().a().G() && ct1.k.c(data)) {
                return true;
            }
            return false;
        }

        @Override // com.dragon.community.impl.publish.d0.b
        public void a(final ParagraphComment data) {
            Intrinsics.checkNotNullParameter(data, "data");
            r94.u uVarC = com.dragon.read.lib.community.inner.c.f173369a.b().b().a().c();
            Context context = this.f95414a;
            String string = context.getString(R.string.egn);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            String string2 = this.f95414a.getString(R.string.c7m);
            Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
            final CSSParagraphCommentReaderService cSSParagraphCommentReaderService = this.f95415b;
            final ICSSParaTextBlock iCSSParaTextBlock = this.f95416c;
            u.a.a(uVarC, context, string, string2, new Function0() { // from class: com.dragon.community.impl.reader.q
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return CSSParagraphCommentReaderService.e.d(cSSParagraphCommentReaderService, data, iCSSParaTextBlock);
                }
            }, null, 16, null);
        }

        private static final Unit d(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, ParagraphComment paragraphComment, ICSSParaTextBlock iCSSParaTextBlock) {
            cSSParagraphCommentReaderService.r0(paragraphComment, iCSSParaTextBlock, "publish_toast");
            return Unit.INSTANCE;
        }

        e(Context context, CSSParagraphCommentReaderService cSSParagraphCommentReaderService, ICSSParaTextBlock iCSSParaTextBlock) {
            this.f95414a = context;
            this.f95415b = cSSParagraphCommentReaderService;
            this.f95416c = iCSSParaTextBlock;
        }
    }

    public static final class f implements d0.b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ Context f95417a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ CSSParagraphCommentReaderService f95418b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ ICSSParaTextBlock f95419c;

        @Override // com.dragon.community.impl.publish.d0.b
        public boolean b(ParagraphComment data) {
            Intrinsics.checkNotNullParameter(data, "data");
            if (com.dragon.community.impl.d.f93177a.a().a().G() && ct1.k.c(data)) {
                return true;
            }
            return false;
        }

        @Override // com.dragon.community.impl.publish.d0.b
        public void a(final ParagraphComment data) {
            Intrinsics.checkNotNullParameter(data, "data");
            r94.u uVarC = com.dragon.read.lib.community.inner.c.f173369a.b().b().a().c();
            Context context = this.f95417a;
            String string = context.getString(R.string.egn);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            String string2 = this.f95417a.getString(R.string.c7m);
            Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
            final CSSParagraphCommentReaderService cSSParagraphCommentReaderService = this.f95418b;
            final ICSSParaTextBlock iCSSParaTextBlock = this.f95419c;
            u.a.a(uVarC, context, string, string2, new Function0() { // from class: com.dragon.community.impl.reader.s
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return CSSParagraphCommentReaderService.f.d(cSSParagraphCommentReaderService, data, iCSSParaTextBlock);
                }
            }, null, 16, null);
        }

        private static final Unit d(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, ParagraphComment paragraphComment, ICSSParaTextBlock iCSSParaTextBlock) {
            cSSParagraphCommentReaderService.r0(paragraphComment, iCSSParaTextBlock, "publish_toast");
            return Unit.INSTANCE;
        }

        f(Context context, CSSParagraphCommentReaderService cSSParagraphCommentReaderService, ICSSParaTextBlock iCSSParaTextBlock) {
            this.f95417a = context;
            this.f95418b = cSSParagraphCommentReaderService;
            this.f95419c = iCSSParaTextBlock;
        }
    }

    private final void W(Function0<Unit> function0) {
        this.f95410z = kotlinx.coroutines.i.e(CoroutineScopeKt.CoroutineScope(Dispatchers.getMain()), null, null, new CSSParagraphCommentReaderService$afterAiImageActivityFinish$1(function0, null), 3, null);
    }

    @Override // zt1.a
    public boolean d(String chapterId) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        return !Intrinsics.areEqual(this.f95395k.get(chapterId), Boolean.TRUE);
    }

    @Override // dr1.h
    public void p(String chapterId) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        Map<Integer, ParaIdeaDataModel> mapF0 = f0(this, chapterId, false, null, 6, null);
        if (mapF0 != null) {
            this.f95397m.j(chapterId, mapF0);
        }
    }

    private final com.dragon.community.kmp.publish.model.g c0(String str) {
        com.dragon.community.kmp.publish.model.g gVar = this.f95392h.get(str);
        if (gVar == null) {
            com.dragon.community.kmp.publish.model.g gVar2 = new com.dragon.community.kmp.publish.model.g(null, null, null, null, com.dragon.community.impl.d.f93177a.a().a().p(), null, 47, null);
            this.f95392h.put(str, gVar2);
            return gVar2;
        }
        return gVar;
    }

    private final l.d d0(String str) {
        l.d dVar = this.f95391g.get(str);
        if (dVar == null) {
            l.d dVar2 = new l.d(com.dragon.community.impl.d.f93177a.a().a().p());
            this.f95391g.put(str, dVar2);
            return dVar2;
        }
        return dVar;
    }

    private final boolean i0(s94.b bVar) {
        if (!bVar.a()) {
            return false;
        }
        if (!bVar.d()) {
            this.f95389e.f("普通阅读器，非内容行，content='" + bVar.getContent() + '\'', new Object[0]);
            return false;
        }
        return true;
    }

    private static final Unit k0(List originList) {
        r94.p pVarA;
        r94.g gVarB;
        Intrinsics.checkNotNullParameter(originList, "originList");
        Iterator it6 = originList.iterator();
        while (it6.hasNext()) {
            com.dragon.community.kmp.publish.model.a aVar = (com.dragon.community.kmp.publish.model.a) it6.next();
            String strL = aVar.l();
            if (strL == null) {
                strL = aVar.m();
            }
            if (strL != null && (pVarA = com.dragon.read.lib.community.inner.c.f173369a.b().a()) != null && (gVarB = pVarA.b()) != null) {
                gVarB.f(strL);
            }
        }
        return Unit.INSTANCE;
    }

    @Override // dr1.h
    public SaaSAiImageOpenParams.DraftInfoParams j(ICSSParaTextBlock textBlock) {
        l.d dVarD0;
        Intrinsics.checkNotNullParameter(textBlock, "textBlock");
        if (com.dragon.community.impl.d.f93177a.b().e()) {
            dVarD0 = pz1.k.a(c0(textBlock.createKey()));
        } else {
            dVarD0 = d0(textBlock.createKey());
        }
        return com.dragon.community.common.model.a.f(dVarD0);
    }

    private final boolean j0(vr1.e eVar) {
        List<com.dragon.community.kmp.publish.model.a> listA;
        final ICSSParaTextBlock iCSSParaTextBlockI = eVar.i();
        if (Intrinsics.areEqual(eVar.h(), this.f95390f) && iCSSParaTextBlockI != null) {
            final vr1.m mVar = new vr1.m();
            mVar.j(!x(this.f95385a.g().d(), iCSSParaTextBlockI));
            if (com.dragon.community.impl.d.f93177a.b().e()) {
                com.dragon.community.kmp.publish.model.g gVarC0 = c0(iCSSParaTextBlockI.createKey());
                com.dragon.community.kmp.publish.model.c cVarO = z02.a.o(eVar);
                if (cVarO != null && (listA = com.dragon.community.kmp.publish.model.d.a(cVarO)) != null) {
                    gVarC0.b(listA, new Function1() { // from class: com.dragon.community.impl.reader.m
                        @Override // kotlin.jvm.functions.Function1
                        public final Object invoke(Object obj) {
                            return CSSParagraphCommentReaderService.k0((List) obj);
                        }
                    });
                }
            } else {
                d0(iCSSParaTextBlockI.createKey()).c(du1.c.a(eVar));
            }
            W(new Function0() { // from class: com.dragon.community.impl.reader.n
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return CSSParagraphCommentReaderService.l0(this.f95548a, mVar, iCSSParaTextBlockI);
                }
            });
            return true;
        }
        return false;
    }

    private final void y0(JumpOpenReaderParaParams jumpOpenReaderParaParams) {
        String str;
        ICSSParaTextBlock iCSSParaTextBlockH = this.f95386b.h(this.f95385a);
        if (iCSSParaTextBlockH == null) {
            return;
        }
        this.f95389e.f("跳转进入阅读器并自动拉起段评弹窗", new Object[0]);
        String str2 = null;
        Serializable serializable = f.a.a(com.dragon.read.lib.community.inner.c.f173369a.b().b().a().q(), this.f95385a.getContext(), false, 2, null).getExtraInfoMap().get("position");
        if (serializable instanceof String) {
            str2 = (String) serializable;
        }
        if (str2 == null) {
            str = "reader_paragraph";
        } else {
            str = str2;
        }
        h.a.b(this, this.f95385a.getContext(), this.f95385a, iCSSParaTextBlockH, 0, null, this.f95386b.g(), this.f95386b.a(), jumpOpenReaderParaParams, str, null, false, null, false, 7680, null);
    }

    @Override // dr1.h
    public void b(s94.l pageData) {
        Intrinsics.checkNotNullParameter(pageData, "pageData");
        if (this.A != null) {
            this.f95394j.clear();
        }
        this.A = pageData;
        m02.n nVar = this.f95398n;
        if (nVar != null) {
            nVar.d(pageData);
        }
        m02.m mVar = this.f95399o;
        if (mVar != null) {
            mVar.d(pageData);
        }
        String chapterId = pageData.getChapterId();
        if (TextUtils.isEmpty(chapterId)) {
            return;
        }
        if (TextUtils.isEmpty(this.f95396l)) {
            this.f95396l = chapterId;
        } else if (!TextUtils.equals(this.f95396l, chapterId)) {
            n02.g.f(this.f95390f, this.f95396l);
            this.f95396l = chapterId;
            p(chapterId);
        }
    }

    @Override // dr1.h
    public int k(s94.l page) {
        Intrinsics.checkNotNullParameter(page, "page");
        String chapterId = page.getChapterId();
        Boolean bool = this.f95395k.get(chapterId);
        if (bool != null) {
            bool.booleanValue();
            Map<Integer, ParaIdeaDataModel> map = this.f95393i.get(chapterId);
            if (map == null) {
                return 0;
            }
            for (s94.k kVar : page.getLineList()) {
                if (kVar instanceof s94.b) {
                    s94.b bVar = (s94.b) kVar;
                    if (bVar.a()) {
                        int iG = bVar.g();
                        ParaIdeaDataModel paraIdeaDataModel = map.get(Integer.valueOf(iG));
                        if (paraIdeaDataModel != null && paraIdeaDataModel.enableHighLightAnim() && paraIdeaDataModel.canPlayHighLightAnim(this.f95390f, chapterId, iG)) {
                            return 1;
                        }
                    } else {
                        continue;
                    }
                }
            }
            return 0;
        }
        return -1;
    }

    @Override // dr1.h
    public int t(s94.l lVar) {
        ParaIdeaDataModel paraIdeaDataModelC;
        int i17 = 0;
        if (lVar == null) {
            return 0;
        }
        List<s94.k> lineList = lVar.getLineList();
        String chapterId = lVar.getChapterId();
        if (!CSSReaderApi.IMPL.readerSwitchService().i(this.f95390f)) {
            return 0;
        }
        for (s94.k kVar : lineList) {
            if (kVar instanceof s94.b) {
                s94.b bVar = (s94.b) kVar;
                if (bVar.a() && (paraIdeaDataModelC = c(chapterId, bVar.g())) != null) {
                    i17 += paraIdeaDataModelC.getParaIdeaData().count;
                }
            }
        }
        return i17;
    }

    private final boolean m0(vr1.f fVar) {
        final ICSSParaTextBlock iCSSParaTextBlockL = fVar.l();
        if (Intrinsics.areEqual(fVar.k(), this.f95390f) && iCSSParaTextBlockL != null) {
            final vr1.m mVar = new vr1.m();
            mVar.j(!x(this.f95385a.g().d(), iCSSParaTextBlockL));
            com.dragon.community.impl.d dVar = com.dragon.community.impl.d.f93177a;
            if (dVar.b().e()) {
                com.dragon.community.kmp.publish.model.g.G(c0(iCSSParaTextBlockL.createKey()), null, null, null, null, z02.a.u(fVar.d()), null, null, z02.a.v(fVar), null, null, 879, null);
            } else {
                d0(iCSSParaTextBlockL.createKey()).e(new lt1.a(null, null, null, null, null, null, null, fVar.d(), fVar, 127, null));
            }
            if (dVar.b().e()) {
                W(new Function0() { // from class: com.dragon.community.impl.reader.g
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return CSSParagraphCommentReaderService.n0(this.f95517a, mVar, iCSSParaTextBlockL);
                    }
                });
            } else {
                h.a.a(this, this.f95385a.getContext(), mVar, iCSSParaTextBlockL, null, 8, null);
            }
            return true;
        }
        return false;
    }

    private static final Unit G0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, JumpOpenReaderParaParams jumpOpenReaderParaParams) {
        cSSParagraphCommentReaderService.y0(jumpOpenReaderParaParams);
        return Unit.INSTANCE;
    }

    private static final Unit p0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, s94.p args) {
        Intrinsics.checkNotNullParameter(args, "args");
        cSSParagraphCommentReaderService.V(args, args.getPageDataList(), false);
        return Unit.INSTANCE;
    }

    private static final GetIdeaListData t0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (GetIdeaListData) function1.invoke(p07);
    }

    private static final Boolean v0(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (Boolean) function1.invoke(p07);
    }

    public static final class c<T> implements Comparator {
        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Comparator
        public final int compare(T t17, T t18) {
            return ComparisonsKt__ComparisonsKt.compareValues(Integer.valueOf(((ConfigItem) t17).publishHour), Integer.valueOf(((ConfigItem) t18).publishHour));
        }
    }

    @Override // dr1.h
    public ParaIdeaDataModel c(String chapterId, int i17) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        Map mapF0 = f0(this, chapterId, false, null, 6, null);
        if (mapF0 != null) {
            return (ParaIdeaDataModel) mapF0.get(Integer.valueOf(i17));
        }
        return null;
    }

    @Override // dr1.h
    public int r(String chapterId, int i17) {
        ParaIdeaData paraIdeaData;
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        ParaIdeaDataModel paraIdeaDataModelC = c(chapterId, i17);
        if (paraIdeaDataModelC != null && (paraIdeaData = paraIdeaDataModelC.getParaIdeaData()) != null) {
            return paraIdeaData.count;
        }
        return 0;
    }

    @Override // dr1.h
    public boolean x(s94.l lVar, ICSSParaTextBlock textBlock) {
        boolean z17;
        Intrinsics.checkNotNullParameter(textBlock, "textBlock");
        if (lVar == null || !Intrinsics.areEqual(lVar.getChapterId(), textBlock.getChapterId())) {
            return false;
        }
        int endParaId = textBlock.getEndParaId();
        int endOffsetInPara = textBlock.getEndOffsetInPara() - 1;
        for (s94.k kVar : lVar.getLineList()) {
            if (kVar instanceof s94.b) {
                s94.b bVar = (s94.b) kVar;
                if (bVar.g() == endParaId) {
                    int iF = bVar.f();
                    if (endOffsetInPara <= bVar.b() && iF <= endOffsetInPara) {
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (z17) {
                        return true;
                    }
                } else {
                    continue;
                }
            }
        }
        return false;
    }

    private final Pair<List<vr1.d>, Boolean> Z(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        boolean z17 = true;
        if (g0(str, str2)) {
            int iJ = this.f95385a.getReaderConfig().j();
            n02.f fVarA = n02.e.f285495a.a(iJ, 1, Type.Companion.b());
            vr1.d dVar = new vr1.d(-1);
            dVar.g(fVarA.b());
            dVar.e(iJ);
            dVar.f(fVarA.c());
            arrayList.add(dVar);
            return TuplesKt.to(arrayList, Boolean.TRUE);
        }
        if (this.f95385a.k(str)) {
            this.f95389e.f("当前章节为只读章节，不显示评论，chapterId=" + str, new Object[0]);
            return TuplesKt.to(arrayList, Boolean.FALSE);
        }
        Map mapF0 = f0(this, str, false, str2, 2, null);
        if (mapF0 != null && !mapF0.isEmpty()) {
            z17 = false;
        }
        if (z17) {
            return TuplesKt.to(arrayList, Boolean.FALSE);
        }
        for (Map.Entry entry : mapF0.entrySet()) {
            int iIntValue = ((Number) entry.getKey()).intValue();
            ParaIdeaDataModel paraIdeaDataModel = (ParaIdeaDataModel) entry.getValue();
            if (iIntValue >= 0 && paraIdeaDataModel.getParaIdeaData().count > 0 && h0(iIntValue)) {
                int iJ2 = this.f95385a.getReaderConfig().j();
                n02.f fVarA2 = n02.e.f285495a.a(iJ2, paraIdeaDataModel.getParaIdeaData().count, paraIdeaDataModel.getType());
                vr1.d dVar2 = new vr1.d(iIntValue);
                dVar2.g(fVarA2.b());
                dVar2.e(iJ2);
                dVar2.f(fVarA2.c());
                arrayList.add(dVar2);
            }
        }
        return TuplesKt.to(arrayList, Boolean.FALSE);
    }

    @Override // dr1.h
    public Single<Boolean> w(final String chapterId, String str) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        if (!d(chapterId)) {
            this.f95389e.f("已有段评数据，不需要重新请求, chapterId=%s", chapterId);
            Single<Boolean> singleJust = Single.just(Boolean.TRUE);
            Intrinsics.checkNotNullExpressionValue(singleJust, "just(...)");
            return singleJust;
        }
        GetIdeaListRequest getIdeaListRequest = new GetIdeaListRequest();
        getIdeaListRequest.itemID = chapterId;
        getIdeaListRequest.commentSource = UgcCommentSourceEnum.NovelParaCommentExposed;
        getIdeaListRequest.itemVersion = str;
        com.dragon.read.lib.community.inner.c cVar = com.dragon.read.lib.community.inner.c.f173369a;
        getIdeaListRequest.appID = cVar.b().b().c().c().b();
        getIdeaListRequest.complianceStatus = cVar.b().b().c().getComplianceStatus();
        final ku1.a aVar = new ku1.a("idea_list");
        Observable<GetIdeaListResponse> observableObserveOn = CommentApiService.getIdeaListRxJava(getIdeaListRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        final Function1 function1 = new Function1() { // from class: com.dragon.community.impl.reader.o
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return CSSParagraphCommentReaderService.s0((GetIdeaListResponse) obj);
            }
        };
        Observable<R> map = observableObserveOn.map(new Function() { // from class: com.dragon.community.impl.reader.p
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return CSSParagraphCommentReaderService.t0(function1, obj);
            }
        });
        final Function1 function12 = new Function1() { // from class: com.dragon.community.impl.reader.c
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return CSSParagraphCommentReaderService.u0(this.f95432a, chapterId, aVar, (GetIdeaListData) obj);
            }
        };
        Single<Boolean> singleSingleOrError = map.map(new Function() { // from class: com.dragon.community.impl.reader.d
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return CSSParagraphCommentReaderService.v0(function12, obj);
            }
        }).onErrorReturn(new Function() { // from class: com.dragon.community.impl.reader.e
            @Override // io.reactivex.functions.Function
            public final Object apply(Object obj) {
                return CSSParagraphCommentReaderService.w0(this.f95441a, chapterId, aVar, (Throwable) obj);
            }
        }).doFinally(new Action() { // from class: com.dragon.community.impl.reader.f
            @Override // io.reactivex.functions.Action
            public final void run() {
                CSSParagraphCommentReaderService.x0();
            }
        }).singleOrError();
        Intrinsics.checkNotNullExpressionValue(singleSingleOrError, "singleOrError(...)");
        return singleSingleOrError;
    }

    private final boolean g0(String str, String str2) {
        int i17;
        long jB;
        if (!this.f95403s) {
            this.f95389e.f("未命中实验，不开启预占位, chapterId=" + str, new Object[0]);
            return false;
        }
        Boolean bool = (Boolean) CollectionKt.getOrNull(this.f95401q, str);
        if (bool != null) {
            this.f95389e.f("该章节已开启/不开启预备位，保持不变, chapterId=" + str, new Object[0]);
            return bool.booleanValue();
        }
        if (!s94.d.a(this.f95385a.m(), str)) {
            this.f95389e.f("非当前章，不开启预占位, chapterId=" + str + ", currentChapterId=" + this.f95385a.m().getCurrentChapterId(), new Object[0]);
            return false;
        }
        if (!CSSReaderApi.IMPL.readerSwitchService().m(this.f95390f)) {
            this.f95389e.f("段评开关为关，不开启预占位，chapterId=" + str, new Object[0]);
            return false;
        }
        if (this.f95385a.k(str)) {
            this.f95389e.f("只读章节，不开启预占位，chapterId=" + str, new Object[0]);
            return false;
        }
        if (this.f95395k.contains(str)) {
            this.f95389e.f("气泡数据已请求完成，不开启预占位，chapterId=" + str, new Object[0]);
            return false;
        }
        ParaBubbleData paraBubbleDataH = this.f95397m.h(str, str2);
        if (paraBubbleDataH == null) {
            this.f95389e.f("没有气泡缓存，开启预占位，chapterId=" + str, new Object[0]);
            return true;
        }
        if (paraBubbleDataH.getCacheTime() <= 0) {
            this.f95389e.f("历史数据没有记录缓存时间，默认缓存已过期，开启预占位，chapterId=" + str, new Object[0]);
            return true;
        }
        ParaBubbleCacheTimeConfig paraBubbleCacheTimeConfig = this.f95404t;
        if (paraBubbleCacheTimeConfig != null) {
            s94.a aVarX = this.f95385a.f().X(str);
            if (aVarX != null) {
                jB = aVarX.b();
            } else {
                jB = -1;
            }
            if (jB > 0) {
                long jF = (com.dragon.read.lib.community.inner.c.f173369a.b().b().c().h().f() - jB) / ((long) 3600000);
                List listSortedWith = CollectionsKt___CollectionsKt.sortedWith(paraBubbleCacheTimeConfig.publishTimeConfig, new c());
                i17 = paraBubbleCacheTimeConfig.maxPublishTimeCacheHour;
                Iterator it6 = listSortedWith.iterator();
                while (true) {
                    if (!it6.hasNext()) {
                        break;
                    }
                    ConfigItem configItem = (ConfigItem) it6.next();
                    if (jF <= configItem.publishHour) {
                        i17 = configItem.cacheHour;
                        break;
                    }
                }
                this.f95389e.f("章节更新距离现在" + jF + "小时, 缓存有效时间=" + i17 + "小时，chapterId=" + str, new Object[0]);
            } else {
                i17 = paraBubbleCacheTimeConfig.noPublishTimeCacheHour;
                this.f95389e.f("获取不到章节更新时间，默认缓存有效时间=" + i17 + "小时，chapterId=" + str, new Object[0]);
            }
        } else {
            i17 = 24;
        }
        float fCurrentTimeMillis = (System.currentTimeMillis() - paraBubbleDataH.getCacheTime()) / 3600000;
        if (fCurrentTimeMillis > i17) {
            this.f95389e.f("缓存已过期，开启预占位，缓存距离现在" + fCurrentTimeMillis + "小时，chapterId=" + str, new Object[0]);
            return true;
        }
        this.f95389e.f("缓存未过期，不开启预占位，缓存距离现在" + fCurrentTimeMillis + "小时，chapterId=" + str, new Object[0]);
        return false;
    }

    private final Map<Integer, ParaIdeaDataModel> e0(String str, boolean z17, String str2) {
        Map<Integer, ParaIdeaDataModel> map = this.f95393i.get(str);
        if (map == null) {
            if (z17) {
                return this.f95397m.i(str, str2);
            }
            return null;
        }
        return map;
    }

    private static final Unit l0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, vr1.m mVar, ICSSParaTextBlock iCSSParaTextBlock) {
        h.a.a(cSSParagraphCommentReaderService, cSSParagraphCommentReaderService.f95385a.getContext(), mVar, iCSSParaTextBlock, null, 8, null);
        return Unit.INSTANCE;
    }

    private static final Unit n0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, vr1.m mVar, ICSSParaTextBlock iCSSParaTextBlock) {
        h.a.a(cSSParagraphCommentReaderService, cSSParagraphCommentReaderService.f95385a.getContext(), mVar, iCSSParaTextBlock, null, 8, null);
        return Unit.INSTANCE;
    }

    private final void r0(SaaSComment saaSComment, ICSSParaTextBlock iCSSParaTextBlock, String str) {
        JumpOpenReaderParaParams jumpOpenReaderParaParams = new JumpOpenReaderParaParams();
        jumpOpenReaderParaParams.setCommentId(saaSComment.getCommentId());
        h.a.b(this, this.f95385a.getContext(), this.f95385a, iCSSParaTextBlock, 0, null, this.f95386b.g(), this.f95386b.a(), jumpOpenReaderParaParams, str, null, false, null, false, 7680, null);
    }

    @Override // dr1.h
    public List<vr1.d> n(String chapterId, String chapterVersion, String chapterName) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        Intrinsics.checkNotNullParameter(chapterVersion, "chapterVersion");
        Intrinsics.checkNotNullParameter(chapterName, "chapterName");
        if (!this.f95402r) {
            return CollectionsKt__CollectionsKt.emptyList();
        }
        Pair<List<vr1.d>, Boolean> pairZ = Z(chapterId, chapterVersion);
        this.f95401q.put(chapterId, pairZ.getSecond());
        this.f95389e.f("气泡排版占位结果，chapterName=" + chapterName + ", chapterId=" + chapterId + ", 预占位=" + pairZ.getSecond().booleanValue() + ", 气泡占位数量=" + pairZ.getFirst().size(), new Object[0]);
        return pairZ.getFirst();
    }

    @Override // dr1.h
    public void u(String chapterId, int i17, ParaIdeaDataModel paraIdeaData) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        Intrinsics.checkNotNullParameter(paraIdeaData, "paraIdeaData");
        Map mapF0 = f0(this, chapterId, false, null, 6, null);
        if (mapF0 == null) {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            linkedHashMap.put(Integer.valueOf(i17), paraIdeaData);
            this.f95393i.put(chapterId, linkedHashMap);
        } else {
            mapF0.put(Integer.valueOf(i17), paraIdeaData);
        }
        this.f95389e.f("同步添加段评成功，chapterId = %s，paraIndex = %s, ideaCount = %d", chapterId, Integer.valueOf(i17), Integer.valueOf(paraIdeaData.getParaIdeaData().count));
        p(chapterId);
    }

    @Override // dr1.h
    public void e(String chapterId, int i17, ParaIdeaDataModel paraIdeaDataModel) {
        boolean z17;
        boolean z18;
        boolean z19;
        boolean z27;
        ParaBubble paraBubble;
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        if (paraIdeaDataModel == null) {
            return;
        }
        ParaIdeaData paraIdeaData = paraIdeaDataModel.getParaIdeaData();
        if (com.dragon.community.impl.d.f93177a.a().a().b() && CSSReaderApi.IMPL.readerSwitchService().o(this.f95390f)) {
            Map<Integer, ParaBubble> map = paraIdeaData.bubbleData;
            if (map != null) {
                paraBubble = (ParaBubble) CollectionKt.getOrNull(map, 3);
            } else {
                paraBubble = null;
            }
            if (paraBubble == null || paraBubble.count <= 0) {
                return;
            }
        }
        if (CSSReaderApi.IMPL.readerSwitchService().a(this.f95390f) && !paraIdeaData.showInLiteMode) {
            return;
        }
        int i18 = paraIdeaData.count;
        if (this.f95394j.containsKey(String.valueOf(i17))) {
            n02.g gVar = n02.g.f285505a;
            String bookId = this.f95385a.m().getBookId();
            int type = paraIdeaDataModel.getType();
            Type.a aVar = Type.Companion;
            if (type == aVar.a()) {
                z19 = true;
            } else {
                z19 = false;
            }
            if (paraIdeaDataModel.getType() == aVar.d()) {
                z27 = true;
            } else {
                z27 = false;
            }
            gVar.e(bookId, chapterId, i17, z19, z27, paraIdeaDataModel.enableHighLight(), true);
            gVar.g(this.f95385a.m().getBookId(), chapterId, i17, i18);
            return;
        }
        this.f95394j.put(String.valueOf(i17), Boolean.TRUE);
        String bookId2 = this.f95385a.m().getBookId();
        int type2 = paraIdeaDataModel.getType();
        Type.a aVar2 = Type.Companion;
        if (type2 == aVar2.a()) {
            z17 = true;
        } else {
            z17 = false;
        }
        if (paraIdeaDataModel.getType() == aVar2.d()) {
            z18 = true;
        } else {
            z18 = false;
        }
        n02.g.d(bookId2, chapterId, i17, i18, z17, z18, paraIdeaDataModel.enableHighLight());
    }

    private final void V(s94.p pVar, List<? extends s94.l> list, boolean z17) {
        s94.b bVar;
        int i17;
        int i18;
        if (this.f95385a.j() || !CSSReaderApi.IMPL.readerSwitchService().i(this.f95390f)) {
            return;
        }
        int i19 = 0;
        try {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            int size = list.size();
            int i27 = 0;
            while (i27 < size) {
                s94.l lVar = list.get(i27);
                if (lVar.isOriginalPage() && !lVar.d()) {
                    List<s94.k> lineList = lVar.getLineList();
                    int size2 = lineList.size();
                    int i28 = 0;
                    while (i28 < size2) {
                        s94.k kVar = lineList.get(i28);
                        if (kVar instanceof s94.b) {
                            bVar = (s94.b) kVar;
                        } else {
                            bVar = null;
                        }
                        s94.b bVar2 = bVar;
                        if (bVar2 == null || !i0(bVar2)) {
                            i17 = size;
                            i18 = i28;
                        } else if (CollectionsKt___CollectionsKt.firstOrNull((List) bVar2.getBlockList()) instanceof r) {
                            this.f95389e.f("已添加block，忽略", new Object[i19]);
                            return;
                        } else {
                            if (this.f95385a.j()) {
                                return;
                            }
                            r rVarM = this.f95386b.m();
                            i17 = size;
                            i18 = i28;
                            rVarM.a(new com.dragon.community.impl.reader.entrance.c(rVarM, this.f95385a, this, this.f95386b, this.f95388d, bVar2));
                            bVar2.c(rVarM);
                        }
                        i28 = i18 + 1;
                        size = i17;
                        i19 = 0;
                    }
                }
                i27++;
                size = size;
                i19 = 0;
            }
            this.f95389e.f("章节：%s，添加block耗时：%d", pVar.getChapterId(), Long.valueOf(SystemClock.elapsedRealtime() - jElapsedRealtime));
        } catch (Exception e17) {
            this.f95389e.d("添加block失败: " + Log.getStackTraceString(e17), new Object[0]);
        }
        if (this.f95385a.j() || z17) {
            return;
        }
        this.f95385a.j();
    }

    private static final void B0(DialogInterface.OnDismissListener onDismissListener, CSSParagraphCommentReaderService cSSParagraphCommentReaderService, xz1.c cVar, DialogInterface dialogInterface) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
        }
        m02.m mVar = cSSParagraphCommentReaderService.f95399o;
        if (mVar != null) {
            mVar.f(cVar.v());
        }
        if (Intrinsics.areEqual(cSSParagraphCommentReaderService.f95406v, dialogInterface)) {
            cSSParagraphCommentReaderService.f95406v = null;
        }
        BusProvider.post(new e95.c(false));
    }

    private static final void D0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, com.dragon.community.impl.list.page.j0 j0Var, DialogInterface.OnDismissListener onDismissListener, DialogInterface dialogInterface) {
        m02.n nVar = cSSParagraphCommentReaderService.f95398n;
        if (nVar != null) {
            nVar.f(j0Var.C());
        }
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
        }
        if (Intrinsics.areEqual(cSSParagraphCommentReaderService.f95405u, dialogInterface)) {
            cSSParagraphCommentReaderService.f95405u = null;
        }
        BusProvider.post(new e95.c(false));
    }

    private static final Boolean w0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, String str, ku1.a aVar, Throwable throwable) {
        Intrinsics.checkNotNullParameter(throwable, "throwable");
        cSSParagraphCommentReaderService.f95389e.d("请求该章段评出错，bookId=%s, chapterId=%s, error=%s", cSSParagraphCommentReaderService.f95390f, str, Log.getStackTraceString(throwable));
        aVar.b(throwable);
        return Boolean.FALSE;
    }

    private static final Boolean u0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, String str, ku1.a aVar, GetIdeaListData it6) {
        boolean z17;
        Intrinsics.checkNotNullParameter(it6, "it");
        cSSParagraphCommentReaderService.f95395k.put(str, Boolean.TRUE);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Map<Integer, ParaIdeaData> map = it6.data;
        if (map != null) {
            for (Map.Entry<Integer, ParaIdeaData> entry : map.entrySet()) {
                ParaIdeaData value = entry.getValue();
                Intrinsics.checkNotNullExpressionValue(value, "<get-value>(...)");
                linkedHashMap.put(entry.getKey(), new ParaIdeaDataModel(value, ParaIdeaDataSource.NETWORK));
            }
        }
        cSSParagraphCommentReaderService.f95393i.put(str, linkedHashMap);
        cSSParagraphCommentReaderService.p(str);
        Map<Integer, ParaIdeaData> map2 = it6.data;
        if (map2 != null && !map2.isEmpty()) {
            z17 = false;
        } else {
            z17 = true;
        }
        aVar.d(z17);
        aVar.c();
        return Boolean.TRUE;
    }

    public CSSParagraphCommentReaderService(s94.s client, s94.y readerDependency, s94.z zVar, s94.x xVar) {
        boolean z17;
        ParaBubbleCacheTimeConfig paraBubbleCacheTimeConfigT;
        Intrinsics.checkNotNullParameter(client, "client");
        Intrinsics.checkNotNullParameter(readerDependency, "readerDependency");
        this.f95385a = client;
        this.f95386b = readerDependency;
        this.f95387c = zVar;
        this.f95388d = xVar;
        this.f95389e = ct1.c.e("ParagraphComment");
        this.f95390f = client.m().getBookId();
        this.f95391g = new LinkedHashMap();
        this.f95392h = new LinkedHashMap();
        this.f95393i = new ConcurrentHashMap<>();
        this.f95394j = new ConcurrentHashMap<>();
        this.f95395k = new ConcurrentHashMap<>();
        this.f95401q = new ConcurrentHashMap<>();
        com.dragon.community.impl.d dVar = com.dragon.community.impl.d.f93177a;
        boolean paraBubbleInlineEnable = dVar.a().a().getParaBubbleInlineEnable();
        this.f95402r = paraBubbleInlineEnable;
        if (paraBubbleInlineEnable && dVar.a().a().S()) {
            z17 = true;
        } else {
            z17 = false;
        }
        this.f95403s = z17;
        if (z17) {
            paraBubbleCacheTimeConfigT = dVar.a().a().t();
        } else {
            paraBubbleCacheTimeConfigT = null;
        }
        this.f95404t = paraBubbleCacheTimeConfigT;
        this.f95407w = new d();
        this.f95408x = new b();
        this.f95409y = new a();
        o0();
        if (xVar != null) {
            xVar.b();
        }
        this.f95397m = new n02.d(client, readerDependency);
    }

    @Override // dr1.h
    public void o(Context context, vr1.m params, ICSSParaTextBlock textBlock, b32.c cVar) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(params, "params");
        Intrinsics.checkNotNullParameter(textBlock, "textBlock");
        this.f95400p = textBlock;
        b32.c cVar2 = new b32.c();
        cVar2.f("book_id", textBlock.getBookId());
        cVar2.f("group_id", textBlock.getChapterId());
        cVar2.f("gid", textBlock.getChapterId());
        cVar2.f("paragraph_id", String.valueOf(textBlock.getEndParaId()));
        cVar2.f("type", "paragraph_comment");
        cVar2.f("position", "paragraph_popup");
        cVar2.f("key_entrance", "paragraph_comment");
        if (cVar != null) {
            cVar2.g(cVar);
        }
        int iA = this.f95386b.a();
        com.dragon.community.impl.d dVar = com.dragon.community.impl.d.f93177a;
        VideoEntranceType videoEntranceTypeL = dVar.a().a().L(textBlock.getBookId());
        VideoEntranceType videoEntranceType = VideoEntranceType.MIX;
        if (videoEntranceTypeL != videoEntranceType) {
            videoEntranceType = VideoEntranceType.NONE;
        }
        SaaSAiImageOpenParams.a aVar = new SaaSAiImageOpenParams.a(false, videoEntranceType.getValue(), 1, null);
        if (dVar.b().e()) {
            com.dragon.community.impl.publish.f fVar = com.dragon.community.impl.publish.f.f95224a;
            Map<String, com.dragon.community.kmp.publish.model.g> map = this.f95392h;
            String strCreateKey = textBlock.createKey();
            String string = context.getString(R.string.f113609f85);
            s94.s sVar = this.f95385a;
            com.dragon.community.impl.publish.ui.d dVar2 = new com.dragon.community.impl.publish.ui.d();
            dVar2.n(new vs1.l(new ys1.h(false, 1, null), new zs1.e(), null, 4, null));
            Unit unit = Unit.INSTANCE;
            fVar.r(context, params, textBlock, map, strCreateKey, string, sVar, dVar2, new t02.j(iA), new t02.b(iA), cVar2, true, new e(context, this, textBlock), aVar);
            return;
        }
        com.dragon.community.impl.publish.f.t(com.dragon.community.impl.publish.f.f95224a, context, params, iA, textBlock, false, this.f95391g, textBlock.createKey(), context.getString(R.string.f113609f85), this.f95385a, new t02.j(iA), cVar2, true, false, null, null, new f(context, this, textBlock), aVar, null, null, 417792, null);
    }

    static /* synthetic */ Map f0(CSSParagraphCommentReaderService cSSParagraphCommentReaderService, String str, boolean z17, String str2, int i17, Object obj) {
        if ((i17 & 2) != 0) {
            z17 = true;
        }
        if ((i17 & 4) != 0) {
            str2 = null;
        }
        return cSSParagraphCommentReaderService.e0(str, z17, str2);
    }

    private final void C0(Context context, s94.s sVar, ICSSParaTextBlock iCSSParaTextBlock, List<UgcComment> list, int i17, JumpOpenReaderParaParams jumpOpenReaderParaParams, final DialogInterface.OnDismissListener onDismissListener, final com.dragon.community.impl.list.page.j0 j0Var) {
        boolean z17;
        boolean z18;
        boolean z19;
        boolean z27;
        r94.g gVarB;
        r94.g gVarB2;
        Y();
        String strA = m02.o.f280407i.a(iCSSParaTextBlock);
        com.dragon.community.impl.d dVar = com.dragon.community.impl.d.f93177a;
        CommentListDialogConfig commentListDialogConfigQ = dVar.a().a().Q();
        boolean zJ = dVar.a().a().J();
        com.dragon.read.lib.community.inner.c cVar = com.dragon.read.lib.community.inner.c.f173369a;
        Activity currentActivity = cVar.b().b().c().getCurrentActivity();
        r94.p pVarA = cVar.b().a();
        int i18 = 0;
        int i19 = 1;
        if (pVarA != null && (gVarB2 = pVarA.b()) != null && gVarB2.isReaderActivity(currentActivity)) {
            z17 = true;
        } else {
            z17 = false;
        }
        r94.p pVarA2 = cVar.b().a();
        if (pVarA2 != null && (gVarB = pVarA2.b()) != null && gVarB.S()) {
            z18 = true;
        } else {
            z18 = false;
        }
        if (zJ && z17) {
            z19 = true;
        } else {
            z19 = false;
        }
        if (zJ && z17 && z18) {
            z27 = true;
        } else {
            z27 = false;
        }
        a.C3593a c3593a = new a.C3593a(true, commentListDialogConfigQ, z19, z27);
        m02.a aVar = this.f95405u;
        DefaultConstructorMarker defaultConstructorMarker = null;
        if (aVar != null) {
            if (!aVar.isShowing()) {
                aVar = null;
            }
            if (aVar != null) {
                aVar.dismiss();
            }
        }
        m02.a aVar2 = new m02.a(context, new t02.h(i18, i19, defaultConstructorMarker), iCSSParaTextBlock, sVar, list, c3593a);
        this.f95405u = aVar2;
        aVar2.a1(dVar.a().a().O());
        aVar2.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.dragon.community.impl.reader.k
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                CSSParagraphCommentReaderService.D0(this.f95540a, j0Var, onDismissListener, dialogInterface);
            }
        });
        aVar2.onThemeUpdate(i17);
        aVar2.e1(j0Var);
        aVar2.show();
        m02.a aVar3 = this.f95405u;
        if (aVar3 != null && aVar3.isShowing()) {
            i18 = 1;
        }
        if (i18 != 0) {
            BusProvider.post(new e95.c(true));
        }
        m02.n nVar = this.f95398n;
        if (nVar != null) {
            nVar.e(strA);
        }
    }

    private final void A0(Context context, s94.s sVar, ICSSParaTextBlock iCSSParaTextBlock, List<UgcComment> list, int i17, String str, JumpOpenReaderParaParams jumpOpenReaderParaParams, String str2, final DialogInterface.OnDismissListener onDismissListener, boolean z17, com.dragon.community.impl.list.page.j0 j0Var) {
        String commentId;
        UgcComment ugcComment;
        boolean z18;
        List listSingletonList;
        xz1.b bVarB;
        boolean z19;
        boolean z27;
        String str3;
        int originalPageCount;
        int originalIndex;
        xz1.e eVarB;
        r94.g gVarB;
        if (this.f95399o == null) {
            this.f95399o = new m02.m();
        }
        m02.k kVar = null;
        if (jumpOpenReaderParaParams != null) {
            commentId = jumpOpenReaderParaParams.getCommentId();
        } else if (list != null && (ugcComment = (UgcComment) CollectionsKt___CollectionsKt.firstOrNull((List) list)) != null) {
            commentId = ugcComment.commentID;
        } else {
            commentId = null;
        }
        if (commentId != null && commentId.length() != 0) {
            z18 = false;
        } else {
            z18 = true;
        }
        if (!z18) {
            listSingletonList = Collections.singletonList(commentId);
        } else {
            listSingletonList = null;
        }
        xz1.g gVarD = pz1.k.d(iCSSParaTextBlock);
        String strA = gVarD.a();
        if (jumpOpenReaderParaParams != null) {
            bVarB = pz1.k.b(jumpOpenReaderParaParams);
        } else {
            bVarB = null;
        }
        final xz1.c cVar = new xz1.c(gVarD, listSingletonList, bVarB, str);
        cVar.A(true);
        com.dragon.read.lib.community.inner.c cVar2 = com.dragon.read.lib.community.inner.c.f173369a;
        r94.p pVarA = cVar2.b().a();
        if (pVarA != null && (gVarB = pVarA.b()) != null && gVarB.S()) {
            z19 = true;
        } else {
            z19 = false;
        }
        cVar.F(z19);
        m02.m mVar = this.f95399o;
        if (mVar != null && (eVarB = mVar.b(strA)) != null) {
            cVar.K(eVarB);
        }
        if (cVar.v() == null) {
            String strT = cVar.t();
            String strC = gVarD.c();
            int index = sVar.f().getIndex(iCSSParaTextBlock.getChapterId());
            s94.l lVarD = sVar.g().d();
            if (lVarD != null) {
                originalPageCount = lVarD.getOriginalPageCount();
            } else {
                originalPageCount = 0;
            }
            s94.l lVarD2 = sVar.g().d();
            if (lVarD2 != null) {
                originalIndex = lVarD2.getOriginalIndex();
            } else {
                originalIndex = 0;
            }
            cVar.K(new xz1.e(strT, strC, index, originalPageCount, originalIndex, null, 32, null));
        }
        if (com.dragon.community.impl.d.f93177a.a().b().a(iCSSParaTextBlock.getBookId()) && j0Var.k()) {
            z27 = true;
        } else {
            z27 = false;
        }
        cVar.D(z27);
        ps1.c cVarF = cVar.f();
        cVarF.e("book_id", gVarD.b());
        cVarF.e("group_id", gVarD.c());
        cVarF.e("gid", gVarD.c());
        cVarF.e("paragraph_id", String.valueOf(gVarD.e()));
        cVarF.e("type", "paragraph_comment");
        cVarF.e("position", str2);
        cVarF.e("key_entrance", "paragraph_comment");
        cVarF.e("forwarded_position", "paragraph_list");
        cVarF.e("enter_from", str2);
        cVarF.e("follow_source", "paragraph_comment");
        cVarF.e("is_from_reader", Boolean.TRUE);
        cVar.J(z17);
        cVar.G(true);
        cVar.C(cVar2.b().b().c().b());
        cVar.E(n12.k.a().a());
        cVar.I(n12.k.a().e());
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (cVar.l()) {
            str3 = ParamKeyConstants.SdkVersion.VERSION;
        } else if (cVar.q()) {
            str3 = "2";
        } else {
            str3 = "0";
        }
        linkedHashMap.put("comment_dislike_filter", str3);
        cVar.B(linkedHashMap);
        this.f95389e.f("show kmpParaCommentListDialog, current is " + this.f95406v, new Object[0]);
        m02.k kVar2 = this.f95406v;
        if (kVar2 != null) {
            if (kVar2.isShowing()) {
                kVar = kVar2;
            }
            if (kVar != null) {
                kVar.dismiss();
            }
        }
        com.dragon.community.impl.list.page.h0 h0VarF = new t02.h(i17).f();
        Intrinsics.checkNotNull(h0VarF);
        s2 s2VarF = h0VarF.f();
        Intrinsics.checkNotNull(s2VarF);
        m02.k kVar3 = new m02.k(context, cVar, s2VarF, j0Var, list);
        kVar3.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.dragon.community.impl.reader.l
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                CSSParagraphCommentReaderService.B0(onDismissListener, this, cVar, dialogInterface);
            }
        });
        kVar3.show();
        this.f95406v = kVar3;
        BusProvider.post(new e95.c(true));
        m02.m mVar2 = this.f95399o;
        if (mVar2 != null) {
            mVar2.e(strA);
        }
    }

    @Override // dr1.h
    public void f(Context context, s94.s client, ICSSParaTextBlock textBlock, int i17, List<UgcComment> list, String str, int i18, JumpOpenReaderParaParams jumpOpenReaderParaParams, String position, DialogInterface.OnDismissListener onDismissListener, boolean z17, b32.c cVar, boolean z18) {
        String commentId;
        UgcComment ugcComment;
        boolean z19;
        List listSingletonList;
        int originalPageCount;
        int originalIndex;
        String str2;
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(client, "client");
        Intrinsics.checkNotNullParameter(textBlock, "textBlock");
        Intrinsics.checkNotNullParameter(position, "position");
        String strA = m02.o.f280407i.a(textBlock);
        m02.o oVarB = null;
        if (jumpOpenReaderParaParams != null) {
            commentId = jumpOpenReaderParaParams.getCommentId();
        } else if (list != null && (ugcComment = (UgcComment) CollectionsKt___CollectionsKt.firstOrNull((List) list)) != null) {
            commentId = ugcComment.commentID;
        } else {
            commentId = null;
        }
        if (commentId != null && commentId.length() != 0) {
            z19 = false;
        } else {
            z19 = true;
        }
        if (!z19) {
            listSingletonList = Collections.singletonList(commentId);
        } else {
            listSingletonList = null;
        }
        int index = client.f().getIndex(textBlock.getChapterId());
        s94.l lVarD = client.g().d();
        if (lVarD != null) {
            originalPageCount = lVarD.getOriginalPageCount();
        } else {
            originalPageCount = 0;
        }
        s94.l lVarD2 = client.g().d();
        if (lVarD2 != null) {
            originalIndex = lVarD2.getOriginalIndex();
        } else {
            originalIndex = 0;
        }
        com.dragon.community.impl.list.page.j0 j0Var = new com.dragon.community.impl.list.page.j0(textBlock, listSingletonList, client, jumpOpenReaderParaParams, str, index, originalPageCount, originalIndex, i17, null, null, null, z18, 3584, null);
        m02.n nVar = this.f95398n;
        if (nVar != null) {
            oVarB = nVar.b(strA);
        }
        j0Var.L(oVarB);
        j0Var.M(position);
        b32.c cVarM = j0Var.m();
        cVarM.g(cVar);
        cVarM.f("book_id", j0Var.h().getBookId());
        cVarM.f("group_id", j0Var.h().getChapterId());
        cVarM.f("gid", j0Var.h().getChapterId());
        cVarM.f("paragraph_id", String.valueOf(j0Var.h().getEndParaId()));
        cVarM.f("type", "paragraph_comment");
        cVarM.f("position", position);
        cVarM.f("key_entrance", "paragraph_comment");
        cVarM.f("forwarded_position", "paragraph_list");
        cVarM.f("enter_from", position);
        cVarM.f("follow_source", "paragraph_comment");
        cVarM.f("is_from_reader", Boolean.TRUE);
        j0Var.N(yz1.r.s(j0Var));
        j0Var.K(z17);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        com.dragon.community.impl.d dVar = com.dragon.community.impl.d.f93177a;
        if (dVar.a().a().a()) {
            str2 = ParamKeyConstants.SdkVersion.VERSION;
        } else if (dVar.a().a().e()) {
            str2 = "2";
        } else {
            str2 = "0";
        }
        linkedHashMap.put("comment_dislike_filter", str2);
        j0Var.H(linkedHashMap);
        if (dVar.b().e()) {
            A0(context, client, textBlock, list, i18, str, jumpOpenReaderParaParams, position, onDismissListener, z17, j0Var);
        } else {
            C0(context, client, textBlock, list, i18, jumpOpenReaderParaParams, onDismissListener, j0Var);
        }
    }
}