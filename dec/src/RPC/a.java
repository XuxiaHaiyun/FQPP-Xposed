package u56;

import com.bytedance.covode.number.Covode;
import com.bytedance.rpc.annotation.RpcOperation;
import com.bytedance.rpc.annotation.RpcParams;
import com.bytedance.rpc.annotation.RpcSerializer;
import com.bytedance.rpc.m;
import com.bytedance.rpc.serialize.SerializeType;
import io.reactivex.Observable;
import readersaas.com.dragon.read.saas.rpc.model.AddBookmarkRequest;
import readersaas.com.dragon.read.saas.rpc.model.AddBookmarkResponse;
import readersaas.com.dragon.read.saas.rpc.model.AudioTimePointRequest;
import readersaas.com.dragon.read.saas.rpc.model.AudioTimePointResponse;
import readersaas.com.dragon.read.saas.rpc.model.BatchFullRequest;
import readersaas.com.dragon.read.saas.rpc.model.BatchFullResponse;
import readersaas.com.dragon.read.saas.rpc.model.BookDetailRequest;
import readersaas.com.dragon.read.saas.rpc.model.BookDetailResponse;
import readersaas.com.dragon.read.saas.rpc.model.ChapterCorrectionRequest;
import readersaas.com.dragon.read.saas.rpc.model.ChapterCorrectionResponse;
import readersaas.com.dragon.read.saas.rpc.model.DelBookmarkByBookRequest;
import readersaas.com.dragon.read.saas.rpc.model.DelBookmarkByBookResponse;
import readersaas.com.dragon.read.saas.rpc.model.DelBookmarkRequest;
import readersaas.com.dragon.read.saas.rpc.model.DelBookmarkResponse;
import readersaas.com.dragon.read.saas.rpc.model.FullRequest;
import readersaas.com.dragon.read.saas.rpc.model.FullResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetBookHighlightItemsRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetBookHighlightItemsResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetBookcardRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetBookcardRespsonse;
import readersaas.com.dragon.read.saas.rpc.model.GetDirectoryForInfoRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetDirectoryForInfoResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetDirectoryForItemIdRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetDirectoryForItemIdResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetExcerptListRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetExcerptListResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetReadItemListByBookIdRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetReadItemListByBookIdResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetReadProgressRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetReadProgressResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetReaderFontTypesRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetReaderFontTypesResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetReaderSettingsRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetReaderSettingsResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetReaderStrategyRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetReaderStrategyResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetSearchCueRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetSearchCueResponse;
import readersaas.com.dragon.read.saas.rpc.model.GetUserBookDurationRequest;
import readersaas.com.dragon.read.saas.rpc.model.GetUserBookDurationResponse;
import readersaas.com.dragon.read.saas.rpc.model.ListBookmarkRequest;
import readersaas.com.dragon.read.saas.rpc.model.ListBookmarkResponse;
import readersaas.com.dragon.read.saas.rpc.model.MGetReadProgressByBookIdRequest;
import readersaas.com.dragon.read.saas.rpc.model.MGetReadProgressByBookIdResponse;
import readersaas.com.dragon.read.saas.rpc.model.MGetSummaryForItemRequest;
import readersaas.com.dragon.read.saas.rpc.model.MGetSummaryForItemResponse;
import readersaas.com.dragon.read.saas.rpc.model.MultiFullImageRequest;
import readersaas.com.dragon.read.saas.rpc.model.MultiFullImageResponse;
import readersaas.com.dragon.read.saas.rpc.model.PersonalListBookmarkRequest;
import readersaas.com.dragon.read.saas.rpc.model.PersonalListBookmarkResponse;
import readersaas.com.dragon.read.saas.rpc.model.QueryDictionaryRequest;
import readersaas.com.dragon.read.saas.rpc.model.QueryDictionaryResponse;
import readersaas.com.dragon.read.saas.rpc.model.RegisterKeyRequest;
import readersaas.com.dragon.read.saas.rpc.model.RegisterKeyResponse;
import readersaas.com.dragon.read.saas.rpc.model.SearchContentRequest;
import readersaas.com.dragon.read.saas.rpc.model.SearchContentResponse;
import readersaas.com.dragon.read.saas.rpc.model.SyncBookmarkRequest;
import readersaas.com.dragon.read.saas.rpc.model.SyncBookmarkResponse;
import readersaas.com.dragon.read.saas.rpc.model.SyncProgressRateRequest;
import readersaas.com.dragon.read.saas.rpc.model.SyncProgressRateResponse;
import readersaas.com.dragon.read.saas.rpc.model.TransURIRequest;
import readersaas.com.dragon.read.saas.rpc.model.TransURIResponse;
import readersaas.com.dragon.read.saas.rpc.model.TyposFeedbackRequest;
import readersaas.com.dragon.read.saas.rpc.model.TyposFeedbackResponse;
import readersaas.com.dragon.read.saas.rpc.model.UploadProgressRateRequest;
import readersaas.com.dragon.read.saas.rpc.model.UploadProgressRateResponse;
import readersaas.com.dragon.read.saas.rpc.model.UploadReaderSettingsRequest;
import readersaas.com.dragon.read.saas.rpc.model.UploadReaderSettingsResponse;

/* JADX INFO: loaded from: classes16.dex */
public class a {

    /* JADX INFO: renamed from: u56.a$a, reason: collision with other inner class name */
    private interface InterfaceC4409a {
        @RpcOperation("$POST /reading/bookapi/last_page/get_user_book_duration/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetUserBookDurationResponse> A(GetUserBookDurationRequest getUserBookDurationRequest);

        @RpcOperation("$GET /reading/reader/book/highlight/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookHighlightItemsResponse> B(GetBookHighlightItemsRequest getBookHighlightItemsRequest);

        @RpcOperation("$GET /reading/bookapi/directory/all_items/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetDirectoryForItemIdResponse> C(GetDirectoryForItemIdRequest getDirectoryForItemIdRequest);

        @RpcOperation("$GET /reading/reader/strategy/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReaderStrategyResponse> D(GetReaderStrategyRequest getReaderStrategyRequest);

        @RpcOperation("$POST /reading/bookapi/read_progress/upload/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UploadProgressRateResponse> E(UploadProgressRateRequest uploadProgressRateRequest);

        @RpcOperation("$GET /reading/reader/item_summary/mget/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<MGetSummaryForItemResponse> F(MGetSummaryForItemRequest mGetSummaryForItemRequest);

        @RpcOperation("$POST /reading/reader/feedback/typo/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<TyposFeedbackResponse> G(TyposFeedbackRequest typosFeedbackRequest);

        @RpcOperation("$GET /reading/reader/batch_full/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<BatchFullResponse> H(BatchFullRequest batchFullRequest);

        @RpcOperation("$GET /reading/reader/multi_full_image/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<MultiFullImageResponse> a(MultiFullImageRequest multiFullImageRequest);

        @RpcOperation("$POST /reading/reader/bookmark/add/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AddBookmarkResponse> b(AddBookmarkRequest addBookmarkRequest);

        @RpcOperation("$GET /reading/reader/bookmark/list/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ListBookmarkResponse> c(ListBookmarkRequest listBookmarkRequest);

        @RpcOperation("$POST /reading/bookapi/search/reader_content/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SearchContentResponse> d(SearchContentRequest searchContentRequest);

        @RpcOperation("$GET /reading/bookapi/reader_settings/load/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReaderSettingsResponse> e(GetReaderSettingsRequest getReaderSettingsRequest);

        @RpcOperation("$POST /reading/reader/book/chapter/correction/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ChapterCorrectionResponse> f(ChapterCorrectionRequest chapterCorrectionRequest);

        @RpcOperation("$GET /reading/bookapi/detail/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<BookDetailResponse> g(BookDetailRequest bookDetailRequest);

        @RpcOperation("$GET /reading/reader/font/get_font_types/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReaderFontTypesResponse> h(GetReaderFontTypesRequest getReaderFontTypesRequest);

        @RpcOperation("$POST /reading/bookapi/read_progress/sync/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SyncProgressRateResponse> i(SyncProgressRateRequest syncProgressRateRequest);

        @RpcOperation("$POST /reading/bookapi/read_progress/read_item_list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReadItemListByBookIdResponse> j(GetReadItemListByBookIdRequest getReadItemListByBookIdRequest);

        @RpcOperation("$GET /reading/reader/bookmark/personal_list/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<PersonalListBookmarkResponse> k(PersonalListBookmarkRequest personalListBookmarkRequest);

        @RpcOperation("$POST /reading/bookapi/reader_settings/upload/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UploadReaderSettingsResponse> l(UploadReaderSettingsRequest uploadReaderSettingsRequest);

        @RpcOperation("$GET /reading/bookapi/read_progress/list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReadProgressResponse> m(GetReadProgressRequest getReadProgressRequest);

        @RpcOperation("$GET /reading/reader/audio/timepoint/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AudioTimePointResponse> n(AudioTimePointRequest audioTimePointRequest);

        @RpcOperation("$GET /reading/reader/full/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<FullResponse> o(FullRequest fullRequest);

        @RpcOperation("$POST /reading/reader/annotation/get_bookcard/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookcardRespsonse> p(GetBookcardRequest getBookcardRequest);

        @RpcOperation("$GET /reading/bookapi/directory/all_infos/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetDirectoryForInfoResponse> q(GetDirectoryForInfoRequest getDirectoryForInfoRequest);

        @RpcOperation("$POST /reading/bookapi/read_progress/get/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<MGetReadProgressByBookIdResponse> r(MGetReadProgressByBookIdRequest mGetReadProgressByBookIdRequest);

        @RpcOperation("$GET /reading/reader/annotation/query_dictionary/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<QueryDictionaryResponse> s(QueryDictionaryRequest queryDictionaryRequest);

        @RpcOperation("$POST /reading/reader/bookmark/del/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<DelBookmarkResponse> t(DelBookmarkRequest delBookmarkRequest);

        @RpcOperation("$POST /reading/crypt/registerkey")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<RegisterKeyResponse> u(RegisterKeyRequest registerKeyRequest);

        @RpcOperation("$POST /reading/reader/bookmark/del_book/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<DelBookmarkByBookResponse> v(DelBookmarkByBookRequest delBookmarkByBookRequest);

        @RpcOperation("$POST /reading/reader/bookmark/sync/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SyncBookmarkResponse> w(SyncBookmarkRequest syncBookmarkRequest);

        @RpcOperation("$POST /reading/reader/resource/trans_uri/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<TransURIResponse> x(TransURIRequest transURIRequest);

        @RpcOperation("$GET /reading/bookapi/search/cue/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetSearchCueResponse> y(GetSearchCueRequest getSearchCueRequest);

        @RpcOperation("$GET /reading/bookapi/excerpt/list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetExcerptListResponse> z(GetExcerptListRequest getExcerptListRequest);
    }

    static {
        Covode.recordClassIndex(647650);
    }

    private static InterfaceC4409a g() {
        return (InterfaceC4409a) m.f(InterfaceC4409a.class);
    }

    public static Observable<QueryDictionaryResponse> A(QueryDictionaryRequest queryDictionaryRequest) {
        return g().s(queryDictionaryRequest);
    }

    public static Observable<RegisterKeyResponse> B(RegisterKeyRequest registerKeyRequest) {
        return g().u(registerKeyRequest);
    }

    public static Observable<SearchContentResponse> C(SearchContentRequest searchContentRequest) {
        return g().d(searchContentRequest);
    }

    public static Observable<SyncBookmarkResponse> D(SyncBookmarkRequest syncBookmarkRequest) {
        return g().w(syncBookmarkRequest);
    }

    public static Observable<SyncProgressRateResponse> E(SyncProgressRateRequest syncProgressRateRequest) {
        return g().i(syncProgressRateRequest);
    }

    public static Observable<TransURIResponse> F(TransURIRequest transURIRequest) {
        return g().x(transURIRequest);
    }

    public static Observable<TyposFeedbackResponse> G(TyposFeedbackRequest typosFeedbackRequest) {
        return g().G(typosFeedbackRequest);
    }

    public static Observable<UploadProgressRateResponse> H(UploadProgressRateRequest uploadProgressRateRequest) {
        return g().E(uploadProgressRateRequest);
    }

    public static Observable<UploadReaderSettingsResponse> I(UploadReaderSettingsRequest uploadReaderSettingsRequest) {
        return g().l(uploadReaderSettingsRequest);
    }

    public static Observable<AddBookmarkResponse> a(AddBookmarkRequest addBookmarkRequest) {
        return g().b(addBookmarkRequest);
    }

    public static Observable<AudioTimePointResponse> b(AudioTimePointRequest audioTimePointRequest) {
        return g().n(audioTimePointRequest);
    }

    public static Observable<BookDetailResponse> c(BookDetailRequest bookDetailRequest) {
        return g().g(bookDetailRequest);
    }

    public static Observable<ChapterCorrectionResponse> d(ChapterCorrectionRequest chapterCorrectionRequest) {
        return g().f(chapterCorrectionRequest);
    }

    public static Observable<DelBookmarkByBookResponse> e(DelBookmarkByBookRequest delBookmarkByBookRequest) {
        return g().v(delBookmarkByBookRequest);
    }

    public static Observable<DelBookmarkResponse> f(DelBookmarkRequest delBookmarkRequest) {
        return g().t(delBookmarkRequest);
    }

    public static Observable<BatchFullResponse> h(BatchFullRequest batchFullRequest) {
        return g().H(batchFullRequest);
    }

    public static Observable<GetBookHighlightItemsResponse> i(GetBookHighlightItemsRequest getBookHighlightItemsRequest) {
        return g().B(getBookHighlightItemsRequest);
    }

    public static Observable<GetBookcardRespsonse> j(GetBookcardRequest getBookcardRequest) {
        return g().p(getBookcardRequest);
    }

    public static Observable<GetDirectoryForInfoResponse> k(GetDirectoryForInfoRequest getDirectoryForInfoRequest) {
        return g().q(getDirectoryForInfoRequest);
    }

    public static Observable<GetDirectoryForItemIdResponse> l(GetDirectoryForItemIdRequest getDirectoryForItemIdRequest) {
        return g().C(getDirectoryForItemIdRequest);
    }

    public static Observable<GetExcerptListResponse> m(GetExcerptListRequest getExcerptListRequest) {
        return g().z(getExcerptListRequest);
    }

    public static Observable<FullResponse> n(FullRequest fullRequest) {
        return g().o(fullRequest);
    }

    public static Observable<GetReadItemListByBookIdResponse> o(GetReadItemListByBookIdRequest getReadItemListByBookIdRequest) {
        return g().j(getReadItemListByBookIdRequest);
    }

    public static Observable<GetReadProgressResponse> p(GetReadProgressRequest getReadProgressRequest) {
        return g().m(getReadProgressRequest);
    }

    public static Observable<GetReaderFontTypesResponse> q(GetReaderFontTypesRequest getReaderFontTypesRequest) {
        return g().h(getReaderFontTypesRequest);
    }

    public static Observable<GetReaderSettingsResponse> r(GetReaderSettingsRequest getReaderSettingsRequest) {
        return g().e(getReaderSettingsRequest);
    }

    public static Observable<GetReaderStrategyResponse> s(GetReaderStrategyRequest getReaderStrategyRequest) {
        return g().D(getReaderStrategyRequest);
    }

    public static Observable<GetSearchCueResponse> t(GetSearchCueRequest getSearchCueRequest) {
        return g().y(getSearchCueRequest);
    }

    public static Observable<GetUserBookDurationResponse> u(GetUserBookDurationRequest getUserBookDurationRequest) {
        return g().A(getUserBookDurationRequest);
    }

    public static Observable<ListBookmarkResponse> v(ListBookmarkRequest listBookmarkRequest) {
        return g().c(listBookmarkRequest);
    }

    public static Observable<MGetReadProgressByBookIdResponse> w(MGetReadProgressByBookIdRequest mGetReadProgressByBookIdRequest) {
        return g().r(mGetReadProgressByBookIdRequest);
    }

    public static Observable<MGetSummaryForItemResponse> x(MGetSummaryForItemRequest mGetSummaryForItemRequest) {
        return g().F(mGetSummaryForItemRequest);
    }

    public static Observable<MultiFullImageResponse> y(MultiFullImageRequest multiFullImageRequest) {
        return g().a(multiFullImageRequest);
    }

    public static Observable<PersonalListBookmarkResponse> z(PersonalListBookmarkRequest personalListBookmarkRequest) {
        return g().k(personalListBookmarkRequest);
    }
}