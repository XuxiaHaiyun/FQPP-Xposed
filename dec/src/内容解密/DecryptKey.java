package com.dragon.read.reader;

import com.bytedance.covode.number.Covode;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes8.dex */
public final class DecryptKey {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final a f183364g;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final String f183365a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final String f183366b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final int f183367c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final int f183368d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final Source f183369e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final boolean f183370f;

    static {
        Covode.recordClassIndex(602146);
        f183364g = new a(null);
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    public static final class Source {
        private static final /* synthetic */ EnumEntries $ENTRIES;
        private static final /* synthetic */ Source[] $VALUES;
        public static final Source CACHE;
        public static final Source NONE;
        public static final Source SERVER;
        private final String symbol;

        private static final /* synthetic */ Source[] $values() {
            return new Source[]{SERVER, CACHE, NONE};
        }

        public static EnumEntries<Source> getEntries() {
            return $ENTRIES;
        }

        public final String getSymbol() {
            return this.symbol;
        }

        public static Source[] values() {
            return (Source[]) $VALUES.clone();
        }

        static {
            Covode.recordClassIndex(602148);
            SERVER = new Source("SERVER", 0, "server");
            CACHE = new Source("CACHE", 1, "cache");
            NONE = new Source("NONE", 2, "");
            Source[] sourceArr$values = $values();
            $VALUES = sourceArr$values;
            $ENTRIES = EnumEntriesKt.enumEntries(sourceArr$values);
        }

        public static Source valueOf(String str) {
            return (Source) Enum.valueOf(Source.class, str);
        }

        private Source(String str, int i17, String str2) {
            this.symbol = str2;
        }
    }

    public final int a() {
        return this.f183367c;
    }

    public final int b() {
        return this.f183368d;
    }

    public final boolean c() {
        return this.f183370f;
    }

    public final Source d() {
        return this.f183369e;
    }

    public final String e() {
        return this.f183365a;
    }

    public final String f() {
        return this.f183366b;
    }

    public static final class a {
        static {
            Covode.recordClassIndex(602147);
        }

        private a() {
        }

        public /* synthetic */ a(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final DecryptKey b() {
            return new DecryptKey("", "", 0, -1, Source.NONE, true);
        }

        public final DecryptKey a() {
            return new DecryptKey("", "", 0, -1, Source.NONE, false, 32, null);
        }
    }

    public String toString() {
        if (this.f183370f) {
            return "DecryptKey(notNeedDecrypt=true)";
        }
        return "DecryptKey(v1.len=" + this.f183365a.length() + ", v2.len=" + this.f183366b.length() + ", ver=" + this.f183367c + ", m_ver=" + this.f183368d + ')';
    }

    public DecryptKey(String v17, String v27, int i17, int i18, Source source, boolean z17) {
        Intrinsics.checkNotNullParameter(v17, "v1");
        Intrinsics.checkNotNullParameter(v27, "v2");
        Intrinsics.checkNotNullParameter(source, "source");
        this.f183365a = v17;
        this.f183366b = v27;
        this.f183367c = i17;
        this.f183368d = i18;
        this.f183369e = source;
        this.f183370f = z17;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public /* synthetic */ DecryptKey(String str, String str2, int i17, int i18, Source source, boolean z17, int i19, DefaultConstructorMarker defaultConstructorMarker) {
        boolean z18;
        if ((i19 & 32) != 0) {
            z18 = false;
        } else {
            z18 = z17;
        }
        this(str, str2, i17, i18, source, z18);
    }
}