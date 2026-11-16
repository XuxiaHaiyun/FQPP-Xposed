package ro4;

import com.bytedance.covode.number.Covode;
import com.bytedance.rpc.annotation.RpcOperation;
import com.bytedance.rpc.annotation.RpcParams;
import com.bytedance.rpc.annotation.RpcSerializer;
import com.bytedance.rpc.m;
import com.bytedance.rpc.serialize.SerializeType;
import com.dragon.read.rpc.model.AddIntoShelfDialogTimeDeltaRequest;
import com.dragon.read.rpc.model.AddIntoShelfDialogTimeDeltaResponse;
import com.dragon.read.rpc.model.AudioPlayInfoRequest;
import com.dragon.read.rpc.model.AudioPlayInfoResponse;
import com.dragon.read.rpc.model.AudioTimePointRequest;
import com.dragon.read.rpc.model.AudioTimePointResponse;
import com.dragon.read.rpc.model.ChapterCorrectionRequest;
import com.dragon.read.rpc.model.ChapterCorrectionResponse;
import com.dragon.read.rpc.model.FullRequest;
import com.dragon.read.rpc.model.FullResponse;
import com.dragon.read.rpc.model.GetBookRecommendComicRequest;
import com.dragon.read.rpc.model.GetBookRecommendComicResponse;
import com.dragon.read.rpc.model.GetBookRecommendDataPlanRequest;
import com.dragon.read.rpc.model.GetBookRecommendDataPlanResponse;
import com.dragon.read.rpc.model.GetComicConfRequest;
import com.dragon.read.rpc.model.GetComicConfResponse;
import com.dragon.read.rpc.model.GetComicItemTailRecommendRequest;
import com.dragon.read.rpc.model.GetComicItemTailRecommendResponse;
import com.dragon.read.rpc.model.GetComicLastPageRecommendRequest;
import com.dragon.read.rpc.model.GetComicLastPageRecommendResponse;
import com.dragon.read.rpc.model.GetLastPageRecommendRequest;
import com.dragon.read.rpc.model.GetLastPageRecommendResponse;
import com.dragon.read.rpc.model.GetReaderFontRequest;
import com.dragon.read.rpc.model.GetReaderFontResponse;
import com.dragon.read.rpc.model.GetRecommendBookRequest;
import com.dragon.read.rpc.model.GetRecommendBookResponse;
import com.dragon.read.rpc.model.LocalReaderRecommendReq;
import com.dragon.read.rpc.model.LocalReaderRecommendResp;
import com.dragon.read.rpc.model.MGetReaderBannerRequest;
import com.dragon.read.rpc.model.MGetReaderBannerResponse;
import com.dragon.read.rpc.model.ReaderAdRewardRequest;
import com.dragon.read.rpc.model.ReaderAdRewardResponse;
import com.dragon.read.rpc.model.ReaderBonusRequest;
import com.dragon.read.rpc.model.ReaderBonusResponse;
import com.dragon.read.rpc.model.RecommendInPossibleLostItemRequest;
import com.dragon.read.rpc.model.RecommendInPossibleLostItemResponse;
import com.dragon.read.rpc.model.TyposFeedbackRequest;
import com.dragon.read.rpc.model.TyposFeedbackResponse;
import com.dragon.read.rpc.model.VideoTimePointRequest;
import com.dragon.read.rpc.model.VideoTimePointResponse;
import io.reactivex.Observable;

/* JADX INFO: loaded from: classes9.dex */
public class f {

    private interface a {
        @RpcOperation("$GET /reading/reader/comic/last_page/recommend/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetComicLastPageRecommendResponse> a(GetComicLastPageRecommendRequest getComicLastPageRecommendRequest);

        @RpcOperation("$GET /reading/reader/book/recommend_comic/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookRecommendComicResponse> b(GetBookRecommendComicRequest getBookRecommendComicRequest);

        @RpcOperation("$POST /reading/reader/feedback/typo/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<TyposFeedbackResponse> c(TyposFeedbackRequest typosFeedbackRequest);

        @RpcOperation("$GET /reading/reader/comic/conf/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetComicConfResponse> d(GetComicConfRequest getComicConfRequest);

        @RpcOperation("$GET /reading/reader/book/lastpage/recommend/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetLastPageRecommendResponse> e(GetLastPageRecommendRequest getLastPageRecommendRequest);

        @RpcOperation("$GET /reading/reader/recommend/book/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetRecommendBookResponse> f(GetRecommendBookRequest getRecommendBookRequest);

        @RpcOperation("$GET /reading/reader/banner/mget/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<MGetReaderBannerResponse> g(MGetReaderBannerRequest mGetReaderBannerRequest);

        @RpcOperation("$GET /reading/reader/bonus/ad_reward/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ReaderAdRewardResponse> h(ReaderAdRewardRequest readerAdRewardRequest);

        @RpcOperation("$GET /reading/reader/audio/playinfo/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AudioPlayInfoResponse> i(AudioPlayInfoRequest audioPlayInfoRequest);

        @RpcOperation("$GET /reading/reader/video/timepoint/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<VideoTimePointResponse> j(VideoTimePointRequest videoTimePointRequest);

        @RpcOperation("$GET /reading/reader/full/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<FullResponse> k(FullRequest fullRequest);

        @RpcOperation("$POST /reading/reader/book/chapter/correction/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ChapterCorrectionResponse> l(ChapterCorrectionRequest chapterCorrectionRequest);

        @RpcOperation("$GET /reading/reader/book/recommend_data_plan/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookRecommendDataPlanResponse> m(GetBookRecommendDataPlanRequest getBookRecommendDataPlanRequest);

        @RpcOperation("$GET /reading/reader/recommend/lost_item/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<RecommendInPossibleLostItemResponse> n(RecommendInPossibleLostItemRequest recommendInPossibleLostItemRequest);

        @RpcOperation("$GET /reading/reader/bonus/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ReaderBonusResponse> o(ReaderBonusRequest readerBonusRequest);

        @RpcOperation("$GET /reading/reader/comic/item_tail/recommend/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetComicItemTailRecommendResponse> p(GetComicItemTailRecommendRequest getComicItemTailRecommendRequest);

        @RpcOperation("$GET /reading/reader/dialog/addinto_bookshelf/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AddIntoShelfDialogTimeDeltaResponse> q(AddIntoShelfDialogTimeDeltaRequest addIntoShelfDialogTimeDeltaRequest);

        @RpcOperation("$GET /reading/reader/font/get/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReaderFontResponse> r(GetReaderFontRequest getReaderFontRequest);

        @RpcOperation("$GET /reading/reader/audio/timepoint/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AudioTimePointResponse> s(AudioTimePointRequest audioTimePointRequest);

        @RpcOperation("$GET /reading/reader/recommend/local_reader/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<LocalReaderRecommendResp> t(LocalReaderRecommendReq localReaderRecommendReq);
    }

    static {
        Covode.recordClassIndex(612442);
    }

    private static a e() {
        return (a) m.f(a.class);
    }

    public static Observable<AddIntoShelfDialogTimeDeltaResponse> a(AddIntoShelfDialogTimeDeltaRequest addIntoShelfDialogTimeDeltaRequest) {
        return e().q(addIntoShelfDialogTimeDeltaRequest);
    }

    public static Observable<AudioPlayInfoResponse> b(AudioPlayInfoRequest audioPlayInfoRequest) {
        return e().i(audioPlayInfoRequest);
    }

    public static Observable<AudioTimePointResponse> c(AudioTimePointRequest audioTimePointRequest) {
        return e().s(audioTimePointRequest);
    }

    public static Observable<ChapterCorrectionResponse> d(ChapterCorrectionRequest chapterCorrectionRequest) {
        return e().l(chapterCorrectionRequest);
    }

    public static Observable<GetBookRecommendComicResponse> f(GetBookRecommendComicRequest getBookRecommendComicRequest) {
        return e().b(getBookRecommendComicRequest);
    }

    public static Observable<GetBookRecommendDataPlanResponse> g(GetBookRecommendDataPlanRequest getBookRecommendDataPlanRequest) {
        return e().m(getBookRecommendDataPlanRequest);
    }

    public static Observable<GetComicConfResponse> h(GetComicConfRequest getComicConfRequest) {
        return e().d(getComicConfRequest);
    }

    public static Observable<GetComicItemTailRecommendResponse> i(GetComicItemTailRecommendRequest getComicItemTailRecommendRequest) {
        return e().p(getComicItemTailRecommendRequest);
    }

    public static Observable<GetComicLastPageRecommendResponse> j(GetComicLastPageRecommendRequest getComicLastPageRecommendRequest) {
        return e().a(getComicLastPageRecommendRequest);
    }

    public static Observable<FullResponse> k(FullRequest fullRequest) {
        return e().k(fullRequest);
    }

    public static Observable<GetLastPageRecommendResponse> l(GetLastPageRecommendRequest getLastPageRecommendRequest) {
        return e().e(getLastPageRecommendRequest);
    }

    public static Observable<GetReaderFontResponse> m(GetReaderFontRequest getReaderFontRequest) {
        return e().r(getReaderFontRequest);
    }

    public static Observable<GetRecommendBookResponse> n(GetRecommendBookRequest getRecommendBookRequest) {
        return e().f(getRecommendBookRequest);
    }

    public static Observable<LocalReaderRecommendResp> o(LocalReaderRecommendReq localReaderRecommendReq) {
        return e().t(localReaderRecommendReq);
    }

    public static Observable<MGetReaderBannerResponse> p(MGetReaderBannerRequest mGetReaderBannerRequest) {
        return e().g(mGetReaderBannerRequest);
    }

    public static Observable<ReaderAdRewardResponse> q(ReaderAdRewardRequest readerAdRewardRequest) {
        return e().h(readerAdRewardRequest);
    }

    public static Observable<ReaderBonusResponse> r(ReaderBonusRequest readerBonusRequest) {
        return e().o(readerBonusRequest);
    }

    public static Observable<RecommendInPossibleLostItemResponse> s(RecommendInPossibleLostItemRequest recommendInPossibleLostItemRequest) {
        return e().n(recommendInPossibleLostItemRequest);
    }

    public static Observable<TyposFeedbackResponse> t(TyposFeedbackRequest typosFeedbackRequest) {
        return e().c(typosFeedbackRequest);
    }

    public static Observable<VideoTimePointResponse> u(VideoTimePointRequest videoTimePointRequest) {
        return e().j(videoTimePointRequest);
    }
}