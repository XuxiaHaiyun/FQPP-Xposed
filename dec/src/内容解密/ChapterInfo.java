package com.dragon.read.rpc.model;

import com.bytedance.covode.number.Covode;
import com.bytedance.rpc.serialize.FieldType;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/* JADX INFO: loaded from: classes9.dex */
public class ChapterInfo implements Serializable {
    private static Class fieldTypeClassRef;
    private static final long serialVersionUID = 0;

    @SerializedName("chapter_name")
    public String chapterName;

    @SerializedName("creation_info")
    public String creationInfo;

    @SerializedName("group_id")
    public String groupId;

    @SerializedName("item_id")
    public String itemId;

    static {
        Covode.recordClassIndex(607416);
        fieldTypeClassRef = FieldType.class;
    }
}