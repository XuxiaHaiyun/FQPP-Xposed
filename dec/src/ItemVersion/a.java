package s94;

import com.bytedance.covode.number.Covode;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes7.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final String f310203a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final String f310204b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final String f310205c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final long f310206d;

    static {
        Covode.recordClassIndex(594925);
    }

    public final String a() {
        return this.f310204b;
    }

    public final long b() {
        return this.f310206d;
    }

    public a(String str, String str2, String version, long j17) {
        Intrinsics.checkNotNullParameter(version, "version");
        this.f310203a = str;
        this.f310204b = str2;
        this.f310205c = version;
        this.f310206d = j17;
    }
}