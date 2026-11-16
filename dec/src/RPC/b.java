package ro4;

import com.bytedance.covode.number.Covode;
import com.bytedance.rpc.annotation.RpcOperation;
import com.bytedance.rpc.annotation.RpcParams;
import com.bytedance.rpc.annotation.RpcSerializer;
import com.bytedance.rpc.m;
import com.bytedance.rpc.serialize.SerializeType;
import com.dragon.read.rpc.model.AIContentFeedbackRequest;
import com.dragon.read.rpc.model.AIContentFeedbackResponse;
import com.dragon.read.rpc.model.AIGuessYourQueryRequest;
import com.dragon.read.rpc.model.AIGuessYourQueryResponse;
import com.dragon.read.rpc.model.ActivatePlanRequest;
import com.dragon.read.rpc.model.ActivatePlanResponse;
import com.dragon.read.rpc.model.ActiveSchemaReportRequest;
import com.dragon.read.rpc.model.ActiveSchemaReportResponse;
import com.dragon.read.rpc.model.AddBookShelfInfoRequest;
import com.dragon.read.rpc.model.AddBookShelfInfoResponse;
import com.dragon.read.rpc.model.AddVideoEpisodeRequest;
import com.dragon.read.rpc.model.AddVideoEpisodeResponse;
import com.dragon.read.rpc.model.AudioDetailRequest;
import com.dragon.read.rpc.model.AudioDetailResponse;
import com.dragon.read.rpc.model.AudioPlayURLRequest;
import com.dragon.read.rpc.model.AudioPlayURLResponse;
import com.dragon.read.rpc.model.BatchAudioPlayURLRequest;
import com.dragon.read.rpc.model.BatchAudioPlayURLResponse;
import com.dragon.read.rpc.model.BookDetailRequest;
import com.dragon.read.rpc.model.BookDetailResponse;
import com.dragon.read.rpc.model.BookItemContentUnlockRequest;
import com.dragon.read.rpc.model.BookItemContentUnlockResponse;
import com.dragon.read.rpc.model.BookToneInfoRequest;
import com.dragon.read.rpc.model.BookToneInfoResponse;
import com.dragon.read.rpc.model.BookstoreTabRequest;
import com.dragon.read.rpc.model.BookstoreTabResponse;
import com.dragon.read.rpc.model.CheckLocalBookChapterRequest;
import com.dragon.read.rpc.model.CheckLocalBookChapterResponse;
import com.dragon.read.rpc.model.ClientReportRequest;
import com.dragon.read.rpc.model.ClientReportResponse;
import com.dragon.read.rpc.model.ComicDetailRequest;
import com.dragon.read.rpc.model.ComicDetailResponse;
import com.dragon.read.rpc.model.CommonAbResultRequest;
import com.dragon.read.rpc.model.CommonAbResultResponse;
import com.dragon.read.rpc.model.DeleteBookShelfInfoRequest;
import com.dragon.read.rpc.model.DeleteBookShelfInfoResponse;
import com.dragon.read.rpc.model.GetActivityMainPageRequest;
import com.dragon.read.rpc.model.GetActivityMainPageResponse;
import com.dragon.read.rpc.model.GetAudioSettingsRequest;
import com.dragon.read.rpc.model.GetAudioSettingsResponse;
import com.dragon.read.rpc.model.GetBizStaticConfigRequest;
import com.dragon.read.rpc.model.GetBizStaticConfigResponse;
import com.dragon.read.rpc.model.GetBookExtraRequest;
import com.dragon.read.rpc.model.GetBookExtraResponse;
import com.dragon.read.rpc.model.GetBookMallCellChangeRequest;
import com.dragon.read.rpc.model.GetBookMallCellChangeResponse;
import com.dragon.read.rpc.model.GetBookMallHomePageResponse;
import com.dragon.read.rpc.model.GetBookPayDetailRequest;
import com.dragon.read.rpc.model.GetBookPayDetailResponse;
import com.dragon.read.rpc.model.GetBookProfitMakingStrategyRequest;
import com.dragon.read.rpc.model.GetBookProfitMakingStrategyResponse;
import com.dragon.read.rpc.model.GetBookShelfBannerRequest;
import com.dragon.read.rpc.model.GetBookShelfBannerResponse;
import com.dragon.read.rpc.model.GetBookShelfInfoRequest;
import com.dragon.read.rpc.model.GetBookShelfInfoResponse;
import com.dragon.read.rpc.model.GetBookShelfListRequest;
import com.dragon.read.rpc.model.GetBookShelfListResponse;
import com.dragon.read.rpc.model.GetBookShelfVideoInfoRequest;
import com.dragon.read.rpc.model.GetBookShelfVideoInfoResponse;
import com.dragon.read.rpc.model.GetBookshelfTabRequest;
import com.dragon.read.rpc.model.GetBookshelfTabResponse;
import com.dragon.read.rpc.model.GetCategoryCellDataRequest;
import com.dragon.read.rpc.model.GetCategoryCellDataResponse;
import com.dragon.read.rpc.model.GetChaseBookRequest;
import com.dragon.read.rpc.model.GetChaseBookResponse;
import com.dragon.read.rpc.model.GetCommerceTagInfoRequest;
import com.dragon.read.rpc.model.GetCommerceTagInfoResponse;
import com.dragon.read.rpc.model.GetDirectoryForInfoRequest;
import com.dragon.read.rpc.model.GetDirectoryForInfoResponse;
import com.dragon.read.rpc.model.GetDirectoryForItemIdRequest;
import com.dragon.read.rpc.model.GetDirectoryForItemIdResponse;
import com.dragon.read.rpc.model.GetExcerptListRequest;
import com.dragon.read.rpc.model.GetExcerptListResponse;
import com.dragon.read.rpc.model.GetGameHistoryRequest;
import com.dragon.read.rpc.model.GetGameHistoryResponse;
import com.dragon.read.rpc.model.GetGuideInfoRequest;
import com.dragon.read.rpc.model.GetGuideInfoResponse;
import com.dragon.read.rpc.model.GetIMCResourceRequest;
import com.dragon.read.rpc.model.GetIMCResourceResponse;
import com.dragon.read.rpc.model.GetLotteryDetailRequest;
import com.dragon.read.rpc.model.GetLotteryDetailResponse;
import com.dragon.read.rpc.model.GetMallBenefitRequest;
import com.dragon.read.rpc.model.GetMallBenefitResponse;
import com.dragon.read.rpc.model.GetMsgSettingsRequest;
import com.dragon.read.rpc.model.GetMsgSettingsResponse;
import com.dragon.read.rpc.model.GetMyTabPlanRequest;
import com.dragon.read.rpc.model.GetMyTabPlanResponse;
import com.dragon.read.rpc.model.GetNewCategoryFrontPageRequest;
import com.dragon.read.rpc.model.GetNewCategoryFrontPageResponse;
import com.dragon.read.rpc.model.GetNewCategoryLandingPageRequest;
import com.dragon.read.rpc.model.GetNewCategoryLandingPageResponse;
import com.dragon.read.rpc.model.GetPlanRequest;
import com.dragon.read.rpc.model.GetReadEncourageRequest;
import com.dragon.read.rpc.model.GetReadEncourageResponse;
import com.dragon.read.rpc.model.GetReadHistoryRequest;
import com.dragon.read.rpc.model.GetReadHistoryResponse;
import com.dragon.read.rpc.model.GetReadProgressRequest;
import com.dragon.read.rpc.model.GetReadProgressResponse;
import com.dragon.read.rpc.model.GetReadStatDetailRequest;
import com.dragon.read.rpc.model.GetReadStatDetailResponse;
import com.dragon.read.rpc.model.GetReaderBannerResourceRequest;
import com.dragon.read.rpc.model.GetReaderBannerResourceResponse;
import com.dragon.read.rpc.model.GetRedDotRequest;
import com.dragon.read.rpc.model.GetRedDotResponse;
import com.dragon.read.rpc.model.GetSSTimorEntryInfoRequest;
import com.dragon.read.rpc.model.GetSSTimorEntryInfoResponse;
import com.dragon.read.rpc.model.GetSearchCellChangeRequest;
import com.dragon.read.rpc.model.GetSearchCellChangeResponse;
import com.dragon.read.rpc.model.GetSearchCueRequest;
import com.dragon.read.rpc.model.GetSearchCueResponse;
import com.dragon.read.rpc.model.GetSearchPageRequest;
import com.dragon.read.rpc.model.GetSearchTabDataResponse;
import com.dragon.read.rpc.model.GetSearchUserInfoRequest;
import com.dragon.read.rpc.model.GetSearchUserInfoResponse;
import com.dragon.read.rpc.model.GetShelfLocalBookListRequest;
import com.dragon.read.rpc.model.GetShelfLocalBookListResponse;
import com.dragon.read.rpc.model.GetSnackBarInShelfRequset;
import com.dragon.read.rpc.model.GetSnackBarInShelfResponse;
import com.dragon.read.rpc.model.GetUserDelBookshelfRequest;
import com.dragon.read.rpc.model.GetUserDelBookshelfResponse;
import com.dragon.read.rpc.model.GetUserPublishVipToastRequest;
import com.dragon.read.rpc.model.GetUserPublishVipToastResponse;
import com.dragon.read.rpc.model.GetUserResearchRequest;
import com.dragon.read.rpc.model.GetUserResearchResponse;
import com.dragon.read.rpc.model.GetVideoContinueWatchAbRequest;
import com.dragon.read.rpc.model.GetVideoContinueWatchAbResponse;
import com.dragon.read.rpc.model.GetVideoModelRequest;
import com.dragon.read.rpc.model.GetVideoModelResponse;
import com.dragon.read.rpc.model.GetVideoTabAbResultRequest;
import com.dragon.read.rpc.model.GetVideoTabAbResultResponse;
import com.dragon.read.rpc.model.GetWidgetsBookRequest;
import com.dragon.read.rpc.model.GetWidgetsBookResponse;
import com.dragon.read.rpc.model.MBookDetailRequest;
import com.dragon.read.rpc.model.MBookDetailResponse;
import com.dragon.read.rpc.model.MGetReadProgressByBookIdRequest;
import com.dragon.read.rpc.model.MGetReadProgressByBookIdResponse;
import com.dragon.read.rpc.model.MGetVideoDataRequest;
import com.dragon.read.rpc.model.MGetVideoDataResponse;
import com.dragon.read.rpc.model.MGetVideoDetailResponse;
import com.dragon.read.rpc.model.QueryBookUpdateRequest;
import com.dragon.read.rpc.model.QueryBookUpdateResponse;
import com.dragon.read.rpc.model.ReadHistoryRecommendRequest;
import com.dragon.read.rpc.model.ReadHistoryRecommendResponse;
import com.dragon.read.rpc.model.RecommendLogReportRequest;
import com.dragon.read.rpc.model.RecommendLogReportResponse;
import com.dragon.read.rpc.model.RefreshReader1ColStrategyRequest;
import com.dragon.read.rpc.model.RefreshReader1ColStrategyResponse;
import com.dragon.read.rpc.model.RelatedBookRequest;
import com.dragon.read.rpc.model.RelatedBookResponse;
import com.dragon.read.rpc.model.ReportBookInfoRequest;
import com.dragon.read.rpc.model.ReportBookInfoResponse;
import com.dragon.read.rpc.model.SSTimorCellInfoRequest;
import com.dragon.read.rpc.model.SSTimorCellInfoResponse;
import com.dragon.read.rpc.model.SSTimorEntryTypeRequest;
import com.dragon.read.rpc.model.SSTimorEntryTypeResponse;
import com.dragon.read.rpc.model.SSTimorTabRequest;
import com.dragon.read.rpc.model.SSTimorTabResponse;
import com.dragon.read.rpc.model.SSTimorTimeRequest;
import com.dragon.read.rpc.model.SSTimorTimeResponse;
import com.dragon.read.rpc.model.SearchSchemaLandingRequest;
import com.dragon.read.rpc.model.SearchSchemaLandingResponse;
import com.dragon.read.rpc.model.SearchUncoverSubscribeRequest;
import com.dragon.read.rpc.model.SearchUncoverSubscribeResponse;
import com.dragon.read.rpc.model.SubscribeUncopyrightedBookRequest;
import com.dragon.read.rpc.model.SubscribeUncopyrightedBookResponse;
import com.dragon.read.rpc.model.SuggestRequest;
import com.dragon.read.rpc.model.SuggestResponse;
import com.dragon.read.rpc.model.SurlRecommendRequest;
import com.dragon.read.rpc.model.SurlRecommendResponse;
import com.dragon.read.rpc.model.SurlRequest;
import com.dragon.read.rpc.model.SurlResponse;
import com.dragon.read.rpc.model.TopicSuggestRequest;
import com.dragon.read.rpc.model.TopicSuggestResponse;
import com.dragon.read.rpc.model.UpdateBookShelfInfoRequest;
import com.dragon.read.rpc.model.UpdateBookShelfInfoResponse;
import com.dragon.read.rpc.model.UpdateBookShelfVideoInfoRequest;
import com.dragon.read.rpc.model.UpdateBookShelfVideoInfoResponse;
import com.dragon.read.rpc.model.UpdateChaseBookRequest;
import com.dragon.read.rpc.model.UpdateChaseBookResponse;
import com.dragon.read.rpc.model.UpdateGameHistoryRequest;
import com.dragon.read.rpc.model.UpdateGameHistoryResponse;
import com.dragon.read.rpc.model.UpdateReadHistoryRequest;
import com.dragon.read.rpc.model.UpdateReadHistoryResponse;
import com.dragon.read.rpc.model.UpdateShelfLocalBookListRequest;
import com.dragon.read.rpc.model.UpdateShelfLocalBookListResponse;
import com.dragon.read.rpc.model.UploadAncientBooksProgressRequest;
import com.dragon.read.rpc.model.UploadAncientBooksProgressResponse;
import com.dragon.read.rpc.model.UploadAudioSettingsRequest;
import com.dragon.read.rpc.model.UploadAudioSettingsResponse;
import com.dragon.read.rpc.model.UploadLocalBookChapterRequest;
import com.dragon.read.rpc.model.UploadLocalBookChapterResponse;
import com.dragon.read.rpc.model.UploadMsgSettingsRequest;
import com.dragon.read.rpc.model.UploadMsgSettingsResponse;
import com.dragon.read.rpc.model.UploadProgressRateRequest;
import com.dragon.read.rpc.model.UploadProgressRateResponse;
import com.dragon.read.rpc.model.UploadSelfTabSortRequest;
import com.dragon.read.rpc.model.UploadSelfTabSortResponse;
import com.dragon.read.rpc.model.VideoDetailRequest;
import com.dragon.read.rpc.model.VideoDetailResponse;
import io.reactivex.Observable;

/* JADX INFO: loaded from: classes9.dex */
public class b {

    private interface a {
        @RpcOperation("$POST /reading/bookapi/ai_dict/content_feedback/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AIContentFeedbackResponse> A(AIContentFeedbackRequest aIContentFeedbackRequest);

        @RpcOperation("$GET /reading/bookapi/video_tab/video_model/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetVideoModelResponse> A0(GetVideoModelRequest getVideoModelRequest);

        @RpcOperation("$GET /reading/bookapi/bookmall/tab/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<BookstoreTabResponse> B(BookstoreTabRequest bookstoreTabRequest);

        @RpcOperation("$GET /reading/bookapi/search/user-info/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetSearchUserInfoResponse> B0(GetSearchUserInfoRequest getSearchUserInfoRequest);

        @RpcOperation("$GET /reading/bookapi/detail/related_book/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<RelatedBookResponse> C(RelatedBookRequest relatedBookRequest);

        @RpcOperation("$POST /reading/bookapi/bookshelf/add/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AddBookShelfInfoResponse> C0(AddBookShelfInfoRequest addBookShelfInfoRequest);

        @RpcOperation("$GET /reading/bookapi/lottery/detail/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetLotteryDetailResponse> D(GetLotteryDetailRequest getLotteryDetailRequest);

        @RpcOperation("$GET /reading/bookapi/my_tab/plan/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetMyTabPlanResponse> D0(GetMyTabPlanRequest getMyTabPlanRequest);

        @RpcOperation("$GET /reading/bookapi/profit/making/strategy/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookProfitMakingStrategyResponse> E(GetBookProfitMakingStrategyRequest getBookProfitMakingStrategyRequest);

        @RpcOperation("$POST /reading/bookapi/bookshelf/local_book/update/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UpdateShelfLocalBookListResponse> E0(UpdateShelfLocalBookListRequest updateShelfLocalBookListRequest);

        @RpcOperation("$GET /reading/bookapi/bookshelf/local_book/list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetShelfLocalBookListResponse> F(GetShelfLocalBookListRequest getShelfLocalBookListRequest);

        @RpcOperation("$POST /reading/bookapi/bookshelf/delete/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<DeleteBookShelfInfoResponse> F0(DeleteBookShelfInfoRequest deleteBookShelfInfoRequest);

        @RpcOperation("$GET /reading/bookapi/read_stat_detail/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReadStatDetailResponse> G(GetReadStatDetailRequest getReadStatDetailRequest);

        @RpcOperation("$GET /reading/bookapi/widgets/book/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetWidgetsBookResponse> G0(GetWidgetsBookRequest getWidgetsBookRequest);

        @RpcOperation("$POST /reading/bookapi/read_progress/get/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<MGetReadProgressByBookIdResponse> H(MGetReadProgressByBookIdRequest mGetReadProgressByBookIdRequest);

        @RpcOperation("$GET /reading/bookapi/search/tab/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetSearchTabDataResponse> H0(GetSearchPageRequest getSearchPageRequest);

        @RpcOperation("$GET /reading/bookapi/search/cue/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetSearchCueResponse> I(GetSearchCueRequest getSearchCueRequest);

        @RpcOperation("$POST /reading/bookapi/video/episode/add/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AddVideoEpisodeResponse> I0(AddVideoEpisodeRequest addVideoEpisodeRequest);

        @RpcOperation("$POST /reading/bookapi/surl/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SurlResponse> J(SurlRequest surlRequest);

        @RpcOperation("$GET /reading/bookapi/new_category/cell/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetCategoryCellDataResponse> J0(GetCategoryCellDataRequest getCategoryCellDataRequest);

        @RpcOperation("$GET /reading/bookapi/audio/toneinfo/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<BookToneInfoResponse> K(BookToneInfoRequest bookToneInfoRequest);

        @RpcOperation("$GET /reading/bookapi/biz_static_config/get/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBizStaticConfigResponse> K0(GetBizStaticConfigRequest getBizStaticConfigRequest);

        @RpcOperation("$POST /reading/bookapi/bookmall/recommend_log_report/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<RecommendLogReportResponse> L(RecommendLogReportRequest recommendLogReportRequest);

        @RpcOperation("$POST /reading/bookapi/activate_plan/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ActivatePlanResponse> L0(ActivatePlanRequest activatePlanRequest);

        @RpcOperation("$GET /reading/bookapi/book_extra/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookExtraResponse> M(GetBookExtraRequest getBookExtraRequest);

        @RpcOperation("$POST /reading/bookapi/bookshelf/banner/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookShelfBannerResponse> M0(GetBookShelfBannerRequest getBookShelfBannerRequest);

        @RpcOperation("$GET /reading/bookapi/sstimor/entry_type/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SSTimorEntryTypeResponse> N(SSTimorEntryTypeRequest sSTimorEntryTypeRequest);

        @RpcOperation("$GET /reading/bookapi/read_progress/recommend/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ReadHistoryRecommendResponse> N0(ReadHistoryRecommendRequest readHistoryRecommendRequest);

        @RpcOperation("$GET /reading/bookapi/read_history/list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReadHistoryResponse> O(GetReadHistoryRequest getReadHistoryRequest);

        @RpcOperation("$GET /reading/bookapi/directory/all_infos/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetDirectoryForInfoResponse> O0(GetDirectoryForInfoRequest getDirectoryForInfoRequest);

        @RpcOperation("$GET /reading/bookapi/commerce/tag_info/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetCommerceTagInfoResponse> P(GetCommerceTagInfoRequest getCommerceTagInfoRequest);

        @RpcOperation("$GET /reading/bookapi/read_progress/list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReadProgressResponse> P0(GetReadProgressRequest getReadProgressRequest);

        @RpcOperation("$POST /reading/bookapi/msg_settings/upload/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UploadMsgSettingsResponse> Q(UploadMsgSettingsRequest uploadMsgSettingsRequest);

        @RpcOperation("$GET /reading/bookapi/mall_benefit/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetMallBenefitResponse> Q0(GetMallBenefitRequest getMallBenefitRequest);

        @RpcOperation("$GET /reading/bookapi/sstimor/tab/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SSTimorTabResponse> R(SSTimorTabRequest sSTimorTabRequest);

        @RpcOperation("$GET /reading/bookapi/search/suggest/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SuggestResponse> R0(SuggestRequest suggestRequest);

        @RpcOperation("$POST /reading/bookapi/user/pay_detail/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookPayDetailResponse> S(GetBookPayDetailRequest getBookPayDetailRequest);

        @RpcOperation("$GET /reading/bookapi/sstimor/time_info/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SSTimorTimeResponse> S0(SSTimorTimeRequest sSTimorTimeRequest);

        @RpcOperation("$POST /reading/bookapi/active_schema_report/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ActiveSchemaReportResponse> T(ActiveSchemaReportRequest activeSchemaReportRequest);

        @RpcOperation("$GET /reading/bookapi/search/schema_landing/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SearchSchemaLandingResponse> T0(SearchSchemaLandingRequest searchSchemaLandingRequest);

        @RpcOperation("$GET /reading/bookapi/imc/resource/get/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetIMCResourceResponse> U(GetIMCResourceRequest getIMCResourceRequest);

        @RpcOperation("$GET /reading/bookapi/local_book/check")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<CheckLocalBookChapterResponse> U0(CheckLocalBookChapterRequest checkLocalBookChapterRequest);

        @RpcOperation("$GET /reading/bookapi/new_category/front/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetNewCategoryFrontPageResponse> V(GetNewCategoryFrontPageRequest getNewCategoryFrontPageRequest);

        @RpcOperation("$POST /reading/bookapi/read_history/update/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UpdateReadHistoryResponse> V0(UpdateReadHistoryRequest updateReadHistoryRequest);

        @RpcOperation("$GET /reading/bookapi/new_category/landing/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetNewCategoryLandingPageResponse> W(GetNewCategoryLandingPageRequest getNewCategoryLandingPageRequest);

        @RpcOperation("$GET /reading/bookapi/common_ab_result/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<CommonAbResultResponse> X(CommonAbResultRequest commonAbResultRequest);

        @RpcOperation("$GET /reading/bookapi/audio/detail/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AudioDetailResponse> Y(AudioDetailRequest audioDetailRequest);

        @RpcOperation("$GET /reading/bookapi/sstimor/cell_info/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SSTimorCellInfoResponse> Z(SSTimorCellInfoRequest sSTimorCellInfoRequest);

        @RpcOperation("$GET /reading/bookapi/bookshelf/list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookShelfListResponse> a(GetBookShelfListRequest getBookShelfListRequest);

        @RpcOperation("$POST /reading/bookapi/bookshelf/video/update/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UpdateBookShelfVideoInfoResponse> a0(UpdateBookShelfVideoInfoRequest updateBookShelfVideoInfoRequest);

        @RpcOperation("$GET /reading/bookapi/book_update/query/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<QueryBookUpdateResponse> b(QueryBookUpdateRequest queryBookUpdateRequest);

        @RpcOperation("$POST /reading/bookapi/publish/uncopyrighted_book/subscribe/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SubscribeUncopyrightedBookResponse> b0(SubscribeUncopyrightedBookRequest subscribeUncopyrightedBookRequest);

        @RpcOperation("$POST /reading/bookapi/audio/batch_play/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<BatchAudioPlayURLResponse> c(BatchAudioPlayURLRequest batchAudioPlayURLRequest);

        @RpcOperation("$GET /reading/bookapi/bookshelf/video/info/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookShelfVideoInfoResponse> c0(GetBookShelfVideoInfoRequest getBookShelfVideoInfoRequest);

        @RpcOperation("$GET /reading/bookapi/bookshelf/info/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookShelfInfoResponse> d(GetBookShelfInfoRequest getBookShelfInfoRequest);

        @RpcOperation("$GET /reading/bookapi/detail/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<BookDetailResponse> d0(BookDetailRequest bookDetailRequest);

        @RpcOperation("$GET /reading/bookapi/plan/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookMallHomePageResponse> e(GetPlanRequest getPlanRequest);

        @RpcOperation("$GET /reading/bookapi/bookshelf/tab/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookshelfTabResponse> e0(GetBookshelfTabRequest getBookshelfTabRequest);

        @RpcOperation("$GET /reading/bookapi/bookshelf/del/info/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetUserDelBookshelfResponse> f(GetUserDelBookshelfRequest getUserDelBookshelfRequest);

        @RpcOperation("$GET /reading/bookapi/red_dot/get/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetRedDotResponse> f0(GetRedDotRequest getRedDotRequest);

        @RpcOperation("$POST /reading/bookapi/audio/playurl/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AudioPlayURLResponse> g(AudioPlayURLRequest audioPlayURLRequest);

        @RpcOperation("$GET /reading/bookapi/search/cell/change/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetSearchCellChangeResponse> g0(GetSearchCellChangeRequest getSearchCellChangeRequest);

        @RpcOperation("$GET /reading/bookapi/video_tab/multi_video_detail/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<MGetVideoDetailResponse> h(VideoDetailRequest videoDetailRequest);

        @RpcOperation("$GET /reading/bookapi/ai_dict/guess_your_query/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AIGuessYourQueryResponse> h0(AIGuessYourQueryRequest aIGuessYourQueryRequest);

        @RpcOperation("$GET /reading/bookapi/bookmall/cell/change/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetBookMallCellChangeResponse> i(GetBookMallCellChangeRequest getBookMallCellChangeRequest);

        @RpcOperation("$POST /reading/bookapi/guide_info/get/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetGuideInfoResponse> i0(GetGuideInfoRequest getGuideInfoRequest);

        @RpcOperation("$POST /reading/bookapi/search/uncover_subscribe/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SearchUncoverSubscribeResponse> j(SearchUncoverSubscribeRequest searchUncoverSubscribeRequest);

        @RpcOperation("$GET /reading/bookapi/game_history/list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetGameHistoryResponse> j0(GetGameHistoryRequest getGameHistoryRequest);

        @RpcOperation("$POST /reading/bookapi/chase_book/update/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UpdateChaseBookResponse> k(UpdateChaseBookRequest updateChaseBookRequest);

        @RpcOperation("$POST /reading/bookapi/game_history/update/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UpdateGameHistoryResponse> k0(UpdateGameHistoryRequest updateGameHistoryRequest);

        @RpcOperation("$POST /reading/bookapi/multi_video_data/get/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<MGetVideoDataResponse> l(MGetVideoDataRequest mGetVideoDataRequest);

        @RpcOperation("$GET /reading/bookapi/bookshelf/snack_bar/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetSnackBarInShelfResponse> l0(GetSnackBarInShelfRequset getSnackBarInShelfRequset);

        @RpcOperation("$POST /reading/bookapi/audio_settings/upload/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UploadAudioSettingsResponse> m(UploadAudioSettingsRequest uploadAudioSettingsRequest);

        @RpcOperation("$GET /reading/bookapi/multi-detail/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<MBookDetailResponse> m0(MBookDetailRequest mBookDetailRequest);

        @RpcOperation("$GET /reading/bookapi/video_tab/continue_watch_ab/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetVideoContinueWatchAbResponse> n(GetVideoContinueWatchAbRequest getVideoContinueWatchAbRequest);

        @RpcOperation("$GET /reading/bookapi/search/topic/suggest/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<TopicSuggestResponse> n0(TopicSuggestRequest topicSuggestRequest);

        @RpcOperation("$GET /reading/bookapi/refresh_reader_1col_strategy/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<RefreshReader1ColStrategyResponse> o(RefreshReader1ColStrategyRequest refreshReader1ColStrategyRequest);

        @RpcOperation("$POST /reading/bookapi/read_progress/upload/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UploadProgressRateResponse> o0(UploadProgressRateRequest uploadProgressRateRequest);

        @RpcOperation("$GET /reading/bookapi/surl/recommend/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SurlRecommendResponse> p(SurlRecommendRequest surlRecommendRequest);

        @RpcOperation("$POST /reading/bookapi/bookname/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ReportBookInfoResponse> p0(ReportBookInfoRequest reportBookInfoRequest);

        @RpcOperation("$POST /reading/bookapi/item/content/unlock/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<BookItemContentUnlockResponse> q(BookItemContentUnlockRequest bookItemContentUnlockRequest);

        @RpcOperation("$GET /reading/bookapi/video_tab/video_detail/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<VideoDetailResponse> q0(VideoDetailRequest videoDetailRequest);

        @RpcOperation("$GET /reading/bookapi/msg_settings/load/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetMsgSettingsResponse> r(GetMsgSettingsRequest getMsgSettingsRequest);

        @RpcOperation("$GET /reading/bookapi/user/publish_vip_toast/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetUserPublishVipToastResponse> r0(GetUserPublishVipToastRequest getUserPublishVipToastRequest);

        @RpcOperation("$POST /reading/bookapi/bookmall/tab/sort/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UploadSelfTabSortResponse> s(UploadSelfTabSortRequest uploadSelfTabSortRequest);

        @RpcOperation("$GET /reading/bookapi/comic_tab/comic_detail/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ComicDetailResponse> s0(ComicDetailRequest comicDetailRequest);

        @RpcOperation("$GET /reading/bookapi/reader_banner_resource/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReaderBannerResourceResponse> t(GetReaderBannerResourceRequest getReaderBannerResourceRequest);

        @RpcOperation("$POST /reading/bookapi/ancient_books_read_progress/upload/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UploadAncientBooksProgressResponse> t0(UploadAncientBooksProgressRequest uploadAncientBooksProgressRequest);

        @RpcOperation("$GET /reading/bookapi/research/get/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetUserResearchResponse> u(GetUserResearchRequest getUserResearchRequest);

        @RpcOperation("$GET /reading/bookapi/excerpt/list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetExcerptListResponse> u0(GetExcerptListRequest getExcerptListRequest);

        @RpcOperation("$POST /reading/bookapi/local_book/upload")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UploadLocalBookChapterResponse> v(UploadLocalBookChapterRequest uploadLocalBookChapterRequest);

        @RpcOperation("$POST /reading/bookapi/activity/main_page/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetActivityMainPageResponse> v0(GetActivityMainPageRequest getActivityMainPageRequest);

        @RpcOperation("$GET /reading/bookapi/audio_settings/load/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetAudioSettingsResponse> w(GetAudioSettingsRequest getAudioSettingsRequest);

        @RpcOperation("$POST /reading/bookapi/bookshelf/update/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UpdateBookShelfInfoResponse> w0(UpdateBookShelfInfoRequest updateBookShelfInfoRequest);

        @RpcOperation("$GET /reading/bookapi/read_encourage/get/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReadEncourageResponse> x(GetReadEncourageRequest getReadEncourageRequest);

        @RpcOperation("$POST /reading/bookapi/client_report/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ClientReportResponse> x0(ClientReportRequest clientReportRequest);

        @RpcOperation("$GET /reading/bookapi/directory/all_items/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetDirectoryForItemIdResponse> y(GetDirectoryForItemIdRequest getDirectoryForItemIdRequest);

        @RpcOperation("$GET /reading/bookapi/sstimor/entry_info/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetSSTimorEntryInfoResponse> y0(GetSSTimorEntryInfoRequest getSSTimorEntryInfoRequest);

        @RpcOperation("$GET /reading/bookapi/chase_book/list/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetChaseBookResponse> z(GetChaseBookRequest getChaseBookRequest);

        @RpcOperation("$GET /reading/bookapi/video_tab/ab_result/v:version/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetVideoTabAbResultResponse> z0(GetVideoTabAbResultRequest getVideoTabAbResultRequest);
    }

    static {
        Covode.recordClassIndex(612424);
    }

    public static Class<?> s() {
        return a.class;
    }

    private static a r() {
        return (a) m.f(a.class);
    }

    public static Observable<GetBookShelfListResponse> A(GetBookShelfListRequest getBookShelfListRequest) {
        return r().a(getBookShelfListRequest);
    }

    public static Observable<ReadHistoryRecommendResponse> A0(ReadHistoryRecommendRequest readHistoryRecommendRequest) {
        return r().N0(readHistoryRecommendRequest);
    }

    public static Observable<GetBookShelfVideoInfoResponse> B(GetBookShelfVideoInfoRequest getBookShelfVideoInfoRequest) {
        return r().c0(getBookShelfVideoInfoRequest);
    }

    public static Observable<RecommendLogReportResponse> B0(RecommendLogReportRequest recommendLogReportRequest) {
        return r().L(recommendLogReportRequest);
    }

    public static Observable<BookToneInfoResponse> C(BookToneInfoRequest bookToneInfoRequest) {
        return r().K(bookToneInfoRequest);
    }

    public static Observable<RefreshReader1ColStrategyResponse> C0(RefreshReader1ColStrategyRequest refreshReader1ColStrategyRequest) {
        return r().o(refreshReader1ColStrategyRequest);
    }

    public static Observable<GetBookshelfTabResponse> D(GetBookshelfTabRequest getBookshelfTabRequest) {
        return r().e0(getBookshelfTabRequest);
    }

    public static Observable<ReportBookInfoResponse> D0(ReportBookInfoRequest reportBookInfoRequest) {
        return r().p0(reportBookInfoRequest);
    }

    public static Observable<GetCategoryCellDataResponse> E(GetCategoryCellDataRequest getCategoryCellDataRequest) {
        return r().J0(getCategoryCellDataRequest);
    }

    public static Observable<SSTimorTabResponse> E0(SSTimorTabRequest sSTimorTabRequest) {
        return r().R(sSTimorTabRequest);
    }

    public static Observable<GetChaseBookResponse> F(GetChaseBookRequest getChaseBookRequest) {
        return r().z(getChaseBookRequest);
    }

    public static Observable<GetSearchCellChangeResponse> F0(GetSearchCellChangeRequest getSearchCellChangeRequest) {
        return r().g0(getSearchCellChangeRequest);
    }

    public static Observable<ComicDetailResponse> G(ComicDetailRequest comicDetailRequest) {
        return r().s0(comicDetailRequest);
    }

    public static Observable<SearchUncoverSubscribeResponse> G0(SearchUncoverSubscribeRequest searchUncoverSubscribeRequest) {
        return r().j(searchUncoverSubscribeRequest);
    }

    public static Observable<GetCommerceTagInfoResponse> H(GetCommerceTagInfoRequest getCommerceTagInfoRequest) {
        return r().P(getCommerceTagInfoRequest);
    }

    public static Observable<AIContentFeedbackResponse> H0(AIContentFeedbackRequest aIContentFeedbackRequest) {
        return r().A(aIContentFeedbackRequest);
    }

    public static Observable<GetBookMallHomePageResponse> I(GetPlanRequest getPlanRequest) {
        return r().e(getPlanRequest);
    }

    public static Observable<SubscribeUncopyrightedBookResponse> I0(SubscribeUncopyrightedBookRequest subscribeUncopyrightedBookRequest) {
        return r().b0(subscribeUncopyrightedBookRequest);
    }

    public static Observable<GetDirectoryForInfoResponse> J(GetDirectoryForInfoRequest getDirectoryForInfoRequest) {
        return r().O0(getDirectoryForInfoRequest);
    }

    public static Observable<SuggestResponse> J0(SuggestRequest suggestRequest) {
        return r().R0(suggestRequest);
    }

    public static Observable<GetDirectoryForItemIdResponse> K(GetDirectoryForItemIdRequest getDirectoryForItemIdRequest) {
        return r().y(getDirectoryForItemIdRequest);
    }

    public static Observable<SurlRecommendResponse> K0(SurlRecommendRequest surlRecommendRequest) {
        return r().p(surlRecommendRequest);
    }

    public static Observable<GetExcerptListResponse> L(GetExcerptListRequest getExcerptListRequest) {
        return r().u0(getExcerptListRequest);
    }

    public static Observable<TopicSuggestResponse> L0(TopicSuggestRequest topicSuggestRequest) {
        return r().n0(topicSuggestRequest);
    }

    public static Observable<GetGameHistoryResponse> M(GetGameHistoryRequest getGameHistoryRequest) {
        return r().j0(getGameHistoryRequest);
    }

    public static Observable<UpdateBookShelfInfoResponse> M0(UpdateBookShelfInfoRequest updateBookShelfInfoRequest) {
        return r().w0(updateBookShelfInfoRequest);
    }

    public static Observable<GetGuideInfoResponse> N(GetGuideInfoRequest getGuideInfoRequest) {
        return r().i0(getGuideInfoRequest);
    }

    public static Observable<UpdateBookShelfVideoInfoResponse> N0(UpdateBookShelfVideoInfoRequest updateBookShelfVideoInfoRequest) {
        return r().a0(updateBookShelfVideoInfoRequest);
    }

    public static Observable<GetIMCResourceResponse> O(GetIMCResourceRequest getIMCResourceRequest) {
        return r().U(getIMCResourceRequest);
    }

    public static Observable<UpdateChaseBookResponse> O0(UpdateChaseBookRequest updateChaseBookRequest) {
        return r().k(updateChaseBookRequest);
    }

    public static Observable<GetLotteryDetailResponse> P(GetLotteryDetailRequest getLotteryDetailRequest) {
        return r().D(getLotteryDetailRequest);
    }

    public static Observable<UpdateGameHistoryResponse> P0(UpdateGameHistoryRequest updateGameHistoryRequest) {
        return r().k0(updateGameHistoryRequest);
    }

    public static Observable<GetMallBenefitResponse> Q(GetMallBenefitRequest getMallBenefitRequest) {
        return r().Q0(getMallBenefitRequest);
    }

    public static Observable<UpdateReadHistoryResponse> Q0(UpdateReadHistoryRequest updateReadHistoryRequest) {
        return r().V0(updateReadHistoryRequest);
    }

    public static Observable<GetMsgSettingsResponse> R(GetMsgSettingsRequest getMsgSettingsRequest) {
        return r().r(getMsgSettingsRequest);
    }

    public static Observable<UpdateShelfLocalBookListResponse> R0(UpdateShelfLocalBookListRequest updateShelfLocalBookListRequest) {
        return r().E0(updateShelfLocalBookListRequest);
    }

    public static Observable<GetMyTabPlanResponse> S(GetMyTabPlanRequest getMyTabPlanRequest) {
        return r().D0(getMyTabPlanRequest);
    }

    public static Observable<UploadAncientBooksProgressResponse> S0(UploadAncientBooksProgressRequest uploadAncientBooksProgressRequest) {
        return r().t0(uploadAncientBooksProgressRequest);
    }

    public static Observable<GetNewCategoryFrontPageResponse> T(GetNewCategoryFrontPageRequest getNewCategoryFrontPageRequest) {
        return r().V(getNewCategoryFrontPageRequest);
    }

    public static Observable<UploadAudioSettingsResponse> T0(UploadAudioSettingsRequest uploadAudioSettingsRequest) {
        return r().m(uploadAudioSettingsRequest);
    }

    public static Observable<GetNewCategoryLandingPageResponse> U(GetNewCategoryLandingPageRequest getNewCategoryLandingPageRequest) {
        return r().W(getNewCategoryLandingPageRequest);
    }

    public static Observable<UploadLocalBookChapterResponse> U0(UploadLocalBookChapterRequest uploadLocalBookChapterRequest) {
        return r().v(uploadLocalBookChapterRequest);
    }

    public static Observable<GetReadEncourageResponse> V(GetReadEncourageRequest getReadEncourageRequest) {
        return r().x(getReadEncourageRequest);
    }

    public static Observable<UploadMsgSettingsResponse> V0(UploadMsgSettingsRequest uploadMsgSettingsRequest) {
        return r().Q(uploadMsgSettingsRequest);
    }

    public static Observable<GetReadHistoryResponse> W(GetReadHistoryRequest getReadHistoryRequest) {
        return r().O(getReadHistoryRequest);
    }

    public static Observable<UploadProgressRateResponse> W0(UploadProgressRateRequest uploadProgressRateRequest) {
        return r().o0(uploadProgressRateRequest);
    }

    public static Observable<GetReadProgressResponse> X(GetReadProgressRequest getReadProgressRequest) {
        return r().P0(getReadProgressRequest);
    }

    public static Observable<UploadSelfTabSortResponse> X0(UploadSelfTabSortRequest uploadSelfTabSortRequest) {
        return r().s(uploadSelfTabSortRequest);
    }

    public static Observable<GetReadStatDetailResponse> Y(GetReadStatDetailRequest getReadStatDetailRequest) {
        return r().G(getReadStatDetailRequest);
    }

    public static Observable<GetReaderBannerResourceResponse> Z(GetReaderBannerResourceRequest getReaderBannerResourceRequest) {
        return r().t(getReaderBannerResourceRequest);
    }

    public static Observable<ActivatePlanResponse> a(ActivatePlanRequest activatePlanRequest) {
        return r().L0(activatePlanRequest);
    }

    public static Observable<GetRedDotResponse> a0(GetRedDotRequest getRedDotRequest) {
        return r().f0(getRedDotRequest);
    }

    public static Observable<ActiveSchemaReportResponse> b(ActiveSchemaReportRequest activeSchemaReportRequest) {
        return r().T(activeSchemaReportRequest);
    }

    public static Observable<RelatedBookResponse> b0(RelatedBookRequest relatedBookRequest) {
        return r().C(relatedBookRequest);
    }

    public static Observable<AddBookShelfInfoResponse> c(AddBookShelfInfoRequest addBookShelfInfoRequest) {
        return r().C0(addBookShelfInfoRequest);
    }

    public static Observable<SSTimorCellInfoResponse> c0(SSTimorCellInfoRequest sSTimorCellInfoRequest) {
        return r().Z(sSTimorCellInfoRequest);
    }

    public static Observable<AddVideoEpisodeResponse> d(AddVideoEpisodeRequest addVideoEpisodeRequest) {
        return r().I0(addVideoEpisodeRequest);
    }

    public static Observable<GetSSTimorEntryInfoResponse> d0(GetSSTimorEntryInfoRequest getSSTimorEntryInfoRequest) {
        return r().y0(getSSTimorEntryInfoRequest);
    }

    public static Observable<AudioDetailResponse> e(AudioDetailRequest audioDetailRequest) {
        return r().Y(audioDetailRequest);
    }

    public static Observable<SSTimorEntryTypeResponse> e0(SSTimorEntryTypeRequest sSTimorEntryTypeRequest) {
        return r().N(sSTimorEntryTypeRequest);
    }

    public static Observable<AudioPlayURLResponse> f(AudioPlayURLRequest audioPlayURLRequest) {
        return r().g(audioPlayURLRequest);
    }

    public static Observable<SSTimorTimeResponse> f0(SSTimorTimeRequest sSTimorTimeRequest) {
        return r().S0(sSTimorTimeRequest);
    }

    public static Observable<BatchAudioPlayURLResponse> g(BatchAudioPlayURLRequest batchAudioPlayURLRequest) {
        return r().c(batchAudioPlayURLRequest);
    }

    public static Observable<GetSearchCueResponse> g0(GetSearchCueRequest getSearchCueRequest) {
        return r().I(getSearchCueRequest);
    }

    public static Observable<BookDetailResponse> h(BookDetailRequest bookDetailRequest) {
        return r().d0(bookDetailRequest);
    }

    public static Observable<SearchSchemaLandingResponse> h0(SearchSchemaLandingRequest searchSchemaLandingRequest) {
        return r().T0(searchSchemaLandingRequest);
    }

    public static Observable<BookItemContentUnlockResponse> i(BookItemContentUnlockRequest bookItemContentUnlockRequest) {
        return r().q(bookItemContentUnlockRequest);
    }

    public static Observable<GetSearchTabDataResponse> i0(GetSearchPageRequest getSearchPageRequest) {
        return r().H0(getSearchPageRequest);
    }

    public static Observable<GetBookMallCellChangeResponse> j(GetBookMallCellChangeRequest getBookMallCellChangeRequest) {
        return r().i(getBookMallCellChangeRequest);
    }

    public static Observable<GetSearchUserInfoResponse> j0(GetSearchUserInfoRequest getSearchUserInfoRequest) {
        return r().B0(getSearchUserInfoRequest);
    }

    public static Observable<BookstoreTabResponse> k(BookstoreTabRequest bookstoreTabRequest) {
        return r().B(bookstoreTabRequest);
    }

    public static Observable<GetShelfLocalBookListResponse> k0(GetShelfLocalBookListRequest getShelfLocalBookListRequest) {
        return r().F(getShelfLocalBookListRequest);
    }

    public static Observable<CheckLocalBookChapterResponse> l(CheckLocalBookChapterRequest checkLocalBookChapterRequest) {
        return r().U0(checkLocalBookChapterRequest);
    }

    public static Observable<GetSnackBarInShelfResponse> l0(GetSnackBarInShelfRequset getSnackBarInShelfRequset) {
        return r().l0(getSnackBarInShelfRequset);
    }

    public static Observable<ClientReportResponse> m(ClientReportRequest clientReportRequest) {
        return r().x0(clientReportRequest);
    }

    public static Observable<GetUserDelBookshelfResponse> m0(GetUserDelBookshelfRequest getUserDelBookshelfRequest) {
        return r().f(getUserDelBookshelfRequest);
    }

    public static Observable<CommonAbResultResponse> n(CommonAbResultRequest commonAbResultRequest) {
        return r().X(commonAbResultRequest);
    }

    public static Observable<GetUserPublishVipToastResponse> n0(GetUserPublishVipToastRequest getUserPublishVipToastRequest) {
        return r().r0(getUserPublishVipToastRequest);
    }

    public static Observable<DeleteBookShelfInfoResponse> o(DeleteBookShelfInfoRequest deleteBookShelfInfoRequest) {
        return r().F0(deleteBookShelfInfoRequest);
    }

    public static Observable<GetUserResearchResponse> o0(GetUserResearchRequest getUserResearchRequest) {
        return r().u(getUserResearchRequest);
    }

    public static Observable<AIGuessYourQueryResponse> p(AIGuessYourQueryRequest aIGuessYourQueryRequest) {
        return r().h0(aIGuessYourQueryRequest);
    }

    public static Observable<GetVideoContinueWatchAbResponse> p0(GetVideoContinueWatchAbRequest getVideoContinueWatchAbRequest) {
        return r().n(getVideoContinueWatchAbRequest);
    }

    public static Observable<GetActivityMainPageResponse> q(GetActivityMainPageRequest getActivityMainPageRequest) {
        return r().v0(getActivityMainPageRequest);
    }

    public static Observable<VideoDetailResponse> q0(VideoDetailRequest videoDetailRequest) {
        return r().q0(videoDetailRequest);
    }

    public static Observable<GetVideoModelResponse> r0(GetVideoModelRequest getVideoModelRequest) {
        return r().A0(getVideoModelRequest);
    }

    public static Observable<GetVideoTabAbResultResponse> s0(GetVideoTabAbResultRequest getVideoTabAbResultRequest) {
        return r().z0(getVideoTabAbResultRequest);
    }

    public static Observable<GetAudioSettingsResponse> t(GetAudioSettingsRequest getAudioSettingsRequest) {
        return r().w(getAudioSettingsRequest);
    }

    public static Observable<GetWidgetsBookResponse> t0(GetWidgetsBookRequest getWidgetsBookRequest) {
        return r().G0(getWidgetsBookRequest);
    }

    public static Observable<GetBizStaticConfigResponse> u(GetBizStaticConfigRequest getBizStaticConfigRequest) {
        return r().K0(getBizStaticConfigRequest);
    }

    public static Observable<MBookDetailResponse> u0(MBookDetailRequest mBookDetailRequest) {
        return r().m0(mBookDetailRequest);
    }

    public static Observable<GetBookExtraResponse> v(GetBookExtraRequest getBookExtraRequest) {
        return r().M(getBookExtraRequest);
    }

    public static Observable<MGetReadProgressByBookIdResponse> v0(MGetReadProgressByBookIdRequest mGetReadProgressByBookIdRequest) {
        return r().H(mGetReadProgressByBookIdRequest);
    }

    public static Observable<GetBookPayDetailResponse> w(GetBookPayDetailRequest getBookPayDetailRequest) {
        return r().S(getBookPayDetailRequest);
    }

    public static Observable<MGetVideoDataResponse> w0(MGetVideoDataRequest mGetVideoDataRequest) {
        return r().l(mGetVideoDataRequest);
    }

    public static Observable<GetBookProfitMakingStrategyResponse> x(GetBookProfitMakingStrategyRequest getBookProfitMakingStrategyRequest) {
        return r().E(getBookProfitMakingStrategyRequest);
    }

    public static Observable<MGetVideoDetailResponse> x0(VideoDetailRequest videoDetailRequest) {
        return r().h(videoDetailRequest);
    }

    public static Observable<GetBookShelfBannerResponse> y(GetBookShelfBannerRequest getBookShelfBannerRequest) {
        return r().M0(getBookShelfBannerRequest);
    }

    public static Observable<SurlResponse> y0(SurlRequest surlRequest) {
        return r().J(surlRequest);
    }

    public static Observable<GetBookShelfInfoResponse> z(GetBookShelfInfoRequest getBookShelfInfoRequest) {
        return r().d(getBookShelfInfoRequest);
    }

    public static Observable<QueryBookUpdateResponse> z0(QueryBookUpdateRequest queryBookUpdateRequest) {
        return r().b(queryBookUpdateRequest);
    }
}