package w45;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import com.bytedance.covode.number.Covode;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.dragon.community.api.model.ParaIdeaDataSource;
import com.dragon.community.common.contentpublish.l;
import com.dragon.community.common.model.SaaSComment;
import com.dragon.community.common.model.SaaSReply;
import com.dragon.read.R;
import com.dragon.read.base.Args;
import com.dragon.read.base.ssconfig.SsConfigMgr;
import com.dragon.read.base.ssconfig.template.AigcImageOpt;
import com.dragon.read.base.ssconfig.template.StoryParaCommentSupportAigcSameImage;
import com.dragon.read.base.util.LogHelper;
import com.dragon.read.base.util.LogWrapper;
import com.dragon.read.rpc.model.AbConfigSourceGroup;
import com.dragon.read.rpc.model.PostData;
import com.dragon.read.saas.comment.paracomment.model.StoryParaComment;
import com.dragon.read.saas.ugc.model.GetIdeaListData;
import com.dragon.read.saas.ugc.model.GetIdeaListRequest;
import com.dragon.read.saas.ugc.model.GetIdeaListResponse;
import com.dragon.read.saas.ugc.model.ParaIdeaData;
import com.dragon.read.saas.ugc.model.UgcCommentGroupTypeOutter;
import com.dragon.read.saas.ugc.model.UgcCommentSourceEnum;
import com.dragon.read.saas.ugc.rpc.CommentApiService;
import com.dragon.read.story.impl.comment.post.paragraphcomment.IStoryParaTextBlock;
import com.dragon.read.story.impl.comment.post.paragraphcomment.ai.StoryParaAiImageFragment;
import com.dragon.read.story.impl.comment.post.paragraphcomment.ai.f;
import com.dragon.read.story.impl.comment.post.paragraphcomment.list.b;
import com.dragon.read.story.impl.comment.post.paragraphcomment.model.StoryOpenParaCommentParams;
import com.dragon.read.util.ReaderCommonColor;
import com.dragon.read.util.kotlin.ResourcesKt;
import com.ss.android.messagebus.BusProvider;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Unit;
import kotlin.collections.CollectionsKt__CollectionsJVMKt;
import kotlin.collections.CollectionsKt__CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt__StringsKt;
import mt1.p;
import s45.i;

/* JADX INFO: loaded from: classes11.dex */
public final class n0 implements s45.i {

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final a f329151n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final int f329152o;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private com.dragon.read.story.impl.feeds.k0 f329153a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final LogHelper f329154b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Map<String, l.d> f329155c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final ConcurrentHashMap<String, Map<Integer, g45.a>> f329156d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final ConcurrentHashMap<String, Boolean> f329157e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final ConcurrentHashMap<String, s45.m> f329158f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private boolean f329159g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private boolean f329160h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private final ConcurrentHashMap<String, Map<Integer, f45.g>> f329161i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private x25.a f329162j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private final c f329163k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private final b f329164l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private final d f329165m;

    public static final class a {
        static {
            Covode.recordClassIndex(617655);
        }

        private a() {
        }

        public /* synthetic */ a(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    static {
        Covode.recordClassIndex(617654);
        f329151n = new a(null);
        f329152o = 8;
    }

    @Override // s45.i
    public boolean j() {
        return this.f329159g;
    }

    @Override // s45.i
    public Map<String, l.d> k() {
        return this.f329155c;
    }

    @Override // s45.i
    public ConcurrentHashMap<String, Map<Integer, g45.a>> l() {
        return this.f329156d;
    }

    @Override // s45.i
    public ConcurrentHashMap<String, s45.m> m() {
        return this.f329158f;
    }

    @Override // s45.i
    public void o() {
        Y();
    }

    @Override // s45.i
    public ConcurrentHashMap<String, Boolean> s() {
        return this.f329157e;
    }

    @Override // s45.i
    public boolean u() {
        return this.f329160h;
    }

    @Override // s45.i
    public void w() {
        T();
    }

    public static final class c implements mt1.p {
        @Override // mt1.p
        public List<UgcCommentGroupTypeOutter> getInterestServiceIds() {
            return CollectionsKt__CollectionsJVMKt.listOf(UgcCommentGroupTypeOutter.Paragraph);
        }

        c() {
        }

        @Override // mt1.a
        public boolean v(b32.c cVar) {
            return p.a.a(this, cVar);
        }

        @Override // mt1.a
        public boolean B(b32.c predicateArgs) {
            String str;
            Intrinsics.checkNotNullParameter(predicateArgs, "predicateArgs");
            Object objC = predicateArgs.c("book_id");
            if (objC instanceof String) {
                str = (String) objC;
            } else {
                str = null;
            }
            return Intrinsics.areEqual(str, n0.this.f329153a.getBookId());
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
            StoryParaComment storyParaComment;
            String groupId;
            f45.g gVar;
            Intrinsics.checkNotNullParameter(syncParams, "syncParams");
            Intrinsics.checkNotNullParameter(comment, "comment");
            p.a.b(this, syncParams, comment);
            if (comment instanceof StoryParaComment) {
                storyParaComment = (StoryParaComment) comment;
            } else {
                storyParaComment = null;
            }
            if (storyParaComment == null || (groupId = comment.getGroupId()) == null) {
                return false;
            }
            int paraId = storyParaComment.getParaId();
            Map<Integer, g45.a> linkedHashMap = n0.this.l().get(groupId);
            if (linkedHashMap == null) {
                linkedHashMap = new LinkedHashMap<>();
            }
            g45.a aVar = linkedHashMap.get(Integer.valueOf(paraId));
            if (aVar != null) {
                g45.a aVar2 = aVar;
                aVar2.a().userCount++;
                aVar2.a().count++;
            } else {
                ParaIdeaData paraIdeaData = new ParaIdeaData();
                paraIdeaData.userCount++;
                paraIdeaData.count = 1;
                g45.a aVar3 = new g45.a(paraIdeaData, ParaIdeaDataSource.SYNC);
                n0.this.I(groupId, paraId, aVar3);
                linkedHashMap.put(Integer.valueOf(paraId), aVar3);
                aVar = aVar3;
            }
            g45.a aVar4 = aVar;
            aVar4.b();
            Map map = (Map) n0.this.f329161i.get(groupId);
            if (map != null && (gVar = (f45.g) map.get(Integer.valueOf(paraId))) != null) {
                gVar.c(aVar4);
            }
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
        public void G(mt1.o syncParams, String commentId, boolean z17, SaaSComment saaSComment) {
            Object objC;
            String str;
            Object objC2;
            f45.g gVar;
            Intrinsics.checkNotNullParameter(syncParams, "syncParams");
            Intrinsics.checkNotNullParameter(commentId, "commentId");
            b32.c cVarC = syncParams.c();
            Integer num = null;
            if (cVarC != null) {
                objC = cVarC.c("group_id");
            } else {
                objC = null;
            }
            if (objC instanceof String) {
                str = (String) objC;
            } else {
                str = null;
            }
            if (str == null) {
                return;
            }
            b32.c cVarC2 = syncParams.c();
            if (cVarC2 != null) {
                objC2 = cVarC2.c("paragraph_id");
            } else {
                objC2 = null;
            }
            if (objC2 instanceof Integer) {
                num = (Integer) objC2;
            }
            if (num != null) {
                int iIntValue = num.intValue();
                Map<Integer, g45.a> linkedHashMap = n0.this.l().get(str);
                if (linkedHashMap == null) {
                    linkedHashMap = new LinkedHashMap<>();
                }
                g45.a aVar = linkedHashMap.get(Integer.valueOf(iIntValue));
                if (aVar != null) {
                    if (z17) {
                        ParaIdeaData paraIdeaDataA = aVar.a();
                        paraIdeaDataA.userCount--;
                    }
                    ParaIdeaData paraIdeaDataA2 = aVar.a();
                    paraIdeaDataA2.count--;
                    aVar.b();
                    Map map = (Map) n0.this.f329161i.get(str);
                    if (map != null && (gVar = (f45.g) map.get(Integer.valueOf(iIntValue))) != null) {
                        gVar.c(aVar);
                        if (aVar.a().count <= 0) {
                            gVar.b();
                        }
                    }
                }
            }
        }
    }

    public static final class e implements l.f {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ Function0<Unit> f329169a;

        @Override // com.dragon.community.common.contentpublish.l.f
        public void a() {
            Function0<Unit> function0 = this.f329169a;
            if (function0 != null) {
                function0.invoke();
            }
        }

        e(Function0<Unit> function0) {
            this.f329169a = function0;
        }
    }

    private final void T() {
        BusProvider.register(this);
        mt1.n.f284522a.n(this.f329163k);
        y25.b.f338212a.a(this.f329164l);
        com.dragon.read.story.impl.comment.post.paragraphcomment.ai.e.f204569c.c(this.f329165m);
    }

    private final void Y() {
        BusProvider.unregister(this);
        mt1.n.f284522a.O(this.f329163k);
        y25.b.f338212a.e(this.f329164l);
        com.dragon.read.story.impl.comment.post.paragraphcomment.ai.e.f204569c.i(this.f329165m);
    }

    @Override // s45.i
    public Single<Boolean> e() {
        if (!this.f329159g && c55.a.f23200a.c()) {
            final String bookId = this.f329153a.getBookId();
            if (bookId == null) {
                Single<Boolean> singleJust = Single.just(Boolean.FALSE);
                Intrinsics.checkNotNullExpressionValue(singleJust, "just(...)");
                return singleJust;
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            HashMap map = new HashMap();
            arrayList.add("allow_story_aigc_ability");
            arrayList2.add(AbConfigSourceGroup.ABConfig);
            map.put("book_id", bookId);
            Single<Map<String, String>> singleObserveOn = SsConfigMgr.getServerABSafely(arrayList, arrayList2, map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            final Function1 function1 = new Function1() { // from class: w45.e0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return n0.U(this.f329110a, bookId, (Map) obj);
                }
            };
            Single<Boolean> singleOnErrorReturn = singleObserveOn.map(new Function() { // from class: w45.f0
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj) {
                    return n0.V(function1, obj);
                }
            }).onErrorReturn(new Function() { // from class: w45.g0
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj) {
                    return n0.W(this.f329124a, (Throwable) obj);
                }
            });
            Intrinsics.checkNotNullExpressionValue(singleOnErrorReturn, "onErrorReturn(...)");
            return singleOnErrorReturn;
        }
        Single<Boolean> singleJust2 = Single.just(Boolean.FALSE);
        Intrinsics.checkNotNullExpressionValue(singleJust2, "just(...)");
        return singleJust2;
    }

    @Override // s45.i
    public Single<Boolean> r() {
        String str;
        boolean z17;
        if (!c55.a.f23200a.c()) {
            Single<Boolean> singleJust = Single.just(Boolean.FALSE);
            Intrinsics.checkNotNullExpressionValue(singleJust, "just(...)");
            return singleJust;
        }
        final String bookId = this.f329153a.getBookId();
        LogWrapper.debug("deliver", this.f329154b.getTag(), "请求段评数据, bookId=" + bookId + ", dataOrder=" + this.f329153a.e0(), new Object[0]);
        PostData postDataN0 = this.f329153a.n0();
        String str2 = null;
        if (postDataN0 != null) {
            str = postDataN0.relateItemId;
        } else {
            str = null;
        }
        PostData postDataN02 = this.f329153a.n0();
        if (postDataN02 != null) {
            str2 = postDataN02.itemVersion;
        }
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
                if (!d(str)) {
                    LogWrapper.info("deliver", this.f329154b.getTag(), "已有段评数据，不需要重新请求, chapterId=" + str, new Object[0]);
                    Single<Boolean> singleJust2 = Single.just(Boolean.TRUE);
                    Intrinsics.checkNotNullExpressionValue(singleJust2, "just(...)");
                    return singleJust2;
                }
                GetIdeaListRequest getIdeaListRequest = new GetIdeaListRequest();
                getIdeaListRequest.itemID = str;
                getIdeaListRequest.commentSource = UgcCommentSourceEnum.NovelParaCommentExposed;
                getIdeaListRequest.itemVersion = str2;
                com.dragon.read.lib.community.inner.c cVar = com.dragon.read.lib.community.inner.c.f173369a;
                getIdeaListRequest.appID = cVar.b().b().c().c().b();
                getIdeaListRequest.complianceStatus = cVar.b().b().c().getComplianceStatus();
                final ku1.a aVar = new ku1.a("story_idea_list");
                final long jCurrentTimeMillis = System.currentTimeMillis();
                Observable<GetIdeaListResponse> observableObserveOn = CommentApiService.getIdeaListRxJava(getIdeaListRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                final Function1 function1 = new Function1() { // from class: w45.h0
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return n0.N((GetIdeaListResponse) obj);
                    }
                };
                Observable<R> map = observableObserveOn.map(new Function() { // from class: w45.i0
                    @Override // io.reactivex.functions.Function
                    public final Object apply(Object obj) {
                        return n0.O(function1, obj);
                    }
                });
                final String str3 = str;
                final Function1 function12 = new Function1() { // from class: w45.j0
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return n0.P(this.f329133a, bookId, str3, aVar, jCurrentTimeMillis, (GetIdeaListData) obj);
                    }
                };
                Observable map2 = map.map(new Function() { // from class: w45.k0
                    @Override // io.reactivex.functions.Function
                    public final Object apply(Object obj) {
                        return n0.Q(function12, obj);
                    }
                });
                final Function1 function13 = new Function1() { // from class: w45.l0
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return n0.R(this.f329142a, bookId, str3, aVar, jCurrentTimeMillis, (Throwable) obj);
                    }
                };
                Single<Boolean> singleSingleOrError = map2.onErrorReturn(new Function() { // from class: w45.m0
                    @Override // io.reactivex.functions.Function
                    public final Object apply(Object obj) {
                        return n0.S(function13, obj);
                    }
                }).singleOrError();
                Intrinsics.checkNotNullExpressionValue(singleSingleOrError, "singleOrError(...)");
                return singleSingleOrError;
            }
        }
        Single<Boolean> singleJust3 = Single.just(Boolean.FALSE);
        Intrinsics.checkNotNullExpressionValue(singleJust3, "just(...)");
        return singleJust3;
    }

    public static final class b implements y25.a {
        b() {
        }

        @Override // y25.a
        public void a(String bookId, String chapterId, int i17, int i18) {
            f45.g gVar;
            Intrinsics.checkNotNullParameter(bookId, "bookId");
            Intrinsics.checkNotNullParameter(chapterId, "chapterId");
            if (!Intrinsics.areEqual(bookId, n0.this.f329153a.getBookId())) {
                return;
            }
            Map<Integer, g45.a> linkedHashMap = n0.this.l().get(chapterId);
            if (linkedHashMap == null) {
                linkedHashMap = new LinkedHashMap<>();
            }
            g45.a aVar = linkedHashMap.get(Integer.valueOf(i17));
            if (aVar != null) {
                aVar.a().count = i18;
                aVar.b();
                Map map = (Map) n0.this.f329161i.get(chapterId);
                if (map != null && (gVar = (f45.g) map.get(Integer.valueOf(i17))) != null) {
                    gVar.c(aVar);
                    if (aVar.a().count <= 0) {
                        gVar.b();
                    }
                }
            }
        }
    }

    public static final class d implements com.dragon.read.story.impl.comment.post.paragraphcomment.ai.f {
        @Override // xv1.e
        public int getPriority() {
            return -1;
        }

        d() {
        }

        @Override // xv1.e
        /* JADX INFO: renamed from: f, reason: merged with bridge method [inline-methods] */
        public boolean b(com.dragon.read.story.impl.comment.post.paragraphcomment.ai.c cVar) {
            return f.a.a(this, cVar);
        }

        @Override // xv1.e
        /* JADX INFO: renamed from: g, reason: merged with bridge method [inline-methods] */
        public boolean a(com.dragon.read.story.impl.comment.post.paragraphcomment.ai.b bVar) {
            return f.a.b(this, bVar);
        }

        @Override // xv1.e
        /* JADX INFO: renamed from: h, reason: merged with bridge method [inline-methods] */
        public boolean c(com.dragon.read.story.impl.comment.post.paragraphcomment.ai.c cVar) {
            return f.a.c(this, cVar);
        }

        @Override // xv1.e
        /* JADX INFO: renamed from: e, reason: merged with bridge method [inline-methods] */
        public boolean d(com.dragon.read.story.impl.comment.post.paragraphcomment.ai.b data) {
            Intrinsics.checkNotNullParameter(data, "data");
            return n0.this.M(data);
        }
    }

    private final Map<Integer, g45.a> L(String str) {
        return l().get(str);
    }

    private static final GetIdeaListData N(GetIdeaListResponse it6) throws Exception {
        Intrinsics.checkNotNullParameter(it6, "it");
        jv1.y.a(it6);
        return it6.data;
    }

    @Override // s45.i
    public boolean d(String chapterId) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        return !Intrinsics.areEqual(s().get(chapterId), Boolean.TRUE);
    }

    @Override // s45.i
    public boolean q(String chapterId) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        if (this.f329161i.get(chapterId) == null || !(!r3.isEmpty())) {
            return false;
        }
        return true;
    }

    @Override // s45.i
    public void t(s45.i storyParaCommentProxy) {
        Intrinsics.checkNotNullParameter(storyParaCommentProxy, "storyParaCommentProxy");
        k().putAll(storyParaCommentProxy.k());
        l().putAll(storyParaCommentProxy.l());
        s().putAll(storyParaCommentProxy.s());
        m().putAll(storyParaCommentProxy.m());
        this.f329160h = storyParaCommentProxy.u();
        this.f329159g = storyParaCommentProxy.j();
    }

    public n0(com.dragon.read.story.impl.feeds.k0 story) {
        Intrinsics.checkNotNullParameter(story, "story");
        this.f329153a = story;
        this.f329154b = com.dragon.read.story.impl.feeds.c0.f204972a.k("ParaComment");
        this.f329155c = new LinkedHashMap();
        this.f329156d = new ConcurrentHashMap<>();
        this.f329157e = new ConcurrentHashMap<>();
        this.f329158f = new ConcurrentHashMap<>();
        this.f329161i = new ConcurrentHashMap<>();
        this.f329162j = new x25.a();
        this.f329163k = new c();
        this.f329164l = new b();
        this.f329165m = new d();
        ur1.b.f322566a.c("story_para_ai_image_fragment", StoryParaAiImageFragment.class);
    }

    private final boolean M(com.dragon.read.story.impl.comment.post.paragraphcomment.ai.b bVar) {
        Context clientContext;
        IStoryParaTextBlock iStoryParaTextBlockI = bVar.i();
        if (iStoryParaTextBlockI != null && Intrinsics.areEqual(iStoryParaTextBlockI.getBookId(), this.f329153a.getBookId())) {
            l.d dVarK = K(iStoryParaTextBlockI.createKey(), true);
            if (dVarK != null) {
                dVarK.c(du1.c.a(bVar));
            }
            com.dragon.read.story.impl.container.b bVarF = this.f329153a.F();
            if (bVarF != null && (clientContext = bVarF.getClientContext()) != null) {
                String strH = bVar.h();
                if (strH == null) {
                    strH = "";
                }
                i.a.a(this, clientContext, iStoryParaTextBlockI, true, strH, null, null, 48, null);
                return true;
            }
        }
        return false;
    }

    @Override // s45.i
    public void h(boolean z17) {
        Collection<f45.g> collectionValues;
        Iterator<T> it6 = this.f329153a.E().keySet().iterator();
        while (it6.hasNext()) {
            Map<Integer, f45.g> map = this.f329161i.get((String) it6.next());
            if (map != null && (collectionValues = map.values()) != null) {
                for (f45.g gVar : collectionValues) {
                    if (z17) {
                        gVar.a();
                    } else {
                        gVar.b();
                    }
                }
            }
        }
    }

    @Override // s45.i
    public void g(String chapterId) {
        boolean z17;
        s45.m mVar;
        boolean z18;
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        if (StringsKt__StringsKt.isBlank(chapterId)) {
            return;
        }
        s45.m mVar2 = m().get(chapterId);
        int i17 = 0;
        if (mVar2 != null && mVar2.c() == 0) {
            z17 = true;
        } else {
            z17 = false;
        }
        if (!z17 && (mVar = m().get(chapterId)) != null) {
            Args args = new Args();
            Collection<Integer> collectionValues = mVar.b().values();
            if (!(collectionValues instanceof Collection) || !collectionValues.isEmpty()) {
                Iterator<T> it6 = collectionValues.iterator();
                int i18 = 0;
                while (it6.hasNext()) {
                    if (((Number) it6.next()).intValue() > 0) {
                        z18 = true;
                    } else {
                        z18 = false;
                    }
                    if (z18 && (i18 = i18 + 1) < 0) {
                        CollectionsKt__CollectionsKt.throwCountOverflow();
                    }
                }
                i17 = i18;
            }
            args.put("entrance_cnt", Integer.valueOf(i17));
            args.put("paragraph_id_cnt", com.dragon.community.saas.utils.y.n(mVar.b()));
            this.f329153a.t0().g(args);
            mVar.a();
        }
    }

    private static final GetIdeaListData O(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (GetIdeaListData) function1.invoke(p07);
    }

    private static final Boolean Q(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (Boolean) function1.invoke(p07);
    }

    private static final Boolean S(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (Boolean) function1.invoke(p07);
    }

    private static final Boolean V(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (Boolean) function1.invoke(p07);
    }

    public int J(String chapterId, int i17) {
        ParaIdeaData paraIdeaDataA;
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        g45.a aVarC = c(chapterId, i17);
        if (aVarC != null && (paraIdeaDataA = aVarC.a()) != null) {
            return paraIdeaDataA.count;
        }
        return 0;
    }

    @Override // s45.i
    public g45.a c(String chapterId, int i17) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        Map<Integer, g45.a> mapL = L(chapterId);
        if (mapL != null) {
            return mapL.get(Integer.valueOf(i17));
        }
        return null;
    }

    private static final Boolean W(n0 n0Var, Throwable it6) {
        Intrinsics.checkNotNullParameter(it6, "it");
        LogWrapper.info("deliver", n0Var.f329154b.getTag(), "aigc生图功能请求失败: " + it6, new Object[0]);
        return Boolean.FALSE;
    }

    public l.d K(String draftKey, boolean z17) {
        Intrinsics.checkNotNullParameter(draftKey, "draftKey");
        l.d dVar = k().get(draftKey);
        if (dVar == null && z17) {
            l.d dVar2 = new l.d(false, 1, null);
            k().put(draftKey, dVar2);
            return dVar2;
        }
        return dVar;
    }

    @Override // s45.i
    public void f(String chapterId, List<Integer> paraIdList) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        Intrinsics.checkNotNullParameter(paraIdList, "paraIdList");
        Map<Integer, f45.g> map = this.f329161i.get(chapterId);
        if (map != null) {
            ArrayList arrayList = new ArrayList();
            for (Object obj : paraIdList) {
                if (map.containsKey(Integer.valueOf(((Number) obj).intValue()))) {
                    arrayList.add(obj);
                }
            }
            if (!arrayList.isEmpty()) {
                Iterator it6 = arrayList.iterator();
                while (it6.hasNext()) {
                    map.remove(Integer.valueOf(((Number) it6.next()).intValue()));
                }
            }
        }
    }

    @Override // s45.i
    public void n(String chapterId, int i17, f45.g view) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        Intrinsics.checkNotNullParameter(view, "view");
        Map<Integer, f45.g> linkedHashMap = this.f329161i.get(chapterId);
        if (linkedHashMap == null) {
            linkedHashMap = new LinkedHashMap<>();
            this.f329161i.put(chapterId, linkedHashMap);
        }
        linkedHashMap.put(Integer.valueOf(i17), view);
    }

    private static final Boolean U(n0 n0Var, String str, Map it6) {
        Intrinsics.checkNotNullParameter(it6, "it");
        boolean z17 = true;
        n0Var.f329159g = true;
        if (!Intrinsics.areEqual(it6.get("allow_story_aigc_ability"), ParamKeyConstants.SdkVersion.VERSION) || !AigcImageOpt.f121366a.a().enable) {
            z17 = false;
        }
        n0Var.f329160h = z17;
        LogWrapper.info("deliver", n0Var.f329154b.getTag(), "aigc生图功能请求成功，bookId=" + str + ", isAigcEnable=" + n0Var.f329160h, new Object[0]);
        return Boolean.TRUE;
    }

    public void I(String chapterId, int i17, g45.a paraCommentData) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        Intrinsics.checkNotNullParameter(paraCommentData, "paraCommentData");
        Map<Integer, g45.a> mapL = L(chapterId);
        if (mapL == null) {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            linkedHashMap.put(Integer.valueOf(i17), paraCommentData);
            l().put(chapterId, linkedHashMap);
        } else {
            mapL.put(Integer.valueOf(i17), paraCommentData);
        }
        LogWrapper.info("deliver", this.f329154b.getTag(), "同步添加段评成功，chapterId = %s，paraIndex = %s, ideaCount = %d", new Object[]{chapterId, Integer.valueOf(i17), Integer.valueOf(paraCommentData.a().count)});
    }

    @Override // s45.i
    public void i(String chapterId, int i17, int i18) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        if (StringsKt__StringsKt.isBlank(chapterId)) {
            return;
        }
        s45.m mVar = m().get(chapterId);
        if (mVar == null) {
            mVar = new s45.m();
        }
        mVar.d(mVar.c() + 1);
        mVar.b().put(Integer.valueOf(i17), Integer.valueOf(i18));
        LogWrapper.debug("deliver", this.f329154b.getTag(), "key is " + chapterId + ", paraId is " + i17 + ", entranceCount=" + mVar.c() + ", commentCount=" + i18, new Object[0]);
        m().put(chapterId, mVar);
    }

    private static final void X(n0 n0Var, b35.c cVar, Function0 function0, DialogInterface dialogInterface) {
        n0Var.f329162j.e(cVar.d());
        if (function0 != null) {
            function0.invoke();
        }
    }

    private static final Boolean R(n0 n0Var, String str, String str2, ku1.a aVar, long j17, Throwable throwable) {
        Intrinsics.checkNotNullParameter(throwable, "throwable");
        LogWrapper.error("deliver", n0Var.f329154b.getTag(), "请求短故事段评出错，bookId=" + str + ", chapterId=" + str2 + ", error=" + Log.getStackTraceString(throwable), new Object[0]);
        aVar.e(System.currentTimeMillis() - j17);
        aVar.b(throwable);
        return Boolean.FALSE;
    }

    private static final Boolean P(n0 n0Var, String str, String str2, ku1.a aVar, long j17, GetIdeaListData it6) {
        Integer numValueOf;
        Intrinsics.checkNotNullParameter(it6, "it");
        LogHelper logHelper = n0Var.f329154b;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("请求短故事段评成功，bookId=");
        sb6.append(str);
        sb6.append(", chapterId=");
        sb6.append(str2);
        sb6.append(", data=");
        Map<Integer, ParaIdeaData> map = it6.data;
        if (map != null) {
            numValueOf = Integer.valueOf(map.size());
        } else {
            numValueOf = null;
        }
        sb6.append(numValueOf);
        String string = sb6.toString();
        boolean z17 = false;
        LogWrapper.info("deliver", logHelper.getTag(), string, new Object[0]);
        n0Var.s().put(str2, Boolean.TRUE);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Map<Integer, ParaIdeaData> map2 = it6.data;
        if (map2 != null) {
            for (Map.Entry<Integer, ParaIdeaData> entry : map2.entrySet()) {
                ParaIdeaData value = entry.getValue();
                Intrinsics.checkNotNullExpressionValue(value, "<get-value>(...)");
                linkedHashMap.put(entry.getKey(), new g45.a(value, ParaIdeaDataSource.NETWORK));
            }
        }
        n0Var.l().put(str2, linkedHashMap);
        aVar.e(System.currentTimeMillis() - j17);
        Map<Integer, ParaIdeaData> map3 = it6.data;
        if (map3 == null || map3.isEmpty()) {
            z17 = true;
        }
        aVar.d(z17);
        aVar.c();
        return Boolean.TRUE;
    }

    @Override // s45.i
    public void p(Context context, IStoryParaTextBlock textBlock, boolean z17, String position, Function0<Unit> function0, Function0<Unit> function02) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(textBlock, "textBlock");
        Intrinsics.checkNotNullParameter(position, "position");
        b32.c cVarZ = com.dragon.read.social.util.a0.z(this.f329153a.t0().h());
        cVarZ.f("paragraph_id", String.valueOf(textBlock.getEndParaId()));
        cVarZ.f("type", "paragraph_comment");
        cVarZ.f("position", position);
        cVarZ.f("comment_count", Integer.valueOf(J(textBlock.getStoryChapterId(), textBlock.getEndParaId())));
        d35.d dVar = new d35.d();
        i35.d dVar2 = i35.d.f258365a;
        int iL = dVar2.l();
        Map<String, l.d> mapK = k();
        String strCreateKey = textBlock.createKey();
        String string = ResourcesKt.getString(R.string.dg9);
        zy4.f fVar = new zy4.f(dVar2.l());
        fVar.A(false);
        Unit unit = Unit.INSTANCE;
        d35.d.d(dVar, context, textBlock, iL, false, mapK, strCreateKey, string, fVar, cVarZ, false, this.f329160h, "paragraph_popup_comment", null, new e(function02), 4096, null);
        if (function0 != null) {
            function0.invoke();
        }
    }

    @Override // s45.i
    public void v(Context context, com.dragon.read.story.impl.feeds.k0 story, com.dragon.read.story.impl.container.b storyClient, IStoryParaTextBlock storyParaBlock, String position, StoryOpenParaCommentParams storyOpenParaCommentParams, Function0<Unit> function0, final Function0<Unit> function02) {
        boolean z17;
        String str;
        String commentId;
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(story, "story");
        Intrinsics.checkNotNullParameter(storyClient, "storyClient");
        Intrinsics.checkNotNullParameter(storyParaBlock, "storyParaBlock");
        Intrinsics.checkNotNullParameter(position, "position");
        String strCreateKey = storyParaBlock.createKey();
        final b35.c cVar = new b35.c(storyParaBlock);
        cVar.u(this.f329162j.b(strCreateKey));
        cVar.y(position);
        cVar.v(this.f329160h);
        boolean z18 = false;
        if (this.f329160h && StoryParaCommentSupportAigcSameImage.f124822a.a().enable) {
            z17 = true;
        } else {
            z17 = false;
        }
        cVar.w(z17);
        cVar.z("post_comment_detail");
        cVar.x(storyOpenParaCommentParams);
        if (storyOpenParaCommentParams != null && (commentId = storyOpenParaCommentParams.getCommentId()) != null) {
            cVar.m().add(commentId);
        }
        b32.c cVarA = cVar.a();
        cVarA.g(com.dragon.read.social.util.a0.z(story.t0().h()));
        cVarA.f("group_id", cVar.t().getStoryChapterId());
        cVarA.f("gid", cVar.t().getStoryChapterId());
        cVarA.f("paragraph_id", String.valueOf(cVar.t().getEndParaId()));
        cVarA.f("type", "paragraph_comment");
        cVarA.f("position", position);
        if (story.f()) {
            str = "0";
        } else {
            str = ParamKeyConstants.SdkVersion.VERSION;
        }
        cVarA.f("if_enter_post_page", str);
        cVarA.f("comment_count", Integer.valueOf(J(cVar.t().getStoryChapterId(), cVar.t().getEndParaId())));
        b.a aVar = new b.a(z18, null, 2, 0 == true ? 1 : 0);
        int iL = i35.d.f258365a.l();
        if (cVar.n() && !ReaderCommonColor.isBlackTheme(iL)) {
            iL = 1;
        }
        ow4.a aVar2 = new ow4.a(0, 1, null);
        aVar2.g(cVar.n());
        com.dragon.read.story.impl.comment.post.paragraphcomment.list.b bVar = new com.dragon.read.story.impl.comment.post.paragraphcomment.list.b(context, aVar2, aVar, story, storyClient);
        bVar.e1(cVar);
        bVar.onThemeUpdate(iL);
        bVar.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: w45.d0
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                n0.X(this.f329102a, cVar, function02, dialogInterface);
            }
        });
        bVar.show();
        if (function0 != null) {
            function0.invoke();
        }
        this.f329162j.d(strCreateKey);
        com.dragon.read.story.impl.feeds.g.f205080a.j(storyParaBlock.getBookId(), "story_para_guide_bar_para_show_v685", 3);
    }
}