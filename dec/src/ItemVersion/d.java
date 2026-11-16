package n02;

import android.app.Application;
import android.content.SharedPreferences;
import com.bytedance.covode.number.Covode;
import com.dragon.community.api.model.ParaIdeaDataModel;
import com.dragon.community.api.model.ParaIdeaDataSource;
import com.dragon.community.impl.reader.entrance.ParaBubbleData;
import com.dragon.community.saas.json.BridgeJsonUtils;
import com.dragon.community.saas.utils.LogHelper;
import com.dragon.community.saas.utils.f1;
import com.dragon.read.saas.ugc.model.ParaIdeaData;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.collections.MapsKt__MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import n02.d;
import s94.s;
import s94.y;

/* JADX INFO: loaded from: classes24.dex */
public final class d {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final a f285485f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final int f285486g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static final LogHelper f285487h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static boolean f285488i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final SharedPreferences f285489j;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final s f285490a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final y f285491b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final String f285492c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private boolean f285493d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private ParaBubbleData f285494e;

    public static final class a {
        static {
            Covode.recordClassIndex(562250);
        }

        private a() {
        }

        public /* synthetic */ a(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* JADX INFO: renamed from: n02.d$a$a, reason: collision with other inner class name */
        public static final class C3703a extends TypeToken<Map<String, ? extends Long>> {
            C3703a() {
            }
        }

        private final void e() {
            if (d.f285488i) {
                return;
            }
            f1.c(new Runnable() { // from class: n02.c
                @Override // java.lang.Runnable
                public final void run() {
                    d.a.f();
                }
            });
        }

        private final Map<String, Long> g() {
            Map<String, Long> mapL = com.dragon.community.common.model.a.l(d.f285489j.getString("para_bubble_cache_map", null), new C3703a());
            if (mapL == null) {
                return MapsKt__MapsKt.emptyMap();
            }
            return mapL;
        }

        private static final void f() {
            boolean z17;
            d.f285488i = true;
            Map<String, Long> mapG = d.f285485f.g();
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            if (!mapG.isEmpty()) {
                SharedPreferences.Editor editorEdit = d.f285489j.edit();
                for (Map.Entry<String, Long> entry : mapG.entrySet()) {
                    String key = entry.getKey();
                    long jLongValue = entry.getValue().longValue();
                    if (System.currentTimeMillis() - jLongValue > 2592000000L) {
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (z17) {
                        editorEdit.remove(key);
                        d.f285487h.f("清除段评缓存 key=" + key, new Object[0]);
                    } else {
                        linkedHashMap.put(key, Long.valueOf(jLongValue));
                    }
                }
                editorEdit.apply();
                d.f285485f.h(linkedHashMap);
            }
            d.f285487h.f("clearCache 定期清除段评缓存，oldSize=" + mapG.size() + ", newSize=" + linkedHashMap.size(), new Object[0]);
        }

        private final void h(Map<String, Long> map) {
            d.f285489j.edit().putString("para_bubble_cache_map", com.dragon.community.common.model.a.n(map)).apply();
        }
    }

    public final void k() {
        f285485f.e();
    }

    private final ParaBubbleData g() {
        if (this.f285494e == null) {
            String string = f285489j.getString(f(), null);
            if (string == null) {
                return null;
            }
            this.f285494e = (ParaBubbleData) BridgeJsonUtils.c(string, ParaBubbleData.class);
        }
        return this.f285494e;
    }

    static {
        Covode.recordClassIndex(562249);
        f285485f = new a(null);
        f285486g = 8;
        f285487h = ct1.c.e("ParaComment");
        r94.e eVarC = com.dragon.read.lib.community.inner.c.f173369a.b().b().c();
        Application applicationA = com.dragon.community.saas.utils.a.a();
        Intrinsics.checkNotNullExpressionValue(applicationA, "context(...)");
        f285489j = eVarC.d(applicationA, "para_bubble_cache");
    }

    private final String f() {
        return "para_bubble_" + com.dragon.read.lib.community.inner.c.f173369a.b().b().c().h().g() + '_' + this.f285492c;
    }

    private final boolean e(String str) {
        return s94.d.a(this.f285490a.m(), str);
    }

    private final void l(String str) {
        a aVar = f285485f;
        Map mutableMap = MapsKt__MapsKt.toMutableMap(aVar.g());
        if (mutableMap.containsKey(str)) {
            mutableMap.remove(str);
            aVar.h(mutableMap);
        }
    }

    private final void o(String str) {
        a aVar = f285485f;
        Map mutableMap = MapsKt__MapsKt.toMutableMap(aVar.g());
        mutableMap.put(str, Long.valueOf(System.currentTimeMillis()));
        aVar.h(mutableMap);
    }

    private final void m(int i17) {
        if (this.f285493d) {
            return;
        }
        this.f285493d = true;
        b32.c cVar = new b32.c();
        cVar.f("code", Integer.valueOf(i17));
        com.dragon.read.lib.community.inner.c.f173369a.b().b().a().j("para_cache_monitor", cVar);
        f285487h.f("获取段评缓存结果 bookId=" + this.f285492c + ", code=" + i17, new Object[0]);
    }

    public final void j(String chapterId, Map<Integer, ParaIdeaDataModel> map) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        if (!e(chapterId)) {
            return;
        }
        n(chapterId, map);
    }

    public d(s client, y readerDependency) {
        Intrinsics.checkNotNullParameter(client, "client");
        Intrinsics.checkNotNullParameter(readerDependency, "readerDependency");
        this.f285490a = client;
        this.f285491b = readerDependency;
        this.f285492c = client.m().getBookId();
    }

    public final ParaBubbleData h(String chapterId, String str) {
        int i17;
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        ParaBubbleData paraBubbleData = null;
        if (!e(chapterId)) {
            return null;
        }
        ParaBubbleData paraBubbleDataG = g();
        if (paraBubbleDataG == null) {
            i17 = 1;
        } else if (!Intrinsics.areEqual(paraBubbleDataG.getChapterId(), chapterId)) {
            i17 = 2;
        } else {
            String itemVersion = paraBubbleDataG.getItemVersion();
            if (str == null) {
                str = this.f285491b.b(chapterId);
            }
            if (!Intrinsics.areEqual(itemVersion, str)) {
                i17 = 3;
            } else {
                i17 = 0;
                paraBubbleData = paraBubbleDataG;
            }
        }
        m(i17);
        return paraBubbleData;
    }

    public final Map<Integer, ParaIdeaDataModel> i(String chapterId, String str) {
        Intrinsics.checkNotNullParameter(chapterId, "chapterId");
        ParaBubbleData paraBubbleDataH = h(chapterId, str);
        if (paraBubbleDataH == null) {
            return null;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry<Integer, ParaIdeaData> entry : paraBubbleDataH.getData().entrySet()) {
            linkedHashMap.put(entry.getKey(), new ParaIdeaDataModel(entry.getValue(), ParaIdeaDataSource.CACHE));
        }
        return linkedHashMap;
    }

    private final void n(String str, Map<Integer, ParaIdeaDataModel> map) {
        if (!this.f285493d) {
            m(1);
        }
        String strF = f();
        if (map == null) {
            this.f285494e = null;
            f285489j.edit().remove(strF).apply();
            l(strF);
            f285487h.f("saveParaBubbleData 当前章无段评，移除段评缓存, chapterId=" + str, new Object[0]);
            return;
        }
        String strB = this.f285491b.b(str);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry<Integer, ParaIdeaDataModel> entry : map.entrySet()) {
            int iIntValue = entry.getKey().intValue();
            ParaIdeaDataModel value = entry.getValue();
            Integer numValueOf = Integer.valueOf(iIntValue);
            ParaIdeaData paraIdeaData = new ParaIdeaData();
            paraIdeaData.count = value.getParaIdeaData().count;
            paraIdeaData.isAuthorComment = value.getParaIdeaData().isAuthorComment;
            paraIdeaData.userCount = value.getParaIdeaData().userCount;
            paraIdeaData.hot = value.getParaIdeaData().hot;
            linkedHashMap.put(numValueOf, paraIdeaData);
        }
        ParaBubbleData paraBubbleData = new ParaBubbleData(strB, str, linkedHashMap, System.currentTimeMillis());
        this.f285494e = paraBubbleData;
        f285489j.edit().putString(strF, BridgeJsonUtils.d(paraBubbleData)).apply();
        o(strF);
        f285487h.f("更新段评缓存 chapterId=" + str, new Object[0]);
    }
}