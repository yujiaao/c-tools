
package com.example.es.pojo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ad_city",
    "allow_delete",
    "audit_notes",
    "audit_recommend",
    "audit_stage",
    "auditor",
    "author_id",
    "auto_write_aricle_id",
    "autohome_ua",
    "cancel_recommend_time",
    "client_type",
    "concept_key_words",
    "configure",
    "content_type",
    "create_time",
    "description",
    "description2",
    "expire_date",
    "hailiang_reason",
    "hailiang_status",
    "has_review",
    "hot_spot_tag",
    "ident",
    "image",
    "image1",
    "image2",
    "image3",
    "img_cross",
    "img_vertical",
    "info_id",
    "info_level",
    "info_type",
    "innovate",
    "ip",
    "ip_province",
    "is_artificial",
    "is_auto_write",
    "is_business",
    "is_convert_text",
    "is_deduction",
    "is_del",
    "is_expose",
    "is_fastest_first",
    "is_first",
    "is_important",
    "is_local_audit",
    "is_low_car",
    "is_manual_content_level",
    "is_manual_or_ai",
    "is_original",
    "is_product_image",
    "is_publish",
    "is_push",
    "is_timing",
    "is_top",
    "is_wai_tui",
    "is_you_xuan",
    "key_words",
    "last_update_time",
    "local_audit_time",
    "local_recommend_time",
    "local_recommend_type",
    "new_key_word_ids",
    "new_key_words",
    "operate_remarks",
    "plus_or_minus_one",
    "plus_or_minus_two",
    "publish_time",
    "publish_timing",
    "push_description",
    "push_title",
    "recommend_to_moji",
    "recommend_to_series",
    "recommend_to_xny",
    "recycled",
    "recycled_along_with_author",
    "show_big_img",
    "show_image_list",
    "show_level",
    "sort_type",
    "status",
    "status_note",
    "status_note_id",
    "title",
    "title2",
    "title3",
    "version_source",
    "wai_tui_time",
    "wait_audit_time",
    "wait_audit_time2",
    "wait_audit_time3",
    "you_xuan_time"
})
@Generated("jsonschema2pojo")
public class Contents {

    @JsonProperty("ad_city")
    private String adCity;
    @JsonProperty("allow_delete")
    private Boolean allowDelete;
    @JsonProperty("audit_notes")
    private String auditNotes;
    @JsonProperty("audit_recommend")
    private Long auditRecommend;
    @JsonProperty("audit_stage")
    private Long auditStage;
    @JsonProperty("auditor")
    private Long auditor;
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("auto_write_aricle_id")
    private String autoWriteAricleId;
    @JsonProperty("autohome_ua")
    private String autohomeUa;
    @JsonProperty("cancel_recommend_time")
    private String cancelRecommendTime;
    @JsonProperty("client_type")
    private Long clientType;
    @JsonProperty("concept_key_words")
    private String conceptKeyWords;
    @JsonProperty("configure")
    private Long configure;
    @JsonProperty("content_type")
    private Long contentType;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("description")
    private String description;
    @JsonProperty("description2")
    private String description2;
    @JsonProperty("expire_date")
    private String expireDate;
    @JsonProperty("hailiang_reason")
    private String hailiangReason;
    @JsonProperty("hailiang_status")
    private Long hailiangStatus;
    @JsonProperty("has_review")
    private Boolean hasReview;
    @JsonProperty("hot_spot_tag")
    private Long hotSpotTag;
    @JsonProperty("ident")
    private Long ident;
    @JsonProperty("image")
    private String image;
    @JsonProperty("image1")
    private String image1;
    @JsonProperty("image2")
    private String image2;
    @JsonProperty("image3")
    private String image3;
    @JsonProperty("img_cross")
    private String imgCross;
    @JsonProperty("img_vertical")
    private String imgVertical;
    @JsonProperty("info_id")
    private Long infoId;
    @JsonProperty("info_level")
    private Long infoLevel;
    @JsonProperty("info_type")
    private Long infoType;
    @JsonProperty("innovate")
    private Boolean innovate;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("ip_province")
    private String ipProvince;
    @JsonProperty("is_artificial")
    private Boolean isArtificial;
    @JsonProperty("is_auto_write")
    private Boolean isAutoWrite;
    @JsonProperty("is_business")
    private Boolean isBusiness;
    @JsonProperty("is_convert_text")
    private Boolean isConvertText;
    @JsonProperty("is_deduction")
    private Boolean isDeduction;
    @JsonProperty("is_del")
    private Long isDel;
    @JsonProperty("is_expose")
    private Boolean isExpose;
    @JsonProperty("is_fastest_first")
    private Boolean isFastestFirst;
    @JsonProperty("is_first")
    private Boolean isFirst;
    @JsonProperty("is_important")
    private Boolean isImportant;
    @JsonProperty("is_local_audit")
    private Boolean isLocalAudit;
    @JsonProperty("is_low_car")
    private Boolean isLowCar;
    @JsonProperty("is_manual_content_level")
    private Boolean isManualContentLevel;
    @JsonProperty("is_manual_or_ai")
    private Boolean isManualOrAi;
    @JsonProperty("is_original")
    private Boolean isOriginal;
    @JsonProperty("is_product_image")
    private Boolean isProductImage;
    @JsonProperty("is_publish")
    private Boolean isPublish;
    @JsonProperty("is_push")
    private Boolean isPush;
    @JsonProperty("is_timing")
    private Boolean isTiming;
    @JsonProperty("is_top")
    private Boolean isTop;
    @JsonProperty("is_wai_tui")
    private Boolean isWaiTui;
    @JsonProperty("is_you_xuan")
    private Boolean isYouXuan;
    @JsonProperty("key_words")
    private String keyWords;
    @JsonProperty("last_update_time")
    private String lastUpdateTime;
    @JsonProperty("local_audit_time")
    private String localAuditTime;
    @JsonProperty("local_recommend_time")
    private String localRecommendTime;
    @JsonProperty("local_recommend_type")
    private Long localRecommendType;
    @JsonProperty("new_key_word_ids")
    private List<String> newKeyWordIds = new ArrayList<String>();
    @JsonProperty("new_key_words")
    private String newKeyWords;
    @JsonProperty("operate_remarks")
    private String operateRemarks;
    @JsonProperty("plus_or_minus_one")
    private Long plusOrMinusOne;
    @JsonProperty("plus_or_minus_two")
    private Long plusOrMinusTwo;
    @JsonProperty("publish_time")
    private String publishTime;
    @JsonProperty("publish_timing")
    private String publishTiming;
    @JsonProperty("push_description")
    private String pushDescription;
    @JsonProperty("push_title")
    private String pushTitle;
    @JsonProperty("recommend_to_moji")
    private Boolean recommendToMoji;
    @JsonProperty("recommend_to_series")
    private Boolean recommendToSeries;
    @JsonProperty("recommend_to_xny")
    private Boolean recommendToXny;
    @JsonProperty("recycled")
    private Boolean recycled;
    @JsonProperty("recycled_along_with_author")
    private Boolean recycledAlongWithAuthor;
    @JsonProperty("show_big_img")
    private Boolean showBigImg;
    @JsonProperty("show_image_list")
    private Boolean showImageList;
    @JsonProperty("show_level")
    private Long showLevel;
    @JsonProperty("sort_type")
    private String sortType;
    @JsonProperty("status")
    private Long status;
    @JsonProperty("status_note")
    private String statusNote;
    @JsonProperty("status_note_id")
    private String statusNoteId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("title2")
    private String title2;
    @JsonProperty("title3")
    private String title3;
    @JsonProperty("version_source")
    private Long versionSource;
    @JsonProperty("wai_tui_time")
    private String waiTuiTime;
    @JsonProperty("wait_audit_time")
    private String waitAuditTime;
    @JsonProperty("wait_audit_time2")
    private String waitAuditTime2;
    @JsonProperty("wait_audit_time3")
    private String waitAuditTime3;
    @JsonProperty("you_xuan_time")
    private String youXuanTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("ad_city")
    public String getAdCity() {
        return adCity;
    }

    @JsonProperty("ad_city")
    public void setAdCity(String adCity) {
        this.adCity = adCity;
    }

    @JsonProperty("allow_delete")
    public Boolean getAllowDelete() {
        return allowDelete;
    }

    @JsonProperty("allow_delete")
    public void setAllowDelete(Boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    @JsonProperty("audit_notes")
    public String getAuditNotes() {
        return auditNotes;
    }

    @JsonProperty("audit_notes")
    public void setAuditNotes(String auditNotes) {
        this.auditNotes = auditNotes;
    }

    @JsonProperty("audit_recommend")
    public Long getAuditRecommend() {
        return auditRecommend;
    }

    @JsonProperty("audit_recommend")
    public void setAuditRecommend(Long auditRecommend) {
        this.auditRecommend = auditRecommend;
    }

    @JsonProperty("audit_stage")
    public Long getAuditStage() {
        return auditStage;
    }

    @JsonProperty("audit_stage")
    public void setAuditStage(Long auditStage) {
        this.auditStage = auditStage;
    }

    @JsonProperty("auditor")
    public Long getAuditor() {
        return auditor;
    }

    @JsonProperty("auditor")
    public void setAuditor(Long auditor) {
        this.auditor = auditor;
    }

    @JsonProperty("author_id")
    public Long getAuthorId() {
        return authorId;
    }

    @JsonProperty("author_id")
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @JsonProperty("auto_write_aricle_id")
    public String getAutoWriteAricleId() {
        return autoWriteAricleId;
    }

    @JsonProperty("auto_write_aricle_id")
    public void setAutoWriteAricleId(String autoWriteAricleId) {
        this.autoWriteAricleId = autoWriteAricleId;
    }

    @JsonProperty("autohome_ua")
    public String getAutohomeUa() {
        return autohomeUa;
    }

    @JsonProperty("autohome_ua")
    public void setAutohomeUa(String autohomeUa) {
        this.autohomeUa = autohomeUa;
    }

    @JsonProperty("cancel_recommend_time")
    public String getCancelRecommendTime() {
        return cancelRecommendTime;
    }

    @JsonProperty("cancel_recommend_time")
    public void setCancelRecommendTime(String cancelRecommendTime) {
        this.cancelRecommendTime = cancelRecommendTime;
    }

    @JsonProperty("client_type")
    public Long getClientType() {
        return clientType;
    }

    @JsonProperty("client_type")
    public void setClientType(Long clientType) {
        this.clientType = clientType;
    }

    @JsonProperty("concept_key_words")
    public String getConceptKeyWords() {
        return conceptKeyWords;
    }

    @JsonProperty("concept_key_words")
    public void setConceptKeyWords(String conceptKeyWords) {
        this.conceptKeyWords = conceptKeyWords;
    }

    @JsonProperty("configure")
    public Long getConfigure() {
        return configure;
    }

    @JsonProperty("configure")
    public void setConfigure(Long configure) {
        this.configure = configure;
    }

    @JsonProperty("content_type")
    public Long getContentType() {
        return contentType;
    }

    @JsonProperty("content_type")
    public void setContentType(Long contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("create_time")
    public String getCreateTime() {
        return createTime;
    }

    @JsonProperty("create_time")
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("description2")
    public String getDescription2() {
        return description2;
    }

    @JsonProperty("description2")
    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    @JsonProperty("expire_date")
    public String getExpireDate() {
        return expireDate;
    }

    @JsonProperty("expire_date")
    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    @JsonProperty("hailiang_reason")
    public String getHailiangReason() {
        return hailiangReason;
    }

    @JsonProperty("hailiang_reason")
    public void setHailiangReason(String hailiangReason) {
        this.hailiangReason = hailiangReason;
    }

    @JsonProperty("hailiang_status")
    public Long getHailiangStatus() {
        return hailiangStatus;
    }

    @JsonProperty("hailiang_status")
    public void setHailiangStatus(Long hailiangStatus) {
        this.hailiangStatus = hailiangStatus;
    }

    @JsonProperty("has_review")
    public Boolean getHasReview() {
        return hasReview;
    }

    @JsonProperty("has_review")
    public void setHasReview(Boolean hasReview) {
        this.hasReview = hasReview;
    }

    @JsonProperty("hot_spot_tag")
    public Long getHotSpotTag() {
        return hotSpotTag;
    }

    @JsonProperty("hot_spot_tag")
    public void setHotSpotTag(Long hotSpotTag) {
        this.hotSpotTag = hotSpotTag;
    }

    @JsonProperty("ident")
    public Long getIdent() {
        return ident;
    }

    @JsonProperty("ident")
    public void setIdent(Long ident) {
        this.ident = ident;
    }

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    @JsonProperty("image1")
    public String getImage1() {
        return image1;
    }

    @JsonProperty("image1")
    public void setImage1(String image1) {
        this.image1 = image1;
    }

    @JsonProperty("image2")
    public String getImage2() {
        return image2;
    }

    @JsonProperty("image2")
    public void setImage2(String image2) {
        this.image2 = image2;
    }

    @JsonProperty("image3")
    public String getImage3() {
        return image3;
    }

    @JsonProperty("image3")
    public void setImage3(String image3) {
        this.image3 = image3;
    }

    @JsonProperty("img_cross")
    public String getImgCross() {
        return imgCross;
    }

    @JsonProperty("img_cross")
    public void setImgCross(String imgCross) {
        this.imgCross = imgCross;
    }

    @JsonProperty("img_vertical")
    public String getImgVertical() {
        return imgVertical;
    }

    @JsonProperty("img_vertical")
    public void setImgVertical(String imgVertical) {
        this.imgVertical = imgVertical;
    }

    @JsonProperty("info_id")
    public Long getInfoId() {
        return infoId;
    }

    @JsonProperty("info_id")
    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    @JsonProperty("info_level")
    public Long getInfoLevel() {
        return infoLevel;
    }

    @JsonProperty("info_level")
    public void setInfoLevel(Long infoLevel) {
        this.infoLevel = infoLevel;
    }

    @JsonProperty("info_type")
    public Long getInfoType() {
        return infoType;
    }

    @JsonProperty("info_type")
    public void setInfoType(Long infoType) {
        this.infoType = infoType;
    }

    @JsonProperty("innovate")
    public Boolean getInnovate() {
        return innovate;
    }

    @JsonProperty("innovate")
    public void setInnovate(Boolean innovate) {
        this.innovate = innovate;
    }

    @JsonProperty("ip")
    public String getIp() {
        return ip;
    }

    @JsonProperty("ip")
    public void setIp(String ip) {
        this.ip = ip;
    }

    @JsonProperty("ip_province")
    public String getIpProvince() {
        return ipProvince;
    }

    @JsonProperty("ip_province")
    public void setIpProvince(String ipProvince) {
        this.ipProvince = ipProvince;
    }

    @JsonProperty("is_artificial")
    public Boolean getIsArtificial() {
        return isArtificial;
    }

    @JsonProperty("is_artificial")
    public void setIsArtificial(Boolean isArtificial) {
        this.isArtificial = isArtificial;
    }

    @JsonProperty("is_auto_write")
    public Boolean getIsAutoWrite() {
        return isAutoWrite;
    }

    @JsonProperty("is_auto_write")
    public void setIsAutoWrite(Boolean isAutoWrite) {
        this.isAutoWrite = isAutoWrite;
    }

    @JsonProperty("is_business")
    public Boolean getIsBusiness() {
        return isBusiness;
    }

    @JsonProperty("is_business")
    public void setIsBusiness(Boolean isBusiness) {
        this.isBusiness = isBusiness;
    }

    @JsonProperty("is_convert_text")
    public Boolean getIsConvertText() {
        return isConvertText;
    }

    @JsonProperty("is_convert_text")
    public void setIsConvertText(Boolean isConvertText) {
        this.isConvertText = isConvertText;
    }

    @JsonProperty("is_deduction")
    public Boolean getIsDeduction() {
        return isDeduction;
    }

    @JsonProperty("is_deduction")
    public void setIsDeduction(Boolean isDeduction) {
        this.isDeduction = isDeduction;
    }

    @JsonProperty("is_del")
    public Long getIsDel() {
        return isDel;
    }

    @JsonProperty("is_del")
    public void setIsDel(Long isDel) {
        this.isDel = isDel;
    }

    @JsonProperty("is_expose")
    public Boolean getIsExpose() {
        return isExpose;
    }

    @JsonProperty("is_expose")
    public void setIsExpose(Boolean isExpose) {
        this.isExpose = isExpose;
    }

    @JsonProperty("is_fastest_first")
    public Boolean getIsFastestFirst() {
        return isFastestFirst;
    }

    @JsonProperty("is_fastest_first")
    public void setIsFastestFirst(Boolean isFastestFirst) {
        this.isFastestFirst = isFastestFirst;
    }

    @JsonProperty("is_first")
    public Boolean getIsFirst() {
        return isFirst;
    }

    @JsonProperty("is_first")
    public void setIsFirst(Boolean isFirst) {
        this.isFirst = isFirst;
    }

    @JsonProperty("is_important")
    public Boolean getIsImportant() {
        return isImportant;
    }

    @JsonProperty("is_important")
    public void setIsImportant(Boolean isImportant) {
        this.isImportant = isImportant;
    }

    @JsonProperty("is_local_audit")
    public Boolean getIsLocalAudit() {
        return isLocalAudit;
    }

    @JsonProperty("is_local_audit")
    public void setIsLocalAudit(Boolean isLocalAudit) {
        this.isLocalAudit = isLocalAudit;
    }

    @JsonProperty("is_low_car")
    public Boolean getIsLowCar() {
        return isLowCar;
    }

    @JsonProperty("is_low_car")
    public void setIsLowCar(Boolean isLowCar) {
        this.isLowCar = isLowCar;
    }

    @JsonProperty("is_manual_content_level")
    public Boolean getIsManualContentLevel() {
        return isManualContentLevel;
    }

    @JsonProperty("is_manual_content_level")
    public void setIsManualContentLevel(Boolean isManualContentLevel) {
        this.isManualContentLevel = isManualContentLevel;
    }

    @JsonProperty("is_manual_or_ai")
    public Boolean getIsManualOrAi() {
        return isManualOrAi;
    }

    @JsonProperty("is_manual_or_ai")
    public void setIsManualOrAi(Boolean isManualOrAi) {
        this.isManualOrAi = isManualOrAi;
    }

    @JsonProperty("is_original")
    public Boolean getIsOriginal() {
        return isOriginal;
    }

    @JsonProperty("is_original")
    public void setIsOriginal(Boolean isOriginal) {
        this.isOriginal = isOriginal;
    }

    @JsonProperty("is_product_image")
    public Boolean getIsProductImage() {
        return isProductImage;
    }

    @JsonProperty("is_product_image")
    public void setIsProductImage(Boolean isProductImage) {
        this.isProductImage = isProductImage;
    }

    @JsonProperty("is_publish")
    public Boolean getIsPublish() {
        return isPublish;
    }

    @JsonProperty("is_publish")
    public void setIsPublish(Boolean isPublish) {
        this.isPublish = isPublish;
    }

    @JsonProperty("is_push")
    public Boolean getIsPush() {
        return isPush;
    }

    @JsonProperty("is_push")
    public void setIsPush(Boolean isPush) {
        this.isPush = isPush;
    }

    @JsonProperty("is_timing")
    public Boolean getIsTiming() {
        return isTiming;
    }

    @JsonProperty("is_timing")
    public void setIsTiming(Boolean isTiming) {
        this.isTiming = isTiming;
    }

    @JsonProperty("is_top")
    public Boolean getIsTop() {
        return isTop;
    }

    @JsonProperty("is_top")
    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    @JsonProperty("is_wai_tui")
    public Boolean getIsWaiTui() {
        return isWaiTui;
    }

    @JsonProperty("is_wai_tui")
    public void setIsWaiTui(Boolean isWaiTui) {
        this.isWaiTui = isWaiTui;
    }

    @JsonProperty("is_you_xuan")
    public Boolean getIsYouXuan() {
        return isYouXuan;
    }

    @JsonProperty("is_you_xuan")
    public void setIsYouXuan(Boolean isYouXuan) {
        this.isYouXuan = isYouXuan;
    }

    @JsonProperty("key_words")
    public String getKeyWords() {
        return keyWords;
    }

    @JsonProperty("key_words")
    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    @JsonProperty("last_update_time")
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    @JsonProperty("last_update_time")
    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @JsonProperty("local_audit_time")
    public String getLocalAuditTime() {
        return localAuditTime;
    }

    @JsonProperty("local_audit_time")
    public void setLocalAuditTime(String localAuditTime) {
        this.localAuditTime = localAuditTime;
    }

    @JsonProperty("local_recommend_time")
    public String getLocalRecommendTime() {
        return localRecommendTime;
    }

    @JsonProperty("local_recommend_time")
    public void setLocalRecommendTime(String localRecommendTime) {
        this.localRecommendTime = localRecommendTime;
    }

    @JsonProperty("local_recommend_type")
    public Long getLocalRecommendType() {
        return localRecommendType;
    }

    @JsonProperty("local_recommend_type")
    public void setLocalRecommendType(Long localRecommendType) {
        this.localRecommendType = localRecommendType;
    }

    @JsonProperty("new_key_word_ids")
    public List<String> getNewKeyWordIds() {
        return newKeyWordIds;
    }

    @JsonProperty("new_key_word_ids")
    public void setNewKeyWordIds(List<String> newKeyWordIds) {
        this.newKeyWordIds = newKeyWordIds;
    }

    @JsonProperty("new_key_words")
    public String getNewKeyWords() {
        return newKeyWords;
    }

    @JsonProperty("new_key_words")
    public void setNewKeyWords(String newKeyWords) {
        this.newKeyWords = newKeyWords;
    }

    @JsonProperty("operate_remarks")
    public String getOperateRemarks() {
        return operateRemarks;
    }

    @JsonProperty("operate_remarks")
    public void setOperateRemarks(String operateRemarks) {
        this.operateRemarks = operateRemarks;
    }

    @JsonProperty("plus_or_minus_one")
    public Long getPlusOrMinusOne() {
        return plusOrMinusOne;
    }

    @JsonProperty("plus_or_minus_one")
    public void setPlusOrMinusOne(Long plusOrMinusOne) {
        this.plusOrMinusOne = plusOrMinusOne;
    }

    @JsonProperty("plus_or_minus_two")
    public Long getPlusOrMinusTwo() {
        return plusOrMinusTwo;
    }

    @JsonProperty("plus_or_minus_two")
    public void setPlusOrMinusTwo(Long plusOrMinusTwo) {
        this.plusOrMinusTwo = plusOrMinusTwo;
    }

    @JsonProperty("publish_time")
    public String getPublishTime() {
        return publishTime;
    }

    @JsonProperty("publish_time")
    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    @JsonProperty("publish_timing")
    public String getPublishTiming() {
        return publishTiming;
    }

    @JsonProperty("publish_timing")
    public void setPublishTiming(String publishTiming) {
        this.publishTiming = publishTiming;
    }

    @JsonProperty("push_description")
    public String getPushDescription() {
        return pushDescription;
    }

    @JsonProperty("push_description")
    public void setPushDescription(String pushDescription) {
        this.pushDescription = pushDescription;
    }

    @JsonProperty("push_title")
    public String getPushTitle() {
        return pushTitle;
    }

    @JsonProperty("push_title")
    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    @JsonProperty("recommend_to_moji")
    public Boolean getRecommendToMoji() {
        return recommendToMoji;
    }

    @JsonProperty("recommend_to_moji")
    public void setRecommendToMoji(Boolean recommendToMoji) {
        this.recommendToMoji = recommendToMoji;
    }

    @JsonProperty("recommend_to_series")
    public Boolean getRecommendToSeries() {
        return recommendToSeries;
    }

    @JsonProperty("recommend_to_series")
    public void setRecommendToSeries(Boolean recommendToSeries) {
        this.recommendToSeries = recommendToSeries;
    }

    @JsonProperty("recommend_to_xny")
    public Boolean getRecommendToXny() {
        return recommendToXny;
    }

    @JsonProperty("recommend_to_xny")
    public void setRecommendToXny(Boolean recommendToXny) {
        this.recommendToXny = recommendToXny;
    }

    @JsonProperty("recycled")
    public Boolean getRecycled() {
        return recycled;
    }

    @JsonProperty("recycled")
    public void setRecycled(Boolean recycled) {
        this.recycled = recycled;
    }

    @JsonProperty("recycled_along_with_author")
    public Boolean getRecycledAlongWithAuthor() {
        return recycledAlongWithAuthor;
    }

    @JsonProperty("recycled_along_with_author")
    public void setRecycledAlongWithAuthor(Boolean recycledAlongWithAuthor) {
        this.recycledAlongWithAuthor = recycledAlongWithAuthor;
    }

    @JsonProperty("show_big_img")
    public Boolean getShowBigImg() {
        return showBigImg;
    }

    @JsonProperty("show_big_img")
    public void setShowBigImg(Boolean showBigImg) {
        this.showBigImg = showBigImg;
    }

    @JsonProperty("show_image_list")
    public Boolean getShowImageList() {
        return showImageList;
    }

    @JsonProperty("show_image_list")
    public void setShowImageList(Boolean showImageList) {
        this.showImageList = showImageList;
    }

    @JsonProperty("show_level")
    public Long getShowLevel() {
        return showLevel;
    }

    @JsonProperty("show_level")
    public void setShowLevel(Long showLevel) {
        this.showLevel = showLevel;
    }

    @JsonProperty("sort_type")
    public String getSortType() {
        return sortType;
    }

    @JsonProperty("sort_type")
    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    @JsonProperty("status")
    public Long getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Long status) {
        this.status = status;
    }

    @JsonProperty("status_note")
    public String getStatusNote() {
        return statusNote;
    }

    @JsonProperty("status_note")
    public void setStatusNote(String statusNote) {
        this.statusNote = statusNote;
    }

    @JsonProperty("status_note_id")
    public String getStatusNoteId() {
        return statusNoteId;
    }

    @JsonProperty("status_note_id")
    public void setStatusNoteId(String statusNoteId) {
        this.statusNoteId = statusNoteId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("title2")
    public String getTitle2() {
        return title2;
    }

    @JsonProperty("title2")
    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    @JsonProperty("title3")
    public String getTitle3() {
        return title3;
    }

    @JsonProperty("title3")
    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    @JsonProperty("version_source")
    public Long getVersionSource() {
        return versionSource;
    }

    @JsonProperty("version_source")
    public void setVersionSource(Long versionSource) {
        this.versionSource = versionSource;
    }

    @JsonProperty("wai_tui_time")
    public String getWaiTuiTime() {
        return waiTuiTime;
    }

    @JsonProperty("wai_tui_time")
    public void setWaiTuiTime(String waiTuiTime) {
        this.waiTuiTime = waiTuiTime;
    }

    @JsonProperty("wait_audit_time")
    public String getWaitAuditTime() {
        return waitAuditTime;
    }

    @JsonProperty("wait_audit_time")
    public void setWaitAuditTime(String waitAuditTime) {
        this.waitAuditTime = waitAuditTime;
    }

    @JsonProperty("wait_audit_time2")
    public String getWaitAuditTime2() {
        return waitAuditTime2;
    }

    @JsonProperty("wait_audit_time2")
    public void setWaitAuditTime2(String waitAuditTime2) {
        this.waitAuditTime2 = waitAuditTime2;
    }

    @JsonProperty("wait_audit_time3")
    public String getWaitAuditTime3() {
        return waitAuditTime3;
    }

    @JsonProperty("wait_audit_time3")
    public void setWaitAuditTime3(String waitAuditTime3) {
        this.waitAuditTime3 = waitAuditTime3;
    }

    @JsonProperty("you_xuan_time")
    public String getYouXuanTime() {
        return youXuanTime;
    }

    @JsonProperty("you_xuan_time")
    public void setYouXuanTime(String youXuanTime) {
        this.youXuanTime = youXuanTime;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Contents.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("adCity");
        sb.append('=');
        sb.append(((this.adCity == null)?"<null>":this.adCity));
        sb.append(',');
        sb.append("allowDelete");
        sb.append('=');
        sb.append(((this.allowDelete == null)?"<null>":this.allowDelete));
        sb.append(',');
        sb.append("auditNotes");
        sb.append('=');
        sb.append(((this.auditNotes == null)?"<null>":this.auditNotes));
        sb.append(',');
        sb.append("auditRecommend");
        sb.append('=');
        sb.append(((this.auditRecommend == null)?"<null>":this.auditRecommend));
        sb.append(',');
        sb.append("auditStage");
        sb.append('=');
        sb.append(((this.auditStage == null)?"<null>":this.auditStage));
        sb.append(',');
        sb.append("auditor");
        sb.append('=');
        sb.append(((this.auditor == null)?"<null>":this.auditor));
        sb.append(',');
        sb.append("authorId");
        sb.append('=');
        sb.append(((this.authorId == null)?"<null>":this.authorId));
        sb.append(',');
        sb.append("autoWriteAricleId");
        sb.append('=');
        sb.append(((this.autoWriteAricleId == null)?"<null>":this.autoWriteAricleId));
        sb.append(',');
        sb.append("autohomeUa");
        sb.append('=');
        sb.append(((this.autohomeUa == null)?"<null>":this.autohomeUa));
        sb.append(',');
        sb.append("cancelRecommendTime");
        sb.append('=');
        sb.append(((this.cancelRecommendTime == null)?"<null>":this.cancelRecommendTime));
        sb.append(',');
        sb.append("clientType");
        sb.append('=');
        sb.append(((this.clientType == null)?"<null>":this.clientType));
        sb.append(',');
        sb.append("conceptKeyWords");
        sb.append('=');
        sb.append(((this.conceptKeyWords == null)?"<null>":this.conceptKeyWords));
        sb.append(',');
        sb.append("configure");
        sb.append('=');
        sb.append(((this.configure == null)?"<null>":this.configure));
        sb.append(',');
        sb.append("contentType");
        sb.append('=');
        sb.append(((this.contentType == null)?"<null>":this.contentType));
        sb.append(',');
        sb.append("createTime");
        sb.append('=');
        sb.append(((this.createTime == null)?"<null>":this.createTime));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("description2");
        sb.append('=');
        sb.append(((this.description2 == null)?"<null>":this.description2));
        sb.append(',');
        sb.append("expireDate");
        sb.append('=');
        sb.append(((this.expireDate == null)?"<null>":this.expireDate));
        sb.append(',');
        sb.append("hailiangReason");
        sb.append('=');
        sb.append(((this.hailiangReason == null)?"<null>":this.hailiangReason));
        sb.append(',');
        sb.append("hailiangStatus");
        sb.append('=');
        sb.append(((this.hailiangStatus == null)?"<null>":this.hailiangStatus));
        sb.append(',');
        sb.append("hasReview");
        sb.append('=');
        sb.append(((this.hasReview == null)?"<null>":this.hasReview));
        sb.append(',');
        sb.append("hotSpotTag");
        sb.append('=');
        sb.append(((this.hotSpotTag == null)?"<null>":this.hotSpotTag));
        sb.append(',');
        sb.append("ident");
        sb.append('=');
        sb.append(((this.ident == null)?"<null>":this.ident));
        sb.append(',');
        sb.append("image");
        sb.append('=');
        sb.append(((this.image == null)?"<null>":this.image));
        sb.append(',');
        sb.append("image1");
        sb.append('=');
        sb.append(((this.image1 == null)?"<null>":this.image1));
        sb.append(',');
        sb.append("image2");
        sb.append('=');
        sb.append(((this.image2 == null)?"<null>":this.image2));
        sb.append(',');
        sb.append("image3");
        sb.append('=');
        sb.append(((this.image3 == null)?"<null>":this.image3));
        sb.append(',');
        sb.append("imgCross");
        sb.append('=');
        sb.append(((this.imgCross == null)?"<null>":this.imgCross));
        sb.append(',');
        sb.append("imgVertical");
        sb.append('=');
        sb.append(((this.imgVertical == null)?"<null>":this.imgVertical));
        sb.append(',');
        sb.append("infoId");
        sb.append('=');
        sb.append(((this.infoId == null)?"<null>":this.infoId));
        sb.append(',');
        sb.append("infoLevel");
        sb.append('=');
        sb.append(((this.infoLevel == null)?"<null>":this.infoLevel));
        sb.append(',');
        sb.append("infoType");
        sb.append('=');
        sb.append(((this.infoType == null)?"<null>":this.infoType));
        sb.append(',');
        sb.append("innovate");
        sb.append('=');
        sb.append(((this.innovate == null)?"<null>":this.innovate));
        sb.append(',');
        sb.append("ip");
        sb.append('=');
        sb.append(((this.ip == null)?"<null>":this.ip));
        sb.append(',');
        sb.append("ipProvince");
        sb.append('=');
        sb.append(((this.ipProvince == null)?"<null>":this.ipProvince));
        sb.append(',');
        sb.append("isArtificial");
        sb.append('=');
        sb.append(((this.isArtificial == null)?"<null>":this.isArtificial));
        sb.append(',');
        sb.append("isAutoWrite");
        sb.append('=');
        sb.append(((this.isAutoWrite == null)?"<null>":this.isAutoWrite));
        sb.append(',');
        sb.append("isBusiness");
        sb.append('=');
        sb.append(((this.isBusiness == null)?"<null>":this.isBusiness));
        sb.append(',');
        sb.append("isConvertText");
        sb.append('=');
        sb.append(((this.isConvertText == null)?"<null>":this.isConvertText));
        sb.append(',');
        sb.append("isDeduction");
        sb.append('=');
        sb.append(((this.isDeduction == null)?"<null>":this.isDeduction));
        sb.append(',');
        sb.append("isDel");
        sb.append('=');
        sb.append(((this.isDel == null)?"<null>":this.isDel));
        sb.append(',');
        sb.append("isExpose");
        sb.append('=');
        sb.append(((this.isExpose == null)?"<null>":this.isExpose));
        sb.append(',');
        sb.append("isFastestFirst");
        sb.append('=');
        sb.append(((this.isFastestFirst == null)?"<null>":this.isFastestFirst));
        sb.append(',');
        sb.append("isFirst");
        sb.append('=');
        sb.append(((this.isFirst == null)?"<null>":this.isFirst));
        sb.append(',');
        sb.append("isImportant");
        sb.append('=');
        sb.append(((this.isImportant == null)?"<null>":this.isImportant));
        sb.append(',');
        sb.append("isLocalAudit");
        sb.append('=');
        sb.append(((this.isLocalAudit == null)?"<null>":this.isLocalAudit));
        sb.append(',');
        sb.append("isLowCar");
        sb.append('=');
        sb.append(((this.isLowCar == null)?"<null>":this.isLowCar));
        sb.append(',');
        sb.append("isManualContentLevel");
        sb.append('=');
        sb.append(((this.isManualContentLevel == null)?"<null>":this.isManualContentLevel));
        sb.append(',');
        sb.append("isManualOrAi");
        sb.append('=');
        sb.append(((this.isManualOrAi == null)?"<null>":this.isManualOrAi));
        sb.append(',');
        sb.append("isOriginal");
        sb.append('=');
        sb.append(((this.isOriginal == null)?"<null>":this.isOriginal));
        sb.append(',');
        sb.append("isProductImage");
        sb.append('=');
        sb.append(((this.isProductImage == null)?"<null>":this.isProductImage));
        sb.append(',');
        sb.append("isPublish");
        sb.append('=');
        sb.append(((this.isPublish == null)?"<null>":this.isPublish));
        sb.append(',');
        sb.append("isPush");
        sb.append('=');
        sb.append(((this.isPush == null)?"<null>":this.isPush));
        sb.append(',');
        sb.append("isTiming");
        sb.append('=');
        sb.append(((this.isTiming == null)?"<null>":this.isTiming));
        sb.append(',');
        sb.append("isTop");
        sb.append('=');
        sb.append(((this.isTop == null)?"<null>":this.isTop));
        sb.append(',');
        sb.append("isWaiTui");
        sb.append('=');
        sb.append(((this.isWaiTui == null)?"<null>":this.isWaiTui));
        sb.append(',');
        sb.append("isYouXuan");
        sb.append('=');
        sb.append(((this.isYouXuan == null)?"<null>":this.isYouXuan));
        sb.append(',');
        sb.append("keyWords");
        sb.append('=');
        sb.append(((this.keyWords == null)?"<null>":this.keyWords));
        sb.append(',');
        sb.append("lastUpdateTime");
        sb.append('=');
        sb.append(((this.lastUpdateTime == null)?"<null>":this.lastUpdateTime));
        sb.append(',');
        sb.append("localAuditTime");
        sb.append('=');
        sb.append(((this.localAuditTime == null)?"<null>":this.localAuditTime));
        sb.append(',');
        sb.append("localRecommendTime");
        sb.append('=');
        sb.append(((this.localRecommendTime == null)?"<null>":this.localRecommendTime));
        sb.append(',');
        sb.append("localRecommendType");
        sb.append('=');
        sb.append(((this.localRecommendType == null)?"<null>":this.localRecommendType));
        sb.append(',');
        sb.append("newKeyWordIds");
        sb.append('=');
        sb.append(((this.newKeyWordIds == null)?"<null>":this.newKeyWordIds));
        sb.append(',');
        sb.append("newKeyWords");
        sb.append('=');
        sb.append(((this.newKeyWords == null)?"<null>":this.newKeyWords));
        sb.append(',');
        sb.append("operateRemarks");
        sb.append('=');
        sb.append(((this.operateRemarks == null)?"<null>":this.operateRemarks));
        sb.append(',');
        sb.append("plusOrMinusOne");
        sb.append('=');
        sb.append(((this.plusOrMinusOne == null)?"<null>":this.plusOrMinusOne));
        sb.append(',');
        sb.append("plusOrMinusTwo");
        sb.append('=');
        sb.append(((this.plusOrMinusTwo == null)?"<null>":this.plusOrMinusTwo));
        sb.append(',');
        sb.append("publishTime");
        sb.append('=');
        sb.append(((this.publishTime == null)?"<null>":this.publishTime));
        sb.append(',');
        sb.append("publishTiming");
        sb.append('=');
        sb.append(((this.publishTiming == null)?"<null>":this.publishTiming));
        sb.append(',');
        sb.append("pushDescription");
        sb.append('=');
        sb.append(((this.pushDescription == null)?"<null>":this.pushDescription));
        sb.append(',');
        sb.append("pushTitle");
        sb.append('=');
        sb.append(((this.pushTitle == null)?"<null>":this.pushTitle));
        sb.append(',');
        sb.append("recommendToMoji");
        sb.append('=');
        sb.append(((this.recommendToMoji == null)?"<null>":this.recommendToMoji));
        sb.append(',');
        sb.append("recommendToSeries");
        sb.append('=');
        sb.append(((this.recommendToSeries == null)?"<null>":this.recommendToSeries));
        sb.append(',');
        sb.append("recommendToXny");
        sb.append('=');
        sb.append(((this.recommendToXny == null)?"<null>":this.recommendToXny));
        sb.append(',');
        sb.append("recycled");
        sb.append('=');
        sb.append(((this.recycled == null)?"<null>":this.recycled));
        sb.append(',');
        sb.append("recycledAlongWithAuthor");
        sb.append('=');
        sb.append(((this.recycledAlongWithAuthor == null)?"<null>":this.recycledAlongWithAuthor));
        sb.append(',');
        sb.append("showBigImg");
        sb.append('=');
        sb.append(((this.showBigImg == null)?"<null>":this.showBigImg));
        sb.append(',');
        sb.append("showImageList");
        sb.append('=');
        sb.append(((this.showImageList == null)?"<null>":this.showImageList));
        sb.append(',');
        sb.append("showLevel");
        sb.append('=');
        sb.append(((this.showLevel == null)?"<null>":this.showLevel));
        sb.append(',');
        sb.append("sortType");
        sb.append('=');
        sb.append(((this.sortType == null)?"<null>":this.sortType));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
        sb.append(',');
        sb.append("statusNote");
        sb.append('=');
        sb.append(((this.statusNote == null)?"<null>":this.statusNote));
        sb.append(',');
        sb.append("statusNoteId");
        sb.append('=');
        sb.append(((this.statusNoteId == null)?"<null>":this.statusNoteId));
        sb.append(',');
        sb.append("title");
        sb.append('=');
        sb.append(((this.title == null)?"<null>":this.title));
        sb.append(',');
        sb.append("title2");
        sb.append('=');
        sb.append(((this.title2 == null)?"<null>":this.title2));
        sb.append(',');
        sb.append("title3");
        sb.append('=');
        sb.append(((this.title3 == null)?"<null>":this.title3));
        sb.append(',');
        sb.append("versionSource");
        sb.append('=');
        sb.append(((this.versionSource == null)?"<null>":this.versionSource));
        sb.append(',');
        sb.append("waiTuiTime");
        sb.append('=');
        sb.append(((this.waiTuiTime == null)?"<null>":this.waiTuiTime));
        sb.append(',');
        sb.append("waitAuditTime");
        sb.append('=');
        sb.append(((this.waitAuditTime == null)?"<null>":this.waitAuditTime));
        sb.append(',');
        sb.append("waitAuditTime2");
        sb.append('=');
        sb.append(((this.waitAuditTime2 == null)?"<null>":this.waitAuditTime2));
        sb.append(',');
        sb.append("waitAuditTime3");
        sb.append('=');
        sb.append(((this.waitAuditTime3 == null)?"<null>":this.waitAuditTime3));
        sb.append(',');
        sb.append("youXuanTime");
        sb.append('=');
        sb.append(((this.youXuanTime == null)?"<null>":this.youXuanTime));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
