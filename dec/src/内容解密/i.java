package w85;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import com.bytedance.covode.number.Covode;
import com.dragon.read.base.Args;
import com.dragon.read.base.ssconfig.template.UseOptCrypt;
import com.dragon.read.base.util.AppUtils;
import com.dragon.read.base.util.LogWrapper;
import com.dragon.read.base.util.ThreadUtils;
import com.dragon.read.crypt.CryptManager;
import com.dragon.read.local.CacheWrapper;
import com.dragon.read.network.ErrorCodeException;
import com.dragon.read.reader.DecryptKey;
import com.dragon.read.util.NumberUtils;
import com.dragon.read.util.ToastUtils;
import com.ss.android.ugc.bytex.taskmonitor.proxy.SingleDelegate;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import readersaas.com.dragon.read.saas.rpc.model.CryptERR;
import readersaas.com.dragon.read.saas.rpc.model.RegisterKeyData;
import readersaas.com.dragon.read.saas.rpc.model.RegisterKeyRequest;
import readersaas.com.dragon.read.saas.rpc.model.RegisterKeyResponse;
import uk4.l0;

/* JADX INFO: loaded from: classes11.dex */
public final class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final i f329741a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final ConcurrentHashMap<String, String> f329742b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final int f329743c;

    public /* synthetic */ class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public static final /* synthetic */ int[] f329744a;

        static {
            Covode.recordClassIndex(619192);
            int[] iArr = new int[CryptERR.values().length];
            try {
                iArr[CryptERR.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[CryptERR.DEGRADE_NONEED_CRYPT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[CryptERR.UNSUPPORT_VERSION.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            f329744a = iArr;
        }
    }

    private i() {
    }

    private final int s() {
        return CryptManager.getMaxMetaKeyVersion();
    }

    private static final void v() {
        ToastUtils.showCommonToast("当前版本停止维护，核心功能将无法使用，请立即更新应用", 1);
    }

    private final boolean m() {
        return UseOptCrypt.f124973a.a().enable;
    }

    static {
        Covode.recordClassIndex(619191);
        f329741a = new i();
        f329742b = new ConcurrentHashMap<>();
        f329743c = 8;
    }

    public static final synchronized Single<DecryptKey> F(String str) {
        return o(str, Integer.MIN_VALUE);
    }

    private final SharedPreferences B(String str) {
        SharedPreferences sharedPreferencesH = CacheWrapper.h(AppUtils.context(), "crypt_key_kv_" + str);
        Intrinsics.checkNotNullExpressionValue(sharedPreferencesH, "getPublicPreference(...)");
        return sharedPreferencesH;
    }

    public static final int C(String str) {
        boolean z17;
        if (str != null && str.length() != 0) {
            z17 = false;
        } else {
            z17 = true;
        }
        if (z17) {
            return 0;
        }
        return f329741a.B(str).getInt("key_timestamp", 0);
    }

    private final String H(int i17) {
        return "key_" + i17;
    }

    private final void j(int i17) throws ErrorCodeException {
        if (i17 <= s()) {
            return;
        }
        LogWrapper.error("experience", "CryptManager", "服务端返回metaKeyVersion=" + i17 + ", 当前最大metaKeyVersion=" + s(), new Object[0]);
        throw new ErrorCodeException(-703, "meta key ver out of range");
    }

    private static final Unit z(Throwable th6) {
        LogWrapper.error("experience", "CryptManager", "getServerKey error: " + Log.getStackTraceString(th6), new Object[0]);
        return Unit.INSTANCE;
    }

    private static final void A(Function1 function1, Object obj) {
        function1.invoke(obj);
    }

    private static final DecryptKey w(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (DecryptKey) function1.invoke(p07);
    }

    private static final DecryptKey y(Function1 function1, Object p07) {
        Intrinsics.checkNotNullParameter(p07, "p0");
        return (DecryptKey) function1.invoke(p07);
    }

    private final String I(int i17, int i18) {
        return "key_v2_" + i17 + '_' + i18;
    }

    public static final Single<DecryptKey> o(final String str, final int i17) {
        if (str == null) {
            str = "";
        }
        Single<DecryptKey> singleSubscribeOn = SingleDelegate.defer(new Callable() { // from class: w85.a
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return i.p(str, i17);
            }
        }).subscribeOn(Schedulers.io());
        Intrinsics.checkNotNullExpressionValue(singleSubscribeOn, "subscribeOn(...)");
        return singleSubscribeOn;
    }

    private final void i(int i17, int i18) throws ErrorCodeException {
        if (i17 != Integer.MIN_VALUE && i18 != i17) {
            LogWrapper.error("experience", "CryptManager", "期望keyVersion=" + i17 + ", 服务端返回KeyVersion=" + i18, new Object[0]);
            throw new ErrorCodeException(-702, "content version mismatch");
        }
    }

    public static final byte[] k(String encryptContent, String key) {
        Intrinsics.checkNotNullParameter(encryptContent, "encryptContent");
        Intrinsics.checkNotNullParameter(key, "key");
        byte[] bArrDecode = Base64.decode(encryptContent, 2);
        byte[] bArrE = rk4.a.e(Arrays.copyOfRange(bArrDecode, 16, bArrDecode.length), key, Arrays.copyOf(bArrDecode, 16));
        Intrinsics.checkNotNullExpressionValue(bArrE, "decrypt(...)");
        return bArrE;
    }

    private final String n(String str, String str2) {
        byte[] bArrI = rk4.a.i(rk4.a.h(NumberUtils.parse(str, 0L)), rk4.a.h(NumberUtils.parse(str2, 0L)));
        String strD = rk4.a.d();
        byte[] bArrF = rk4.a.f(bArrI, "ac25c67ddd8f38c1b37a2348828e222e", strD);
        Intrinsics.checkNotNull(strD);
        byte[] bytes = strD.getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
        String strEncodeToString = Base64.encodeToString(rk4.a.i(bytes, bArrF), 2);
        Intrinsics.checkNotNullExpressionValue(strEncodeToString, "encodeToString(...)");
        return strEncodeToString;
    }

    private static final SingleSource p(String str, int i17) {
        DecryptKey decryptKeyQ;
        i iVar = f329741a;
        if (!iVar.m() || (decryptKeyQ = iVar.r(str, i17)) == null) {
            decryptKeyQ = iVar.q(str, i17);
        }
        if (decryptKeyQ != null) {
            Single singleJust = Single.just(decryptKeyQ);
            Intrinsics.checkNotNull(singleJust);
            return singleJust;
        }
        return iVar.t(str, i17);
    }

    /* JADX WARN: Removed duplicated region for block: B:8:0x001d A[PHI: r2
  0x001d: PHI (r2v2 java.lang.String) = (r2v1 java.lang.String), (r2v3 java.lang.String) binds: [B:3:0x000c, B:5:0x0018] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final DecryptKey q(String str, int i17) {
        String str2;
        boolean z17;
        String strH = H(i17);
        ConcurrentHashMap<String, String> concurrentHashMap = f329742b;
        String str3 = concurrentHashMap.get(strH);
        if (str3 == null) {
            str3 = "";
            String string = B(str).getString(strH, "");
            if (string != null) {
                str2 = string;
            } else {
                str2 = str3;
            }
        }
        if (str2.length() > 0) {
            z17 = true;
        } else {
            z17 = false;
        }
        if (z17) {
            concurrentHashMap.put(strH, str2);
            return new DecryptKey(str2, "", i17, 0, DecryptKey.Source.CACHE, false, 32, null);
        }
        return null;
    }

    private static final DecryptKey x(int i17, DecryptKey key) throws ErrorCodeException {
        boolean z17;
        Intrinsics.checkNotNullParameter(key, "key");
        i iVar = f329741a;
        iVar.i(i17, key.a());
        if (key.f().length() > 0) {
            z17 = true;
        } else {
            z17 = false;
        }
        if (z17 && key.b() >= 0) {
            iVar.j(key.b());
        }
        return key;
    }

    private final DecryptKey r(String str, int i17) {
        boolean z17;
        int iS = s();
        while (true) {
            boolean z18 = true;
            if (1000 < iS) {
                String str2 = f329742b.get(I(i17, iS));
                if (str2 == null) {
                    str2 = "";
                }
                if (str2.length() <= 0) {
                    z18 = false;
                }
                if (z18) {
                    return new DecryptKey("", str2, i17, iS, DecryptKey.Source.CACHE, false, 32, null);
                }
                iS--;
            } else {
                SharedPreferences sharedPreferencesB = B(str);
                for (int iS2 = s(); 1000 < iS2; iS2--) {
                    String strI = I(i17, iS2);
                    String string = sharedPreferencesB.getString(strI, "");
                    if (string == null) {
                        string = "";
                    }
                    if (string.length() > 0) {
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (z17) {
                        f329742b.put(strI, string);
                        return new DecryptKey("", string, i17, iS2, DecryptKey.Source.CACHE, false, 32, null);
                    }
                }
                return null;
            }
        }
    }

    public static final byte[] l(String content, DecryptKey key) {
        boolean z17;
        byte[] bArrDecrypt;
        Intrinsics.checkNotNullParameter(content, "content");
        Intrinsics.checkNotNullParameter(key, "key");
        long jElapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        if (key.c()) {
            bArrDecrypt = content.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bArrDecrypt, "getBytes(...)");
        } else {
            boolean z18 = true;
            if (key.f().length() > 0) {
                z17 = true;
            } else {
                z17 = false;
            }
            if (z17) {
                try {
                    bArrDecrypt = CryptManager.decrypt(content, key.f(), key.b());
                } catch (Exception e17) {
                    LogWrapper.error("experience", "CryptManager", "decryptString(key=" + key + ") " + Log.getStackTraceString(e17), new Object[0]);
                    bArrDecrypt = new byte[0];
                }
            } else {
                if (key.e().length() <= 0) {
                    z18 = false;
                }
                if (z18) {
                    bArrDecrypt = k(content, key.e());
                } else {
                    bArrDecrypt = new byte[0];
                }
            }
        }
        f329741a.G(key, bArrDecrypt.length, SystemClock.elapsedRealtimeNanos() - jElapsedRealtimeNanos);
        Intrinsics.checkNotNull(bArrDecrypt);
        return bArrDecrypt;
    }

    private final Single<DecryptKey> t(final String str, final int i17) {
        boolean z17;
        String deviceId = l0.f321902b.getDeviceId();
        boolean z18 = false;
        final int iS = 1;
        if (deviceId.length() == 0) {
            z17 = true;
        } else {
            z17 = false;
        }
        if (!z17) {
            if (str.length() == 0) {
                z18 = true;
            }
            if (!z18) {
                if (m()) {
                    iS = s();
                }
                RegisterKeyRequest registerKeyRequest = new RegisterKeyRequest();
                registerKeyRequest.keyver = iS;
                registerKeyRequest.content = n(deviceId, str);
                Observable<RegisterKeyResponse> observableSubscribeOn = u56.a.B(registerKeyRequest).subscribeOn(Schedulers.io());
                final Function1 function1 = new Function1() { // from class: w85.b
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return i.u(str, iS, (RegisterKeyResponse) obj);
                    }
                };
                Observable<R> map = observableSubscribeOn.map(new Function() { // from class: w85.c
                    @Override // io.reactivex.functions.Function
                    public final Object apply(Object obj) {
                        return i.w(function1, obj);
                    }
                });
                final Function1 function12 = new Function1() { // from class: w85.d
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return i.x(i17, (DecryptKey) obj);
                    }
                };
                Single singleSingleOrError = map.map(new Function() { // from class: w85.e
                    @Override // io.reactivex.functions.Function
                    public final Object apply(Object obj) {
                        return i.y(function12, obj);
                    }
                }).singleOrError();
                final Function1 function13 = new Function1() { // from class: w85.f
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return i.z((Throwable) obj);
                    }
                };
                Single<DecryptKey> singleDoOnError = singleSingleOrError.doOnError(new Consumer() { // from class: w85.g
                    @Override // io.reactivex.functions.Consumer
                    public final void accept(Object obj) {
                        i.A(function13, obj);
                    }
                });
                Intrinsics.checkNotNullExpressionValue(singleDoOnError, "doOnError(...)");
                return singleDoOnError;
            }
        }
        Single<DecryptKey> singleError = Single.error(new ErrorCodeException(-700, "deviceId or userId is empty"));
        Intrinsics.checkNotNullExpressionValue(singleError, "error(...)");
        return singleError;
    }

    private final void G(DecryptKey decryptKey, int i17, long j17) {
        LogWrapper.info("experience", "CryptManager", "decryptString key=" + decryptKey + " len=" + i17 + " time=" + (j17 / ((long) 1000)) + "us", new Object[0]);
        if (decryptKey.c()) {
            return;
        }
        com.dragon.read.reader.depend.a aVar = com.dragon.read.reader.depend.a.f185562b;
        Args args = new Args();
        args.put("key_length", Integer.valueOf(Math.max(decryptKey.e().length(), decryptKey.f().length())));
        args.put("result_length", Integer.valueOf(i17));
        args.put("meta_key_version", Integer.valueOf(decryptKey.b()));
        args.put("time", Float.valueOf(j17 / 1000000.0f));
        args.put("from", decryptKey.d().getSymbol());
        Unit unit = Unit.INSTANCE;
        aVar.D("decrypt_content", args);
    }

    private static final DecryptKey u(String str, int i17, RegisterKeyResponse result) throws ErrorCodeException {
        int i18;
        boolean z17;
        boolean z18;
        boolean z19;
        DecryptKey decryptKey;
        boolean z27;
        int i19;
        boolean z28;
        Intrinsics.checkNotNullParameter(result, "result");
        CryptERR cryptERR = result.code;
        if (cryptERR == null) {
            i18 = -1;
        } else {
            i18 = a.f329744a[cryptERR.ordinal()];
        }
        if (i18 != 1) {
            if (i18 != 2) {
                if (i18 != 3) {
                    LogWrapper.error("experience", "CryptManager", "getServerKey error code=" + result.code.getValue() + " msg=" + result.message, new Object[0]);
                    throw new ErrorCodeException(result.code.getValue(), result.message);
                }
                ThreadUtils.postInForeground(new Runnable() { // from class: w85.h
                    @Override // java.lang.Runnable
                    public final void run() {
                        i.v();
                    }
                });
                throw new ErrorCodeException(result.code.getValue(), result.message);
            }
            decryptKey = DecryptKey.f183364g.b();
        } else {
            RegisterKeyData registerKeyData = result.data;
            if (registerKeyData != null) {
                String str2 = registerKeyData.key;
                String str3 = "";
                if (str2 == null) {
                    str2 = "";
                }
                String strA = rk4.a.a(k(str2, "ac25c67ddd8f38c1b37a2348828e222e"));
                RegisterKeyData registerKeyData2 = result.data;
                String str4 = registerKeyData2.kmskey;
                if (str4 != null) {
                    str3 = str4;
                }
                int i27 = registerKeyData2.keyver;
                Intrinsics.checkNotNull(strA);
                if (strA.length() == 0) {
                    z17 = true;
                } else {
                    z17 = false;
                }
                if (z17) {
                    if (str3.length() == 0) {
                        z28 = true;
                    } else {
                        z28 = false;
                    }
                    if (z28) {
                        throw new ErrorCodeException(-701, "key is empty");
                    }
                }
                if (strA.length() > 0) {
                    z18 = true;
                } else {
                    z18 = false;
                }
                if (z18) {
                    f329741a.D(str, strA, i27, result.data.keyRegisterTs);
                }
                if (str3.length() > 0) {
                    z19 = true;
                } else {
                    z19 = false;
                }
                if (z19) {
                    f329741a.E(str, str3, i27, i17, result.data.keyRegisterTs);
                }
                if (str3.length() > 0) {
                    z27 = true;
                } else {
                    z27 = false;
                }
                if (z27) {
                    i19 = i17;
                } else {
                    i19 = 1;
                }
                decryptKey = new DecryptKey(strA, str3, i27, i19, DecryptKey.Source.SERVER, false);
            } else {
                throw new ErrorCodeException(-701, "key is empty");
            }
        }
        LogWrapper.info("experience", "CryptManager", "getServerKey(enableV2=" + f329741a.m() + "): " + decryptKey, new Object[0]);
        return decryptKey;
    }

    private final void D(String str, String str2, int i17, int i18) {
        SharedPreferences.Editor editorEdit = B(str).edit();
        editorEdit.putString(f329741a.H(i17), str2);
        editorEdit.putInt("key_timestamp", i18);
        editorEdit.apply();
        LogWrapper.info("experience", "CryptManager", "putCacheKeyV1 ver=" + i17, new Object[0]);
    }

    private final void E(String str, String str2, int i17, int i18, int i19) {
        SharedPreferences.Editor editorEdit = B(str).edit();
        editorEdit.putString(f329741a.I(i17, i18), str2);
        editorEdit.putInt("key_timestamp", i19);
        editorEdit.apply();
        LogWrapper.info("experience", "CryptManager", "putCacheKeyV2 ver=" + i17 + " mVer=" + i18, new Object[0]);
    }
}