package s94;

import android.content.Context;
import com.bytedance.covode.number.Covode;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes7.dex */
public interface s {
    boolean a();

    u b(g gVar);

    u c(Function0<Unit> function0);

    List<l> d(String str);

    u e(Function1<? super p, Unit> function1);

    f f();

    n g();

    Context getContext();

    t getReaderConfig();

    v getUiService();

    void h(u uVar);

    u i(boolean z17, Function0<Unit> function0);

    boolean j();

    boolean k(String str);

    boolean l(String str);

    c m();

    public static final class a {
        static {
            Covode.recordClassIndex(594947);
        }

        public static /* synthetic */ u a(s sVar, boolean z17, Function0 function0, int i17, Object obj) {
            if (obj == null) {
                if ((i17 & 1) != 0) {
                    z17 = false;
                }
                return sVar.i(z17, function0);
            }
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: registerTaskEndListener");
        }
    }
}