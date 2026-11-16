package com.dragon.read.saas.ugc.rpc;

import com.bytedance.covode.number.Covode;
import com.bytedance.rpc.annotation.RpcOperation;
import com.bytedance.rpc.annotation.RpcParams;
import com.bytedance.rpc.annotation.RpcSerializer;
import com.bytedance.rpc.m;
import com.bytedance.rpc.serialize.SerializeType;
import com.dragon.read.saas.ugc.model.AddCommentRequest;
import com.dragon.read.saas.ugc.model.AddCommentResponse;
import com.dragon.read.saas.ugc.model.AddReplyRequest;
import com.dragon.read.saas.ugc.model.AddReplyResponse;
import com.dragon.read.saas.ugc.model.DelCommentRequest;
import com.dragon.read.saas.ugc.model.DelCommentResponse;
import com.dragon.read.saas.ugc.model.DeleteAIGCRequest;
import com.dragon.read.saas.ugc.model.DeleteAIGCResponse;
import com.dragon.read.saas.ugc.model.DoActionRequest;
import com.dragon.read.saas.ugc.model.DoActionResponse;
import com.dragon.read.saas.ugc.model.DoTaskRequest;
import com.dragon.read.saas.ugc.model.DoTaskResponse;
import com.dragon.read.saas.ugc.model.GenerateAIContentRequest;
import com.dragon.read.saas.ugc.model.GenerateAIContentResponse;
import com.dragon.read.saas.ugc.model.GetAIContentGenSettingReponse;
import com.dragon.read.saas.ugc.model.GetAIContentGenSettingRequest;
import com.dragon.read.saas.ugc.model.GetCommentListRequest;
import com.dragon.read.saas.ugc.model.GetCommentListResponse;
import com.dragon.read.saas.ugc.model.GetIdeaListRequest;
import com.dragon.read.saas.ugc.model.GetIdeaListResponse;
import com.dragon.read.saas.ugc.model.GetRelationListRequest;
import com.dragon.read.saas.ugc.model.GetRelationListResponse;
import com.dragon.read.saas.ugc.model.GetReplyListRequest;
import com.dragon.read.saas.ugc.model.GetReplyListResponse;
import com.dragon.read.saas.ugc.model.GetUserAIGCListRequest;
import com.dragon.read.saas.ugc.model.GetUserAIGCListResponse;
import com.dragon.read.saas.ugc.model.GetUserCommentRequest;
import com.dragon.read.saas.ugc.model.GetUserCommentResponse;
import com.dragon.read.saas.ugc.model.GetUserConfigRequest;
import com.dragon.read.saas.ugc.model.GetUserConfigResponse;
import com.dragon.read.saas.ugc.model.GetUserProfileRequest;
import com.dragon.read.saas.ugc.model.GetUserProfileResponse;
import com.dragon.read.saas.ugc.model.ReportCommentRequest;
import com.dragon.read.saas.ugc.model.ReportCommentResponse;
import com.dragon.read.saas.ugc.model.SetUserConfigRequest;
import com.dragon.read.saas.ugc.model.SetUserConfigResponse;
import com.dragon.read.saas.ugc.model.UpdateCommentRequest;
import com.dragon.read.saas.ugc.model.UpdateCommentResponse;
import com.dragon.read.saas.ugc.model.UploadEditedAIGCRequest;
import com.dragon.read.saas.ugc.model.UploadEditedAIGCResponse;
import io.reactivex.Observable;

/* JADX INFO: loaded from: classes2.dex */
public class CommentApiService {

    private interface a {
        @RpcOperation("$POST /novel/commentapi/ai_generate/gen_setting/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetAIContentGenSettingReponse> a(GetAIContentGenSettingRequest getAIContentGenSettingRequest);

        @RpcOperation("$POST /novel/commentapi/ai_generate/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GenerateAIContentResponse> b(GenerateAIContentRequest generateAIContentRequest);

        @RpcOperation("$POST /novel/commentapi/user_config/set/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<SetUserConfigResponse> c(SetUserConfigRequest setUserConfigRequest);

        @RpcOperation("$POST /novel/commentapi/aigc/del/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<DeleteAIGCResponse> d(DeleteAIGCRequest deleteAIGCRequest);

        @RpcOperation("$POST /novel/commentapi/relation/list/:target_user_id/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetRelationListResponse> e(GetRelationListRequest getRelationListRequest);

        @RpcOperation("$POST /novel/commentapi/comment/list/:group_id/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetCommentListResponse> f(GetCommentListRequest getCommentListRequest);

        @RpcOperation("$POST /novel/commentapi/task/do/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<DoTaskResponse> g(DoTaskRequest doTaskRequest);

        @RpcOperation("$POST /novel/commentapi/idea/list/:item_id/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetIdeaListResponse> h(GetIdeaListRequest getIdeaListRequest);

        @RpcOperation("$POST /novel/commentapi/comment/add/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AddCommentResponse> i(AddCommentRequest addCommentRequest);

        @RpcOperation("$POST /novel/commentapi/reply/list/:comment_id/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetReplyListResponse> j(GetReplyListRequest getReplyListRequest);

        @RpcOperation("$POST /novel/commentapi/edited_aigc/upload/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UploadEditedAIGCResponse> k(UploadEditedAIGCRequest uploadEditedAIGCRequest);

        @RpcOperation("$POST /novel/commentapi/reply/add/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<AddReplyResponse> l(AddReplyRequest addReplyRequest);

        @RpcOperation("$POST /novel/commentapi/user_config/get/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetUserConfigResponse> m(GetUserConfigRequest getUserConfigRequest);

        @RpcOperation("$POST /novel/commentapi/user/profile/:target_user_id/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetUserProfileResponse> n(GetUserProfileRequest getUserProfileRequest);

        @RpcOperation("$POST /novel/commentapi/comment/do_action/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<DoActionResponse> o(DoActionRequest doActionRequest);

        @RpcOperation("$POST /novel/commentapi/comment/self/:target_user_id/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetUserCommentResponse> p(GetUserCommentRequest getUserCommentRequest);

        @RpcOperation("$POST /novel/commentapi/comment/del/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<DelCommentResponse> q(DelCommentRequest delCommentRequest);

        @RpcOperation("$POST /novel/commentapi/aigc/user_list/:group_id/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<GetUserAIGCListResponse> r(GetUserAIGCListRequest getUserAIGCListRequest);

        @RpcOperation("$POST /novel/commentapi/comment/update/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<UpdateCommentResponse> s(UpdateCommentRequest updateCommentRequest);

        @RpcOperation("$POST /novel/commentapi/comment/report/v1/")
        @RpcParams(true)
        @RpcSerializer(SerializeType.JSON)
        Observable<ReportCommentResponse> t(ReportCommentRequest reportCommentRequest);
    }

    static {
        Covode.recordClassIndex(613077);
    }

    public static Class<?> getApiClass() {
        return a.class;
    }

    private static a getApi() {
        return (a) m.f(a.class);
    }

    public static Observable<AddCommentResponse> addCommentRxJava(AddCommentRequest addCommentRequest) {
        return getApi().i(addCommentRequest);
    }

    public static Observable<AddReplyResponse> addReplyRxJava(AddReplyRequest addReplyRequest) {
        return getApi().l(addReplyRequest);
    }

    public static Observable<DelCommentResponse> delCommentRxJava(DelCommentRequest delCommentRequest) {
        return getApi().q(delCommentRequest);
    }

    public static Observable<DeleteAIGCResponse> deleteAIGCRxJava(DeleteAIGCRequest deleteAIGCRequest) {
        return getApi().d(deleteAIGCRequest);
    }

    public static Observable<DoActionResponse> doActionRxJava(DoActionRequest doActionRequest) {
        return getApi().o(doActionRequest);
    }

    public static Observable<DoTaskResponse> doTaskRxJava(DoTaskRequest doTaskRequest) {
        return getApi().g(doTaskRequest);
    }

    public static Observable<GenerateAIContentResponse> generateAIContentRxJava(GenerateAIContentRequest generateAIContentRequest) {
        return getApi().b(generateAIContentRequest);
    }

    public static Observable<GetAIContentGenSettingReponse> getAIContentGenSettingRxJava(GetAIContentGenSettingRequest getAIContentGenSettingRequest) {
        return getApi().a(getAIContentGenSettingRequest);
    }

    public static Observable<GetCommentListResponse> getCommentListRxJava(GetCommentListRequest getCommentListRequest) {
        return getApi().f(getCommentListRequest);
    }

    public static Observable<GetIdeaListResponse> getIdeaListRxJava(GetIdeaListRequest getIdeaListRequest) {
        return getApi().h(getIdeaListRequest);
    }

    public static Observable<GetRelationListResponse> getRelationListRxJava(GetRelationListRequest getRelationListRequest) {
        return getApi().e(getRelationListRequest);
    }

    public static Observable<GetReplyListResponse> getReplyListRxJava(GetReplyListRequest getReplyListRequest) {
        return getApi().j(getReplyListRequest);
    }

    public static Observable<GetUserAIGCListResponse> getUserAIGCListRxJava(GetUserAIGCListRequest getUserAIGCListRequest) {
        return getApi().r(getUserAIGCListRequest);
    }

    public static Observable<GetUserCommentResponse> getUserCommentRxJava(GetUserCommentRequest getUserCommentRequest) {
        return getApi().p(getUserCommentRequest);
    }

    public static Observable<GetUserConfigResponse> getUserConfigRxJava(GetUserConfigRequest getUserConfigRequest) {
        return getApi().m(getUserConfigRequest);
    }

    public static Observable<GetUserProfileResponse> getUserProfileRxJava(GetUserProfileRequest getUserProfileRequest) {
        return getApi().n(getUserProfileRequest);
    }

    public static Observable<ReportCommentResponse> reportCommentRxJava(ReportCommentRequest reportCommentRequest) {
        return getApi().t(reportCommentRequest);
    }

    public static Observable<SetUserConfigResponse> setUserConfigRxJava(SetUserConfigRequest setUserConfigRequest) {
        return getApi().c(setUserConfigRequest);
    }

    public static Observable<UpdateCommentResponse> updateCommentRxJava(UpdateCommentRequest updateCommentRequest) {
        return getApi().s(updateCommentRequest);
    }

    public static Observable<UploadEditedAIGCResponse> uploadEditedAIGCRxJava(UploadEditedAIGCRequest uploadEditedAIGCRequest) {
        return getApi().k(uploadEditedAIGCRequest);
    }
}