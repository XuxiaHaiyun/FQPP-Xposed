package com.dragon.read.saas.ugc.model;

import com.bytedance.covode.number.Covode;
import com.bytedance.rpc.serialize.FieldType;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/* JADX INFO: loaded from: classes2.dex */
public class GetIdeaListResponse implements Serializable {
    private static Class fieldTypeClassRef;
    private static final long serialVersionUID = 0;
    public CommentApiERR code;
    public GetIdeaListData data;

    @SerializedName("debug_info")
    public String debugInfo;

    @SerializedName("Header")
    public CustomRespHeader header;

    @SerializedName("log_id")
    public String logID;
    public String message;

    static {
        Covode.recordClassIndex(612748);
        fieldTypeClassRef = FieldType.class;
    }
}