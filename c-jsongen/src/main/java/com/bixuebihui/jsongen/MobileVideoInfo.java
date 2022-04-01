
package com.bixuebihui.jsongen;

import java.util.ArrayList;
import java.util.HashMap;
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
    "attribute_ids",
    "attribute_names",
    "author_id",
    "author_name",
    "brand_ids",
    "content_label_ids",
    "content_label_names",
    "cover_image",
    "cover_image_2_3",
    "created_stime",
    "duaration",
    "expire_date",
    "factory_ids",
    "first_frame",
    "is_del",
    "is_privacy",
    "is_public",
    "is_recommend",
    "label_ids",
    "label_names",
    "labeled",
    "make_score",
    "media_id",
    "off_video",
    "play_url",
    "series_ids",
    "style_label_ids",
    "style_label_names",
    "stytle_score",
    "title",
    "title_score",
    "total_score",
    "verify_status",
    "video_id",
    "video_source"
})
@Generated("jsonschema2pojo")
public class MobileVideoInfo {

    @JsonProperty("attribute_ids")
    private List<Long> attributeIds = new ArrayList<Long>();
    @JsonProperty("attribute_names")
    private List<String> attributeNames = new ArrayList<String>();
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("author_name")
    private String authorName;
    @JsonProperty("brand_ids")
    private List<Long> brandIds = new ArrayList<Long>();
    @JsonProperty("content_label_ids")
    private List<Long> contentLabelIds = new ArrayList<Long>();
    @JsonProperty("content_label_names")
    private List<String> contentLabelNames = new ArrayList<String>();
    @JsonProperty("cover_image")
    private String coverImage;
    @JsonProperty("cover_image_2_3")
    private String coverImage23;
    @JsonProperty("created_stime")
    private String createdStime;
    @JsonProperty("duaration")
    private Long duaration;
    @JsonProperty("expire_date")
    private String expireDate;
    @JsonProperty("factory_ids")
    private List<Long> factoryIds = new ArrayList<Long>();
    @JsonProperty("first_frame")
    private String firstFrame;
    @JsonProperty("is_del")
    private Long isDel;
    @JsonProperty("is_privacy")
    private Long isPrivacy;
    @JsonProperty("is_public")
    private Long isPublic;
    @JsonProperty("is_recommend")
    private Long isRecommend;
    @JsonProperty("label_ids")
    private List<Long> labelIds = new ArrayList<Long>();
    @JsonProperty("label_names")
    private List<String> labelNames = new ArrayList<String>();
    @JsonProperty("labeled")
    private Long labeled;
    @JsonProperty("make_score")
    private Double makeScore;
    @JsonProperty("media_id")
    private String mediaId;
    @JsonProperty("off_video")
    private Long offVideo;
    @JsonProperty("play_url")
    private String playUrl;
    @JsonProperty("series_ids")
    private List<Long> seriesIds = new ArrayList<Long>();
    @JsonProperty("style_label_ids")
    private List<Long> styleLabelIds = new ArrayList<Long>();
    @JsonProperty("style_label_names")
    private List<String> styleLabelNames = new ArrayList<String>();
    @JsonProperty("stytle_score")
    private Double stytleScore;
    @JsonProperty("title")
    private String title;
    @JsonProperty("title_score")
    private Double titleScore;
    @JsonProperty("total_score")
    private Double totalScore;
    @JsonProperty("verify_status")
    private Long verifyStatus;
    @JsonProperty("video_id")
    private Long videoId;
    @JsonProperty("video_source")
    private Long videoSource;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("attribute_ids")
    public List<Long> getAttributeIds() {
        return attributeIds;
    }

    @JsonProperty("attribute_ids")
    public void setAttributeIds(List<Long> attributeIds) {
        this.attributeIds = attributeIds;
    }

    @JsonProperty("attribute_names")
    public List<String> getAttributeNames() {
        return attributeNames;
    }

    @JsonProperty("attribute_names")
    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames;
    }

    @JsonProperty("author_id")
    public Long getAuthorId() {
        return authorId;
    }

    @JsonProperty("author_id")
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @JsonProperty("author_name")
    public String getAuthorName() {
        return authorName;
    }

    @JsonProperty("author_name")
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @JsonProperty("brand_ids")
    public List<Long> getBrandIds() {
        return brandIds;
    }

    @JsonProperty("brand_ids")
    public void setBrandIds(List<Long> brandIds) {
        this.brandIds = brandIds;
    }

    @JsonProperty("content_label_ids")
    public List<Long> getContentLabelIds() {
        return contentLabelIds;
    }

    @JsonProperty("content_label_ids")
    public void setContentLabelIds(List<Long> contentLabelIds) {
        this.contentLabelIds = contentLabelIds;
    }

    @JsonProperty("content_label_names")
    public List<String> getContentLabelNames() {
        return contentLabelNames;
    }

    @JsonProperty("content_label_names")
    public void setContentLabelNames(List<String> contentLabelNames) {
        this.contentLabelNames = contentLabelNames;
    }

    @JsonProperty("cover_image")
    public String getCoverImage() {
        return coverImage;
    }

    @JsonProperty("cover_image")
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    @JsonProperty("cover_image_2_3")
    public String getCoverImage23() {
        return coverImage23;
    }

    @JsonProperty("cover_image_2_3")
    public void setCoverImage23(String coverImage23) {
        this.coverImage23 = coverImage23;
    }

    @JsonProperty("created_stime")
    public String getCreatedStime() {
        return createdStime;
    }

    @JsonProperty("created_stime")
    public void setCreatedStime(String createdStime) {
        this.createdStime = createdStime;
    }

    @JsonProperty("duaration")
    public Long getDuaration() {
        return duaration;
    }

    @JsonProperty("duaration")
    public void setDuaration(Long duaration) {
        this.duaration = duaration;
    }

    @JsonProperty("expire_date")
    public String getExpireDate() {
        return expireDate;
    }

    @JsonProperty("expire_date")
    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    @JsonProperty("factory_ids")
    public List<Long> getFactoryIds() {
        return factoryIds;
    }

    @JsonProperty("factory_ids")
    public void setFactoryIds(List<Long> factoryIds) {
        this.factoryIds = factoryIds;
    }

    @JsonProperty("first_frame")
    public String getFirstFrame() {
        return firstFrame;
    }

    @JsonProperty("first_frame")
    public void setFirstFrame(String firstFrame) {
        this.firstFrame = firstFrame;
    }

    @JsonProperty("is_del")
    public Long getIsDel() {
        return isDel;
    }

    @JsonProperty("is_del")
    public void setIsDel(Long isDel) {
        this.isDel = isDel;
    }

    @JsonProperty("is_privacy")
    public Long getIsPrivacy() {
        return isPrivacy;
    }

    @JsonProperty("is_privacy")
    public void setIsPrivacy(Long isPrivacy) {
        this.isPrivacy = isPrivacy;
    }

    @JsonProperty("is_public")
    public Long getIsPublic() {
        return isPublic;
    }

    @JsonProperty("is_public")
    public void setIsPublic(Long isPublic) {
        this.isPublic = isPublic;
    }

    @JsonProperty("is_recommend")
    public Long getIsRecommend() {
        return isRecommend;
    }

    @JsonProperty("is_recommend")
    public void setIsRecommend(Long isRecommend) {
        this.isRecommend = isRecommend;
    }

    @JsonProperty("label_ids")
    public List<Long> getLabelIds() {
        return labelIds;
    }

    @JsonProperty("label_ids")
    public void setLabelIds(List<Long> labelIds) {
        this.labelIds = labelIds;
    }

    @JsonProperty("label_names")
    public List<String> getLabelNames() {
        return labelNames;
    }

    @JsonProperty("label_names")
    public void setLabelNames(List<String> labelNames) {
        this.labelNames = labelNames;
    }

    @JsonProperty("labeled")
    public Long getLabeled() {
        return labeled;
    }

    @JsonProperty("labeled")
    public void setLabeled(Long labeled) {
        this.labeled = labeled;
    }

    @JsonProperty("make_score")
    public Double getMakeScore() {
        return makeScore;
    }

    @JsonProperty("make_score")
    public void setMakeScore(Double makeScore) {
        this.makeScore = makeScore;
    }

    @JsonProperty("media_id")
    public String getMediaId() {
        return mediaId;
    }

    @JsonProperty("media_id")
    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @JsonProperty("off_video")
    public Long getOffVideo() {
        return offVideo;
    }

    @JsonProperty("off_video")
    public void setOffVideo(Long offVideo) {
        this.offVideo = offVideo;
    }

    @JsonProperty("play_url")
    public String getPlayUrl() {
        return playUrl;
    }

    @JsonProperty("play_url")
    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    @JsonProperty("series_ids")
    public List<Long> getSeriesIds() {
        return seriesIds;
    }

    @JsonProperty("series_ids")
    public void setSeriesIds(List<Long> seriesIds) {
        this.seriesIds = seriesIds;
    }

    @JsonProperty("style_label_ids")
    public List<Long> getStyleLabelIds() {
        return styleLabelIds;
    }

    @JsonProperty("style_label_ids")
    public void setStyleLabelIds(List<Long> styleLabelIds) {
        this.styleLabelIds = styleLabelIds;
    }

    @JsonProperty("style_label_names")
    public List<String> getStyleLabelNames() {
        return styleLabelNames;
    }

    @JsonProperty("style_label_names")
    public void setStyleLabelNames(List<String> styleLabelNames) {
        this.styleLabelNames = styleLabelNames;
    }

    @JsonProperty("stytle_score")
    public Double getStytleScore() {
        return stytleScore;
    }

    @JsonProperty("stytle_score")
    public void setStytleScore(Double stytleScore) {
        this.stytleScore = stytleScore;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("title_score")
    public Double getTitleScore() {
        return titleScore;
    }

    @JsonProperty("title_score")
    public void setTitleScore(Double titleScore) {
        this.titleScore = titleScore;
    }

    @JsonProperty("total_score")
    public Double getTotalScore() {
        return totalScore;
    }

    @JsonProperty("total_score")
    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    @JsonProperty("verify_status")
    public Long getVerifyStatus() {
        return verifyStatus;
    }

    @JsonProperty("verify_status")
    public void setVerifyStatus(Long verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    @JsonProperty("video_id")
    public Long getVideoId() {
        return videoId;
    }

    @JsonProperty("video_id")
    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    @JsonProperty("video_source")
    public Long getVideoSource() {
        return videoSource;
    }

    @JsonProperty("video_source")
    public void setVideoSource(Long videoSource) {
        this.videoSource = videoSource;
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
        sb.append(MobileVideoInfo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("attributeIds");
        sb.append('=');
        sb.append(((this.attributeIds == null)?"<null>":this.attributeIds));
        sb.append(',');
        sb.append("attributeNames");
        sb.append('=');
        sb.append(((this.attributeNames == null)?"<null>":this.attributeNames));
        sb.append(',');
        sb.append("authorId");
        sb.append('=');
        sb.append(((this.authorId == null)?"<null>":this.authorId));
        sb.append(',');
        sb.append("authorName");
        sb.append('=');
        sb.append(((this.authorName == null)?"<null>":this.authorName));
        sb.append(',');
        sb.append("brandIds");
        sb.append('=');
        sb.append(((this.brandIds == null)?"<null>":this.brandIds));
        sb.append(',');
        sb.append("contentLabelIds");
        sb.append('=');
        sb.append(((this.contentLabelIds == null)?"<null>":this.contentLabelIds));
        sb.append(',');
        sb.append("contentLabelNames");
        sb.append('=');
        sb.append(((this.contentLabelNames == null)?"<null>":this.contentLabelNames));
        sb.append(',');
        sb.append("coverImage");
        sb.append('=');
        sb.append(((this.coverImage == null)?"<null>":this.coverImage));
        sb.append(',');
        sb.append("coverImage23");
        sb.append('=');
        sb.append(((this.coverImage23 == null)?"<null>":this.coverImage23));
        sb.append(',');
        sb.append("createdStime");
        sb.append('=');
        sb.append(((this.createdStime == null)?"<null>":this.createdStime));
        sb.append(',');
        sb.append("duaration");
        sb.append('=');
        sb.append(((this.duaration == null)?"<null>":this.duaration));
        sb.append(',');
        sb.append("expireDate");
        sb.append('=');
        sb.append(((this.expireDate == null)?"<null>":this.expireDate));
        sb.append(',');
        sb.append("factoryIds");
        sb.append('=');
        sb.append(((this.factoryIds == null)?"<null>":this.factoryIds));
        sb.append(',');
        sb.append("firstFrame");
        sb.append('=');
        sb.append(((this.firstFrame == null)?"<null>":this.firstFrame));
        sb.append(',');
        sb.append("isDel");
        sb.append('=');
        sb.append(((this.isDel == null)?"<null>":this.isDel));
        sb.append(',');
        sb.append("isPrivacy");
        sb.append('=');
        sb.append(((this.isPrivacy == null)?"<null>":this.isPrivacy));
        sb.append(',');
        sb.append("isPublic");
        sb.append('=');
        sb.append(((this.isPublic == null)?"<null>":this.isPublic));
        sb.append(',');
        sb.append("isRecommend");
        sb.append('=');
        sb.append(((this.isRecommend == null)?"<null>":this.isRecommend));
        sb.append(',');
        sb.append("labelIds");
        sb.append('=');
        sb.append(((this.labelIds == null)?"<null>":this.labelIds));
        sb.append(',');
        sb.append("labelNames");
        sb.append('=');
        sb.append(((this.labelNames == null)?"<null>":this.labelNames));
        sb.append(',');
        sb.append("labeled");
        sb.append('=');
        sb.append(((this.labeled == null)?"<null>":this.labeled));
        sb.append(',');
        sb.append("makeScore");
        sb.append('=');
        sb.append(((this.makeScore == null)?"<null>":this.makeScore));
        sb.append(',');
        sb.append("mediaId");
        sb.append('=');
        sb.append(((this.mediaId == null)?"<null>":this.mediaId));
        sb.append(',');
        sb.append("offVideo");
        sb.append('=');
        sb.append(((this.offVideo == null)?"<null>":this.offVideo));
        sb.append(',');
        sb.append("playUrl");
        sb.append('=');
        sb.append(((this.playUrl == null)?"<null>":this.playUrl));
        sb.append(',');
        sb.append("seriesIds");
        sb.append('=');
        sb.append(((this.seriesIds == null)?"<null>":this.seriesIds));
        sb.append(',');
        sb.append("styleLabelIds");
        sb.append('=');
        sb.append(((this.styleLabelIds == null)?"<null>":this.styleLabelIds));
        sb.append(',');
        sb.append("styleLabelNames");
        sb.append('=');
        sb.append(((this.styleLabelNames == null)?"<null>":this.styleLabelNames));
        sb.append(',');
        sb.append("stytleScore");
        sb.append('=');
        sb.append(((this.stytleScore == null)?"<null>":this.stytleScore));
        sb.append(',');
        sb.append("title");
        sb.append('=');
        sb.append(((this.title == null)?"<null>":this.title));
        sb.append(',');
        sb.append("titleScore");
        sb.append('=');
        sb.append(((this.titleScore == null)?"<null>":this.titleScore));
        sb.append(',');
        sb.append("totalScore");
        sb.append('=');
        sb.append(((this.totalScore == null)?"<null>":this.totalScore));
        sb.append(',');
        sb.append("verifyStatus");
        sb.append('=');
        sb.append(((this.verifyStatus == null)?"<null>":this.verifyStatus));
        sb.append(',');
        sb.append("videoId");
        sb.append('=');
        sb.append(((this.videoId == null)?"<null>":this.videoId));
        sb.append(',');
        sb.append("videoSource");
        sb.append('=');
        sb.append(((this.videoSource == null)?"<null>":this.videoSource));
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

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.isRecommend == null)? 0 :this.isRecommend.hashCode()));
        result = ((result* 31)+((this.titleScore == null)? 0 :this.titleScore.hashCode()));
        result = ((result* 31)+((this.labelNames == null)? 0 :this.labelNames.hashCode()));
        result = ((result* 31)+((this.stytleScore == null)? 0 :this.stytleScore.hashCode()));
        result = ((result* 31)+((this.createdStime == null)? 0 :this.createdStime.hashCode()));
        result = ((result* 31)+((this.duaration == null)? 0 :this.duaration.hashCode()));
        result = ((result* 31)+((this.videoId == null)? 0 :this.videoId.hashCode()));
        result = ((result* 31)+((this.isPrivacy == null)? 0 :this.isPrivacy.hashCode()));
        result = ((result* 31)+((this.mediaId == null)? 0 :this.mediaId.hashCode()));
        result = ((result* 31)+((this.title == null)? 0 :this.title.hashCode()));
        result = ((result* 31)+((this.brandIds == null)? 0 :this.brandIds.hashCode()));
        result = ((result* 31)+((this.makeScore == null)? 0 :this.makeScore.hashCode()));
        result = ((result* 31)+((this.seriesIds == null)? 0 :this.seriesIds.hashCode()));
        result = ((result* 31)+((this.factoryIds == null)? 0 :this.factoryIds.hashCode()));
        result = ((result* 31)+((this.coverImage23 == null)? 0 :this.coverImage23 .hashCode()));
        result = ((result* 31)+((this.playUrl == null)? 0 :this.playUrl.hashCode()));
        result = ((result* 31)+((this.contentLabelIds == null)? 0 :this.contentLabelIds.hashCode()));
        result = ((result* 31)+((this.videoSource == null)? 0 :this.videoSource.hashCode()));
        result = ((result* 31)+((this.attributeNames == null)? 0 :this.attributeNames.hashCode()));
        result = ((result* 31)+((this.labelIds == null)? 0 :this.labelIds.hashCode()));
        result = ((result* 31)+((this.attributeIds == null)? 0 :this.attributeIds.hashCode()));
        result = ((result* 31)+((this.firstFrame == null)? 0 :this.firstFrame.hashCode()));
        result = ((result* 31)+((this.verifyStatus == null)? 0 :this.verifyStatus.hashCode()));
        result = ((result* 31)+((this.offVideo == null)? 0 :this.offVideo.hashCode()));
        result = ((result* 31)+((this.coverImage == null)? 0 :this.coverImage.hashCode()));
        result = ((result* 31)+((this.isPublic == null)? 0 :this.isPublic.hashCode()));
        result = ((result* 31)+((this.expireDate == null)? 0 :this.expireDate.hashCode()));
        result = ((result* 31)+((this.labeled == null)? 0 :this.labeled.hashCode()));
        result = ((result* 31)+((this.contentLabelNames == null)? 0 :this.contentLabelNames.hashCode()));
        result = ((result* 31)+((this.authorId == null)? 0 :this.authorId.hashCode()));
        result = ((result* 31)+((this.totalScore == null)? 0 :this.totalScore.hashCode()));
        result = ((result* 31)+((this.styleLabelIds == null)? 0 :this.styleLabelIds.hashCode()));
        result = ((result* 31)+((this.authorName == null)? 0 :this.authorName.hashCode()));
        result = ((result* 31)+((this.styleLabelNames == null)? 0 :this.styleLabelNames.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.isDel == null)? 0 :this.isDel.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MobileVideoInfo) == false) {
            return false;
        }
        MobileVideoInfo rhs = ((MobileVideoInfo) other);
        return (((((((((((((((((((((((((((((((((((((this.isRecommend == rhs.isRecommend)||((this.isRecommend!= null)&&this.isRecommend.equals(rhs.isRecommend)))&&((this.titleScore == rhs.titleScore)||((this.titleScore!= null)&&this.titleScore.equals(rhs.titleScore))))&&((this.labelNames == rhs.labelNames)||((this.labelNames!= null)&&this.labelNames.equals(rhs.labelNames))))&&((this.stytleScore == rhs.stytleScore)||((this.stytleScore!= null)&&this.stytleScore.equals(rhs.stytleScore))))&&((this.createdStime == rhs.createdStime)||((this.createdStime!= null)&&this.createdStime.equals(rhs.createdStime))))&&((this.duaration == rhs.duaration)||((this.duaration!= null)&&this.duaration.equals(rhs.duaration))))&&((this.videoId == rhs.videoId)||((this.videoId!= null)&&this.videoId.equals(rhs.videoId))))&&((this.isPrivacy == rhs.isPrivacy)||((this.isPrivacy!= null)&&this.isPrivacy.equals(rhs.isPrivacy))))&&((this.mediaId == rhs.mediaId)||((this.mediaId!= null)&&this.mediaId.equals(rhs.mediaId))))&&((this.title == rhs.title)||((this.title!= null)&&this.title.equals(rhs.title))))&&((this.brandIds == rhs.brandIds)||((this.brandIds!= null)&&this.brandIds.equals(rhs.brandIds))))&&((this.makeScore == rhs.makeScore)||((this.makeScore!= null)&&this.makeScore.equals(rhs.makeScore))))&&((this.seriesIds == rhs.seriesIds)||((this.seriesIds!= null)&&this.seriesIds.equals(rhs.seriesIds))))&&((this.factoryIds == rhs.factoryIds)||((this.factoryIds!= null)&&this.factoryIds.equals(rhs.factoryIds))))&&((this.coverImage23 == rhs.coverImage23)||((this.coverImage23 != null)&&this.coverImage23 .equals(rhs.coverImage23))))&&((this.playUrl == rhs.playUrl)||((this.playUrl!= null)&&this.playUrl.equals(rhs.playUrl))))&&((this.contentLabelIds == rhs.contentLabelIds)||((this.contentLabelIds!= null)&&this.contentLabelIds.equals(rhs.contentLabelIds))))&&((this.videoSource == rhs.videoSource)||((this.videoSource!= null)&&this.videoSource.equals(rhs.videoSource))))&&((this.attributeNames == rhs.attributeNames)||((this.attributeNames!= null)&&this.attributeNames.equals(rhs.attributeNames))))&&((this.labelIds == rhs.labelIds)||((this.labelIds!= null)&&this.labelIds.equals(rhs.labelIds))))&&((this.attributeIds == rhs.attributeIds)||((this.attributeIds!= null)&&this.attributeIds.equals(rhs.attributeIds))))&&((this.firstFrame == rhs.firstFrame)||((this.firstFrame!= null)&&this.firstFrame.equals(rhs.firstFrame))))&&((this.verifyStatus == rhs.verifyStatus)||((this.verifyStatus!= null)&&this.verifyStatus.equals(rhs.verifyStatus))))&&((this.offVideo == rhs.offVideo)||((this.offVideo!= null)&&this.offVideo.equals(rhs.offVideo))))&&((this.coverImage == rhs.coverImage)||((this.coverImage!= null)&&this.coverImage.equals(rhs.coverImage))))&&((this.isPublic == rhs.isPublic)||((this.isPublic!= null)&&this.isPublic.equals(rhs.isPublic))))&&((this.expireDate == rhs.expireDate)||((this.expireDate!= null)&&this.expireDate.equals(rhs.expireDate))))&&((this.labeled == rhs.labeled)||((this.labeled!= null)&&this.labeled.equals(rhs.labeled))))&&((this.contentLabelNames == rhs.contentLabelNames)||((this.contentLabelNames!= null)&&this.contentLabelNames.equals(rhs.contentLabelNames))))&&((this.authorId == rhs.authorId)||((this.authorId!= null)&&this.authorId.equals(rhs.authorId))))&&((this.totalScore == rhs.totalScore)||((this.totalScore!= null)&&this.totalScore.equals(rhs.totalScore))))&&((this.styleLabelIds == rhs.styleLabelIds)||((this.styleLabelIds!= null)&&this.styleLabelIds.equals(rhs.styleLabelIds))))&&((this.authorName == rhs.authorName)||((this.authorName!= null)&&this.authorName.equals(rhs.authorName))))&&((this.styleLabelNames == rhs.styleLabelNames)||((this.styleLabelNames!= null)&&this.styleLabelNames.equals(rhs.styleLabelNames))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.isDel == rhs.isDel)||((this.isDel!= null)&&this.isDel.equals(rhs.isDel))));
    }

}
