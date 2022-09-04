
package com.bixuebihui.hit;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "app_url",
    "author_id",
    "author_img",
    "author_name",
    "biz_id",
    "brand_ids",
    "city_ids",
    "content",
    "content_level",
    "content_type",
    "created_stime",
    "description",
    "duration",
    "factory_ids",
    "global_id",
    "height",
    "img_url",
    "is_del",
    "is_off",
    "is_public",
    "koubei_label_names",
    "label_names",
    "like_count",
    "m_url",
    "media_id",
    "modified_stime",
    "multi_images",
    "pc_url",
    "play_url",
    "price",
    "publish_time",
    "pv",
    "reply_count",
    "series_ids",
    "size",
    "source",
    "source_biz_id",
    "spec_ids",
    "title",
    "topic_reply_count",
    "verify_status",
    "vv",
    "width",
    "year_ids"
})
@Generated("jsonschema2pojo")
public class SchemaNestedObject {

    @JsonProperty("app_url")
    private String appUrl;
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("author_img")
    private String authorImg;
    @JsonProperty("author_name")
    private String authorName;
    @JsonProperty("biz_id")
    private Long bizId;
    @JsonProperty("brand_ids")
    private List<Long> brandIds = new ArrayList<Long>();
    @JsonProperty("city_ids")
    private List<Long> cityIds = new ArrayList<Long>();
    @JsonProperty("content")
    private String content;
    @JsonProperty("content_level")
    private Long contentLevel;
    @JsonProperty("content_type")
    private Long contentType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "UTC")
    @JsonProperty("created_stime")
    private Date createdStime;
    @JsonProperty("description")
    private String description;
    @JsonProperty("duration")
    private Long duration;
    @JsonProperty("factory_ids")
    private List<Long> factoryIds = new ArrayList<Long>();
    @JsonProperty("global_id")
    private Long globalId;
    @JsonProperty("height")
    private Long height;
    @JsonProperty("img_url")
    private String imgUrl;
    @JsonProperty("is_del")
    private Long isDel;
    @JsonProperty("is_off")
    private Long isOff;
    @JsonProperty("is_public")
    private Long isPublic;
    @JsonProperty("koubei_label_names")
    private List<KoubeiLabelName> koubeiLabelNames = new ArrayList<KoubeiLabelName>();
    @JsonProperty("label_names")
    private List<String> labelNames = new ArrayList<String>();
    @JsonProperty("like_count")
    private Long likeCount;
    @JsonProperty("m_url")
    private String mUrl;
    @JsonProperty("media_id")
    private String mediaId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "UTC")
    @JsonProperty("modified_stime")
    private Date modifiedStime;
    @JsonProperty("multi_images")
    private String multiImages;
    @JsonProperty("pc_url")
    private String pcUrl;
    @JsonProperty("play_url")
    private List<String> playUrl = new ArrayList<String>();
    @JsonProperty("price")
    private Double price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "UTC")
    @JsonProperty("publish_time")
    private Date publishTime;
    @JsonProperty("pv")
    private Long pv;
    @JsonProperty("reply_count")
    private Long replyCount;
    @JsonProperty("series_ids")
    private List<Long> seriesIds = new ArrayList<Long>();
    @JsonProperty("size")
    private Long size;
    @JsonProperty("source")
    private Long source;
    @JsonProperty("source_biz_id")
    private Long sourceBizId;
    @JsonProperty("spec_ids")
    private List<Long> specIds = new ArrayList<Long>();
    @JsonProperty("title")
    private String title;
    @JsonProperty("topic_reply_count")
    private Long topicReplyCount;
    @JsonProperty("verify_status")
    private Long verifyStatus;
    @JsonProperty("vv")
    private Long vv;
    @JsonProperty("width")
    private Long width;
    @JsonProperty("year_ids")
    private List<Long> yearIds = new ArrayList<Long>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("app_url")
    public String getAppUrl() {
        return appUrl;
    }

    @JsonProperty("app_url")
    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    @JsonProperty("author_id")
    public Long getAuthorId() {
        return authorId;
    }

    @JsonProperty("author_id")
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @JsonProperty("author_img")
    public String getAuthorImg() {
        return authorImg;
    }

    @JsonProperty("author_img")
    public void setAuthorImg(String authorImg) {
        this.authorImg = authorImg;
    }

    @JsonProperty("author_name")
    public String getAuthorName() {
        return authorName;
    }

    @JsonProperty("author_name")
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @JsonProperty("biz_id")
    public Long getBizId() {
        return bizId;
    }

    @JsonProperty("biz_id")
    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    @JsonProperty("brand_ids")
    public List<Long> getBrandIds() {
        return brandIds;
    }

    @JsonProperty("brand_ids")
    public void setBrandIds(List<Long> brandIds) {
        this.brandIds = brandIds;
    }

    @JsonProperty("city_ids")
    public List<Long> getCityIds() {
        return cityIds;
    }

    @JsonProperty("city_ids")
    public void setCityIds(List<Long> cityIds) {
        this.cityIds = cityIds;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("content_level")
    public Long getContentLevel() {
        return contentLevel;
    }

    @JsonProperty("content_level")
    public void setContentLevel(Long contentLevel) {
        this.contentLevel = contentLevel;
    }

    @JsonProperty("content_type")
    public Long getContentType() {
        return contentType;
    }

    @JsonProperty("content_type")
    public void setContentType(Long contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("created_stime")
    public Date getCreatedStime() {
        return createdStime;
    }

    @JsonProperty("created_stime")
    public void setCreatedStime(Date createdStime) {
        this.createdStime = createdStime;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("duration")
    public Long getDuration() {
        return duration;
    }

    @JsonProperty("duration")
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @JsonProperty("factory_ids")
    public List<Long> getFactoryIds() {
        return factoryIds;
    }

    @JsonProperty("factory_ids")
    public void setFactoryIds(List<Long> factoryIds) {
        this.factoryIds = factoryIds;
    }

    @JsonProperty("global_id")
    public Long getGlobalId() {
        return globalId;
    }

    @JsonProperty("global_id")
    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    @JsonProperty("height")
    public Long getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(Long height) {
        this.height = height;
    }

    @JsonProperty("img_url")
    public String getImgUrl() {
        return imgUrl;
    }

    @JsonProperty("img_url")
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @JsonProperty("is_del")
    public Long getIsDel() {
        return isDel;
    }

    @JsonProperty("is_del")
    public void setIsDel(Long isDel) {
        this.isDel = isDel;
    }

    @JsonProperty("is_off")
    public Long getIsOff() {
        return isOff;
    }

    @JsonProperty("is_off")
    public void setIsOff(Long isOff) {
        this.isOff = isOff;
    }

    @JsonProperty("is_public")
    public Long getIsPublic() {
        return isPublic;
    }

    @JsonProperty("is_public")
    public void setIsPublic(Long isPublic) {
        this.isPublic = isPublic;
    }

    @JsonProperty("koubei_label_names")
    public List<KoubeiLabelName> getKoubeiLabelNames() {
        return koubeiLabelNames;
    }

    @JsonProperty("koubei_label_names")
    public void setKoubeiLabelNames(List<KoubeiLabelName> koubeiLabelNames) {
        this.koubeiLabelNames = koubeiLabelNames;
    }

    @JsonProperty("label_names")
    public List<String> getLabelNames() {
        return labelNames;
    }

    @JsonProperty("label_names")
    public void setLabelNames(List<String> labelNames) {
        this.labelNames = labelNames;
    }

    @JsonProperty("like_count")
    public Long getLikeCount() {
        return likeCount;
    }

    @JsonProperty("like_count")
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    @JsonProperty("m_url")
    public String getmUrl() {
        return mUrl;
    }

    @JsonProperty("m_url")
    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    @JsonProperty("media_id")
    public String getMediaId() {
        return mediaId;
    }

    @JsonProperty("media_id")
    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @JsonProperty("modified_stime")
    public Date getModifiedStime() {
        return modifiedStime;
    }

    @JsonProperty("modified_stime")
    public void setModifiedStime(Date modifiedStime) {
        this.modifiedStime = modifiedStime;
    }

    @JsonProperty("multi_images")
    public String getMultiImages() {
        return multiImages;
    }

    @JsonProperty("multi_images")
    public void setMultiImages(String multiImages) {
        this.multiImages = multiImages;
    }

    @JsonProperty("pc_url")
    public String getPcUrl() {
        return pcUrl;
    }

    @JsonProperty("pc_url")
    public void setPcUrl(String pcUrl) {
        this.pcUrl = pcUrl;
    }

    @JsonProperty("play_url")
    public List<String> getPlayUrl() {
        return playUrl;
    }

    @JsonProperty("play_url")
    public void setPlayUrl(List<String> playUrl) {
        this.playUrl = playUrl;
    }

    @JsonProperty("price")
    public Double getPrice() {
        return price;
    }

    @JsonProperty("price")
    public void setPrice(Double price) {
        this.price = price;
    }

    @JsonProperty("publish_time")
    public Date getPublishTime() {
        return publishTime;
    }

    @JsonProperty("publish_time")
    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    @JsonProperty("pv")
    public Long getPv() {
        return pv;
    }

    @JsonProperty("pv")
    public void setPv(Long pv) {
        this.pv = pv;
    }

    @JsonProperty("reply_count")
    public Long getReplyCount() {
        return replyCount;
    }

    @JsonProperty("reply_count")
    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    @JsonProperty("series_ids")
    public List<Long> getSeriesIds() {
        return seriesIds;
    }

    @JsonProperty("series_ids")
    public void setSeriesIds(List<Long> seriesIds) {
        this.seriesIds = seriesIds;
    }

    @JsonProperty("size")
    public Long getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Long size) {
        this.size = size;
    }

    @JsonProperty("source")
    public Long getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(Long source) {
        this.source = source;
    }

    @JsonProperty("source_biz_id")
    public Long getSourceBizId() {
        return sourceBizId;
    }

    @JsonProperty("source_biz_id")
    public void setSourceBizId(Long sourceBizId) {
        this.sourceBizId = sourceBizId;
    }

    @JsonProperty("spec_ids")
    public List<Long> getSpecIds() {
        return specIds;
    }

    @JsonProperty("spec_ids")
    public void setSpecIds(List<Long> specIds) {
        this.specIds = specIds;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("topic_reply_count")
    public Long getTopicReplyCount() {
        return topicReplyCount;
    }

    @JsonProperty("topic_reply_count")
    public void setTopicReplyCount(Long topicReplyCount) {
        this.topicReplyCount = topicReplyCount;
    }

    @JsonProperty("verify_status")
    public Long getVerifyStatus() {
        return verifyStatus;
    }

    @JsonProperty("verify_status")
    public void setVerifyStatus(Long verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    @JsonProperty("vv")
    public Long getVv() {
        return vv;
    }

    @JsonProperty("vv")
    public void setVv(Long vv) {
        this.vv = vv;
    }

    @JsonProperty("width")
    public Long getWidth() {
        return width;
    }

    @JsonProperty("width")
    public void setWidth(Long width) {
        this.width = width;
    }

    @JsonProperty("year_ids")
    public List<Long> getYearIds() {
        return yearIds;
    }

    @JsonProperty("year_ids")
    public void setYearIds(List<Long> yearIds) {
        this.yearIds = yearIds;
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
        sb.append(SchemaNestedObject.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("appUrl");
        sb.append('=');
        sb.append(((this.appUrl == null)?"<null>":this.appUrl));
        sb.append(',');
        sb.append("authorId");
        sb.append('=');
        sb.append(((this.authorId == null)?"<null>":this.authorId));
        sb.append(',');
        sb.append("authorImg");
        sb.append('=');
        sb.append(((this.authorImg == null)?"<null>":this.authorImg));
        sb.append(',');
        sb.append("authorName");
        sb.append('=');
        sb.append(((this.authorName == null)?"<null>":this.authorName));
        sb.append(',');
        sb.append("bizId");
        sb.append('=');
        sb.append(((this.bizId == null)?"<null>":this.bizId));
        sb.append(',');
        sb.append("brandIds");
        sb.append('=');
        sb.append(((this.brandIds == null)?"<null>":this.brandIds));
        sb.append(',');
        sb.append("cityIds");
        sb.append('=');
        sb.append(((this.cityIds == null)?"<null>":this.cityIds));
        sb.append(',');
        sb.append("content");
        sb.append('=');
        sb.append(((this.content == null)?"<null>":this.content));
        sb.append(',');
        sb.append("contentLevel");
        sb.append('=');
        sb.append(((this.contentLevel == null)?"<null>":this.contentLevel));
        sb.append(',');
        sb.append("contentType");
        sb.append('=');
        sb.append(((this.contentType == null)?"<null>":this.contentType));
        sb.append(',');
        sb.append("createdStime");
        sb.append('=');
        sb.append(((this.createdStime == null)?"<null>":this.createdStime));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("duration");
        sb.append('=');
        sb.append(((this.duration == null)?"<null>":this.duration));
        sb.append(',');
        sb.append("factoryIds");
        sb.append('=');
        sb.append(((this.factoryIds == null)?"<null>":this.factoryIds));
        sb.append(',');
        sb.append("globalId");
        sb.append('=');
        sb.append(((this.globalId == null)?"<null>":this.globalId));
        sb.append(',');
        sb.append("height");
        sb.append('=');
        sb.append(((this.height == null)?"<null>":this.height));
        sb.append(',');
        sb.append("imgUrl");
        sb.append('=');
        sb.append(((this.imgUrl == null)?"<null>":this.imgUrl));
        sb.append(',');
        sb.append("isDel");
        sb.append('=');
        sb.append(((this.isDel == null)?"<null>":this.isDel));
        sb.append(',');
        sb.append("isOff");
        sb.append('=');
        sb.append(((this.isOff == null)?"<null>":this.isOff));
        sb.append(',');
        sb.append("isPublic");
        sb.append('=');
        sb.append(((this.isPublic == null)?"<null>":this.isPublic));
        sb.append(',');
        sb.append("koubeiLabelNames");
        sb.append('=');
        sb.append(((this.koubeiLabelNames == null)?"<null>":this.koubeiLabelNames));
        sb.append(',');
        sb.append("labelNames");
        sb.append('=');
        sb.append(((this.labelNames == null)?"<null>":this.labelNames));
        sb.append(',');
        sb.append("likeCount");
        sb.append('=');
        sb.append(((this.likeCount == null)?"<null>":this.likeCount));
        sb.append(',');
        sb.append("mUrl");
        sb.append('=');
        sb.append(((this.mUrl == null)?"<null>":this.mUrl));
        sb.append(',');
        sb.append("mediaId");
        sb.append('=');
        sb.append(((this.mediaId == null)?"<null>":this.mediaId));
        sb.append(',');
        sb.append("modifiedStime");
        sb.append('=');
        sb.append(((this.modifiedStime == null)?"<null>":this.modifiedStime));
        sb.append(',');
        sb.append("multiImages");
        sb.append('=');
        sb.append(((this.multiImages == null)?"<null>":this.multiImages));
        sb.append(',');
        sb.append("pcUrl");
        sb.append('=');
        sb.append(((this.pcUrl == null)?"<null>":this.pcUrl));
        sb.append(',');
        sb.append("playUrl");
        sb.append('=');
        sb.append(((this.playUrl == null)?"<null>":this.playUrl));
        sb.append(',');
        sb.append("price");
        sb.append('=');
        sb.append(((this.price == null)?"<null>":this.price));
        sb.append(',');
        sb.append("publishTime");
        sb.append('=');
        sb.append(((this.publishTime == null)?"<null>":this.publishTime));
        sb.append(',');
        sb.append("pv");
        sb.append('=');
        sb.append(((this.pv == null)?"<null>":this.pv));
        sb.append(',');
        sb.append("replyCount");
        sb.append('=');
        sb.append(((this.replyCount == null)?"<null>":this.replyCount));
        sb.append(',');
        sb.append("seriesIds");
        sb.append('=');
        sb.append(((this.seriesIds == null)?"<null>":this.seriesIds));
        sb.append(',');
        sb.append("size");
        sb.append('=');
        sb.append(((this.size == null)?"<null>":this.size));
        sb.append(',');
        sb.append("source");
        sb.append('=');
        sb.append(((this.source == null)?"<null>":this.source));
        sb.append(',');
        sb.append("sourceBizId");
        sb.append('=');
        sb.append(((this.sourceBizId == null)?"<null>":this.sourceBizId));
        sb.append(',');
        sb.append("specIds");
        sb.append('=');
        sb.append(((this.specIds == null)?"<null>":this.specIds));
        sb.append(',');
        sb.append("title");
        sb.append('=');
        sb.append(((this.title == null)?"<null>":this.title));
        sb.append(',');
        sb.append("topicReplyCount");
        sb.append('=');
        sb.append(((this.topicReplyCount == null)?"<null>":this.topicReplyCount));
        sb.append(',');
        sb.append("verifyStatus");
        sb.append('=');
        sb.append(((this.verifyStatus == null)?"<null>":this.verifyStatus));
        sb.append(',');
        sb.append("vv");
        sb.append('=');
        sb.append(((this.vv == null)?"<null>":this.vv));
        sb.append(',');
        sb.append("width");
        sb.append('=');
        sb.append(((this.width == null)?"<null>":this.width));
        sb.append(',');
        sb.append("yearIds");
        sb.append('=');
        sb.append(((this.yearIds == null)?"<null>":this.yearIds));
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
        result = ((result* 31)+((this.vv == null)? 0 :this.vv.hashCode()));
        result = ((result* 31)+((this.labelNames == null)? 0 :this.labelNames.hashCode()));
        result = ((result* 31)+((this.createdStime == null)? 0 :this.createdStime.hashCode()));
        result = ((result* 31)+((this.pv == null)? 0 :this.pv.hashCode()));
        result = ((result* 31)+((this.contentLevel == null)? 0 :this.contentLevel.hashCode()));
        result = ((result* 31)+((this.description == null)? 0 :this.description.hashCode()));
        result = ((result* 31)+((this.likeCount == null)? 0 :this.likeCount.hashCode()));
        result = ((result* 31)+((this.authorImg == null)? 0 :this.authorImg.hashCode()));
        result = ((result* 31)+((this.source == null)? 0 :this.source.hashCode()));
        result = ((result* 31)+((this.mediaId == null)? 0 :this.mediaId.hashCode()));
        result = ((result* 31)+((this.title == null)? 0 :this.title.hashCode()));
        result = ((result* 31)+((this.yearIds == null)? 0 :this.yearIds.hashCode()));
        result = ((result* 31)+((this.brandIds == null)? 0 :this.brandIds.hashCode()));
        result = ((result* 31)+((this.seriesIds == null)? 0 :this.seriesIds.hashCode()));
        result = ((result* 31)+((this.content == null)? 0 :this.content.hashCode()));
        result = ((result* 31)+((this.factoryIds == null)? 0 :this.factoryIds.hashCode()));
        result = ((result* 31)+((this.playUrl == null)? 0 :this.playUrl.hashCode()));
        result = ((result* 31)+((this.duration == null)? 0 :this.duration.hashCode()));
        result = ((result* 31)+((this.modifiedStime == null)? 0 :this.modifiedStime.hashCode()));
        result = ((result* 31)+((this.multiImages == null)? 0 :this.multiImages.hashCode()));
        result = ((result* 31)+((this.topicReplyCount == null)? 0 :this.topicReplyCount.hashCode()));
        result = ((result* 31)+((this.verifyStatus == null)? 0 :this.verifyStatus.hashCode()));
        result = ((result* 31)+((this.price == null)? 0 :this.price.hashCode()));
        result = ((result* 31)+((this.bizId == null)? 0 :this.bizId.hashCode()));
        result = ((result* 31)+((this.isOff == null)? 0 :this.isOff.hashCode()));
        result = ((result* 31)+((this.isPublic == null)? 0 :this.isPublic.hashCode()));
        result = ((result* 31)+((this.cityIds == null)? 0 :this.cityIds.hashCode()));
        result = ((result* 31)+((this.contentType == null)? 0 :this.contentType.hashCode()));
        result = ((result* 31)+((this.height == null)? 0 :this.height.hashCode()));
        result = ((result* 31)+((this.publishTime == null)? 0 :this.publishTime.hashCode()));
        result = ((result* 31)+((this.pcUrl == null)? 0 :this.pcUrl.hashCode()));
        result = ((result* 31)+((this.sourceBizId == null)? 0 :this.sourceBizId.hashCode()));
        result = ((result* 31)+((this.koubeiLabelNames == null)? 0 :this.koubeiLabelNames.hashCode()));
        result = ((result* 31)+((this.globalId == null)? 0 :this.globalId.hashCode()));
        result = ((result* 31)+((this.appUrl == null)? 0 :this.appUrl.hashCode()));
        result = ((result* 31)+((this.authorId == null)? 0 :this.authorId.hashCode()));
        result = ((result* 31)+((this.imgUrl == null)? 0 :this.imgUrl.hashCode()));
        result = ((result* 31)+((this.mUrl == null)? 0 :this.mUrl.hashCode()));
        result = ((result* 31)+((this.replyCount == null)? 0 :this.replyCount.hashCode()));
        result = ((result* 31)+((this.size == null)? 0 :this.size.hashCode()));
        result = ((result* 31)+((this.specIds == null)? 0 :this.specIds.hashCode()));
        result = ((result* 31)+((this.authorName == null)? 0 :this.authorName.hashCode()));
        result = ((result* 31)+((this.width == null)? 0 :this.width.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.isDel == null)? 0 :this.isDel.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SchemaNestedObject) == false) {
            return false;
        }
        SchemaNestedObject rhs = ((SchemaNestedObject) other);
        return ((((((((((((((((((((((((((((((((((((((((((((((this.vv == rhs.vv)||((this.vv!= null)&&this.vv.equals(rhs.vv)))&&((this.labelNames == rhs.labelNames)||((this.labelNames!= null)&&this.labelNames.equals(rhs.labelNames))))&&((this.createdStime == rhs.createdStime)||((this.createdStime!= null)&&this.createdStime.equals(rhs.createdStime))))&&((this.pv == rhs.pv)||((this.pv!= null)&&this.pv.equals(rhs.pv))))&&((this.contentLevel == rhs.contentLevel)||((this.contentLevel!= null)&&this.contentLevel.equals(rhs.contentLevel))))&&((this.description == rhs.description)||((this.description!= null)&&this.description.equals(rhs.description))))&&((this.likeCount == rhs.likeCount)||((this.likeCount!= null)&&this.likeCount.equals(rhs.likeCount))))&&((this.authorImg == rhs.authorImg)||((this.authorImg!= null)&&this.authorImg.equals(rhs.authorImg))))&&((this.source == rhs.source)||((this.source!= null)&&this.source.equals(rhs.source))))&&((this.mediaId == rhs.mediaId)||((this.mediaId!= null)&&this.mediaId.equals(rhs.mediaId))))&&((this.title == rhs.title)||((this.title!= null)&&this.title.equals(rhs.title))))&&((this.yearIds == rhs.yearIds)||((this.yearIds!= null)&&this.yearIds.equals(rhs.yearIds))))&&((this.brandIds == rhs.brandIds)||((this.brandIds!= null)&&this.brandIds.equals(rhs.brandIds))))&&((this.seriesIds == rhs.seriesIds)||((this.seriesIds!= null)&&this.seriesIds.equals(rhs.seriesIds))))&&((this.content == rhs.content)||((this.content!= null)&&this.content.equals(rhs.content))))&&((this.factoryIds == rhs.factoryIds)||((this.factoryIds!= null)&&this.factoryIds.equals(rhs.factoryIds))))&&((this.playUrl == rhs.playUrl)||((this.playUrl!= null)&&this.playUrl.equals(rhs.playUrl))))&&((this.duration == rhs.duration)||((this.duration!= null)&&this.duration.equals(rhs.duration))))&&((this.modifiedStime == rhs.modifiedStime)||((this.modifiedStime!= null)&&this.modifiedStime.equals(rhs.modifiedStime))))&&((this.multiImages == rhs.multiImages)||((this.multiImages!= null)&&this.multiImages.equals(rhs.multiImages))))&&((this.topicReplyCount == rhs.topicReplyCount)||((this.topicReplyCount!= null)&&this.topicReplyCount.equals(rhs.topicReplyCount))))&&((this.verifyStatus == rhs.verifyStatus)||((this.verifyStatus!= null)&&this.verifyStatus.equals(rhs.verifyStatus))))&&((this.price == rhs.price)||((this.price!= null)&&this.price.equals(rhs.price))))&&((this.bizId == rhs.bizId)||((this.bizId!= null)&&this.bizId.equals(rhs.bizId))))&&((this.isOff == rhs.isOff)||((this.isOff!= null)&&this.isOff.equals(rhs.isOff))))&&((this.isPublic == rhs.isPublic)||((this.isPublic!= null)&&this.isPublic.equals(rhs.isPublic))))&&((this.cityIds == rhs.cityIds)||((this.cityIds!= null)&&this.cityIds.equals(rhs.cityIds))))&&((this.contentType == rhs.contentType)||((this.contentType!= null)&&this.contentType.equals(rhs.contentType))))&&((this.height == rhs.height)||((this.height!= null)&&this.height.equals(rhs.height))))&&((this.publishTime == rhs.publishTime)||((this.publishTime!= null)&&this.publishTime.equals(rhs.publishTime))))&&((this.pcUrl == rhs.pcUrl)||((this.pcUrl!= null)&&this.pcUrl.equals(rhs.pcUrl))))&&((this.sourceBizId == rhs.sourceBizId)||((this.sourceBizId!= null)&&this.sourceBizId.equals(rhs.sourceBizId))))&&((this.koubeiLabelNames == rhs.koubeiLabelNames)||((this.koubeiLabelNames!= null)&&this.koubeiLabelNames.equals(rhs.koubeiLabelNames))))&&((this.globalId == rhs.globalId)||((this.globalId!= null)&&this.globalId.equals(rhs.globalId))))&&((this.appUrl == rhs.appUrl)||((this.appUrl!= null)&&this.appUrl.equals(rhs.appUrl))))&&((this.authorId == rhs.authorId)||((this.authorId!= null)&&this.authorId.equals(rhs.authorId))))&&((this.imgUrl == rhs.imgUrl)||((this.imgUrl!= null)&&this.imgUrl.equals(rhs.imgUrl))))&&((this.mUrl == rhs.mUrl)||((this.mUrl!= null)&&this.mUrl.equals(rhs.mUrl))))&&((this.replyCount == rhs.replyCount)||((this.replyCount!= null)&&this.replyCount.equals(rhs.replyCount))))&&((this.size == rhs.size)||((this.size!= null)&&this.size.equals(rhs.size))))&&((this.specIds == rhs.specIds)||((this.specIds!= null)&&this.specIds.equals(rhs.specIds))))&&((this.authorName == rhs.authorName)||((this.authorName!= null)&&this.authorName.equals(rhs.authorName))))&&((this.width == rhs.width)||((this.width!= null)&&this.width.equals(rhs.width))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.isDel == rhs.isDel)||((this.isDel!= null)&&this.isDel.equals(rhs.isDel))));
    }

}
