package com.dragon.read.saas.ugc.model;

import com.bytedance.covode.number.Covode;
import com.bytedance.rpc.annotation.RpcField;
import com.bytedance.rpc.annotation.RpcFieldModify;
import com.bytedance.rpc.serialize.FieldType;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/* JADX INFO: loaded from: classes2.dex */
@RpcFieldModify
public class GetIdeaListRequest implements Serializable {
    private static Class fieldTypeClassRef;
    private static final long serialVersionUID = 0;

    @SerializedName("aid")
    @RpcField(FieldType.QUERY)
    public int appID;

    @SerializedName("comment_source")
    public UgcCommentSourceEnum commentSource;

    @SerializedName("compliance_status")
    @RpcField(FieldType.QUERY)
    public int complianceStatus;

    @SerializedName("item_id")
    public String itemID;

    @SerializedName("item_version")
    public String itemVersion;

    @SerializedName("server_channel")
    public UgcCommentChannelEnum serverChannel;

    static {
        Covode.recordClassIndex(612747);
        fieldTypeClassRef = FieldType.class;
    }
}