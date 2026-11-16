package zm4;

import com.dragon.read.component.biz.interfaces.NsReaderActivity;
import com.dragon.read.reader.DecryptKey;
import com.dragon.read.reader.download.ChapterInfo;
import com.dragon.reader.lib.ReaderClient;
import com.dragon.reader.lib.datalevel.model.Chapter;
import io.reactivex.Single;
import java.util.List;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import readersaas.com.dragon.read.saas.rpc.model.FullReqType;
import readersaas.com.dragon.read.saas.rpc.model.ItemContent;

/* JADX INFO: loaded from: classes9.dex */
public interface e {
    Single<DecryptKey> a(String str);

    Single<DecryptKey> b(String str, int i17);

    byte[] c(String str, DecryptKey decryptKey);

    int d(String str);

    boolean e();

    String f(ChapterInfo chapterInfo);

    ChapterInfo g(String str, String str2);

    void h(NsReaderActivity nsReaderActivity, String str, String str2);

    Chapter i(ChapterInfo chapterInfo);

    Single<ChapterInfo> j(com.dragon.read.reader.model.d dVar);

    void k(String str, String str2);

    void l(String str, List<String> list, int i17, int i18, int i19);

    Single<ChapterInfo> m(ChapterInfo chapterInfo);

    Single<ChapterInfo> n(ReaderClient readerClient, String str, String str2, FullReqType fullReqType, Function3<? super String, ? super Boolean, ? super ItemContent, Unit> function3);

    void o(String str, String str2);

    boolean p(String str, String str2);

    String q(ChapterInfo chapterInfo, String str);

    String r(String str, DecryptKey decryptKey, boolean z17, String str2, String str3);

    void s(String str, Map<String, String> map);
}