
package com.bixuebihui.hit;

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
    "app_url",
    "attribute_ids",
    "attribute_names",
    "author_id",
    "author_img",
    "author_level",
    "author_name",
    "author_sex",
    "bbs_id",
    "bbs_image",
    "bbs_name",
    "bbs_scheme",
    "biz_id",
    "brand_ids",
    "brand_names",
    "carseries_status",
    "city_ids",
    "clip_play_url",
    "community_status",
    "content",
    "content_level",
    "content_type",
    "created_stime",
    "default_img_url",
    "description",
    "duration",
    "expire_time",
    "factory_ids",
    "first_frame",
    "global_id",
    "height",
    "highlight_section",
    "img_url",
    "img_url_23",
    "imgurlheight",
    "imgurlwidth",
    "ip_location",
    "is_del",
    "is_labeled",
    "is_off",
    "is_privacy",
    "is_public",
    "is_recommend",
    "is_recommend_up",
    "koubei_boughtDate",
    "koubei_driveKilometer",
    "koubei_electricity",
    "koubei_label_names",
    "koubei_oilFeeling",
    "koubei_powerDrivenType",
    "koubei_routineCruisingRange",
    "koubei_winterCruisingRange",
    "label_ids",
    "label_names",
    "like_count",
    "m_url",
    "make_score",
    "media_id",
    "modified_stime",
    "multi_images",
    "new_content_type",
    "new_source",
    "off_video",
    "pc_url",
    "play_url",
    "positive_negative",
    "price",
    "price_type",
    "publish_time",
    "pv",
    "qa_author_id",
    "qa_author_img",
    "qa_author_name",
    "qa_author_sex",
    "query",
    "randombackgroundurl",
    "randomcolor",
    "reply_count",
    "script",
    "series_ids",
    "series_names",
    "shopping_time",
    "size",
    "source",
    "source_biz_id",
    "spec_ids",
    "spec_names",
    "style_label_ids",
    "style_label_names",
    "style_score",
    "sub_title",
    "title",
    "title_score",
    "topic_reply_count",
    "total_score",
    "verify_status",
    "videovertical",
    "vv",
    "width",
    "year_ids"
})
@Generated("jsonschema2pojo")
public class AllContentInfo {

    @JsonProperty("app_url")
    private String appUrl;
    @JsonProperty("attribute_ids")
    private List<Long> attributeIds = new ArrayList<Long>();
    @JsonProperty("attribute_names")
    private List<String> attributeNames = new ArrayList<String>();
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("author_img")
    private String authorImg;
    @JsonProperty("author_level")
    private Long authorLevel;
    @JsonProperty("author_name")
    private String authorName;
    @JsonProperty("author_sex")
    private Long authorSex;
    @JsonProperty("bbs_id")
    private Long bbsId;
    @JsonProperty("bbs_image")
    private String bbsImage;
    @JsonProperty("bbs_name")
    private String bbsName;
    @JsonProperty("bbs_scheme")
    private String bbsScheme;
    @JsonProperty("biz_id")
    private Long bizId;
    @JsonProperty("brand_ids")
    private List<Long> brandIds = new ArrayList<Long>();
    @JsonProperty("brand_names")
    private List<String> brandNames = new ArrayList<String>();
    @JsonProperty("carseries_status")
    private Long carseriesStatus;
    @JsonProperty("city_ids")
    private List<Long> cityIds = new ArrayList<Long>();
    @JsonProperty("clip_play_url")
    private String clipPlayUrl;
    @JsonProperty("community_status")
    private Long communityStatus;
    @JsonProperty("content")
    private String content;
    @JsonProperty("content_level")
    private Long contentLevel;
    @JsonProperty("content_type")
    private Long contentType;
    @JsonProperty("created_stime")
    private String createdStime;
    @JsonProperty("default_img_url")
    private String defaultImgUrl;
    @JsonProperty("description")
    private String description;
    @JsonProperty("duration")
    private Long duration;
    @JsonProperty("expire_time")
    private String expireTime;
    @JsonProperty("factory_ids")
    private List<Long> factoryIds = new ArrayList<Long>();
    @JsonProperty("first_frame")
    private String firstFrame;
    @JsonProperty("global_id")
    private Long globalId;
    @JsonProperty("height")
    private Long height;
    @JsonProperty("highlight_section")
    private String highlightSection;
    @JsonProperty("img_url")
    private String imgUrl;
    @JsonProperty("img_url_23")
    private String imgUrl23;
    @JsonProperty("imgurlheight")
    private Long imgurlheight;
    @JsonProperty("imgurlwidth")
    private Long imgurlwidth;
    @JsonProperty("ip_location")
    private String ipLocation;
    @JsonProperty("is_del")
    private Long isDel;
    @JsonProperty("is_labeled")
    private Long isLabeled;
    @JsonProperty("is_off")
    private Long isOff;
    @JsonProperty("is_privacy")
    private Long isPrivacy;
    @JsonProperty("is_public")
    private Long isPublic;
    @JsonProperty("is_recommend")
    private Long isRecommend;
    @JsonProperty("is_recommend_up")
    private Long isRecommendUp;
    @JsonProperty("koubei_boughtDate")
    private String koubeiBoughtDate;
    @JsonProperty("koubei_driveKilometer")
    private Double koubeiDriveKilometer;
    @JsonProperty("koubei_electricity")
    private Long koubeiElectricity;
    @JsonProperty("koubei_label_names")
    private List<KoubeiLabelName> koubeiLabelNames = new ArrayList<KoubeiLabelName>();
    @JsonProperty("koubei_oilFeeling")
    private String koubeiOilFeeling;
    @JsonProperty("koubei_powerDrivenType")
    private Long koubeiPowerDrivenType;
    @JsonProperty("koubei_routineCruisingRange")
    private Long koubeiRoutineCruisingRange;
    @JsonProperty("koubei_winterCruisingRange")
    private Long koubeiWinterCruisingRange;
    @JsonProperty("label_ids")
    private List<Long> labelIds = new ArrayList<Long>();
    @JsonProperty("label_names")
    private List<String> labelNames = new ArrayList<String>();
    @JsonProperty("like_count")
    private Long likeCount;
    @JsonProperty("m_url")
    private String mUrl;
    @JsonProperty("make_score")
    private Double makeScore;
    @JsonProperty("media_id")
    private String mediaId;
    @JsonProperty("modified_stime")
    private String modifiedStime;
    @JsonProperty("multi_images")
    private String multiImages;
    @JsonProperty("new_content_type")
    private Long newContentType;
    @JsonProperty("new_source")
    private Long newSource;
    @JsonProperty("off_video")
    private Long offVideo;
    @JsonProperty("pc_url")
    private String pcUrl;
    @JsonProperty("play_url")
    private List<String> playUrl = new ArrayList<String>();
    @JsonProperty("positive_negative")
    private Long positiveNegative;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("price_type")
    private Long priceType;
    @JsonProperty("publish_time")
    private String publishTime;
    @JsonProperty("pv")
    private Long pv;
    @JsonProperty("qa_author_id")
    private Long qaAuthorId;
    @JsonProperty("qa_author_img")
    private String qaAuthorImg;
    @JsonProperty("qa_author_name")
    private String qaAuthorName;
    @JsonProperty("qa_author_sex")
    private Long qaAuthorSex;
    @JsonProperty("query")
    private Object query;
    @JsonProperty("randombackgroundurl")
    private String randombackgroundurl;
    @JsonProperty("randomcolor")
    private String randomcolor;
    @JsonProperty("reply_count")
    private Long replyCount;
    @JsonProperty("script")
    private Object script;
    @JsonProperty("series_ids")
    private List<Long> seriesIds = new ArrayList<Long>();
    @JsonProperty("series_names")
    private List<String> seriesNames = new ArrayList<String>();
    @JsonProperty("shopping_time")
    private String shoppingTime;
    @JsonProperty("size")
    private Long size;
    @JsonProperty("source")
    private Long source;
    @JsonProperty("source_biz_id")
    private Long sourceBizId;
    @JsonProperty("spec_ids")
    private List<Long> specIds = new ArrayList<Long>();
    @JsonProperty("spec_names")
    private List<String> specNames = new ArrayList<String>();
    @JsonProperty("style_label_ids")
    private List<Long> styleLabelIds = new ArrayList<Long>();
    @JsonProperty("style_label_names")
    private List<String> styleLabelNames = new ArrayList<String>();
    @JsonProperty("style_score")
    private Double styleScore;
    @JsonProperty("sub_title")
    private String subTitle;
    @JsonProperty("title")
    private String title;
    @JsonProperty("title_score")
    private Double titleScore;
    @JsonProperty("topic_reply_count")
    private Long topicReplyCount;
    @JsonProperty("total_score")
    private Double totalScore;
    @JsonProperty("verify_status")
    private Long verifyStatus;
    @JsonProperty("videovertical")
    private Long videovertical;
    @JsonProperty("vv")
    private Long vv;
    @JsonProperty("width")
    private Long width;
    @JsonProperty("year_ids")
    private List<Long> yearIds = new ArrayList<Long>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("app_url")
    public String getAppUrl() {
        return appUrl;
    }

    @JsonProperty("app_url")
    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

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

    @JsonProperty("author_img")
    public String getAuthorImg() {
        return authorImg;
    }

    @JsonProperty("author_img")
    public void setAuthorImg(String authorImg) {
        this.authorImg = authorImg;
    }

    @JsonProperty("author_level")
    public Long getAuthorLevel() {
        return authorLevel;
    }

    @JsonProperty("author_level")
    public void setAuthorLevel(Long authorLevel) {
        this.authorLevel = authorLevel;
    }

    @JsonProperty("author_name")
    public String getAuthorName() {
        return authorName;
    }

    @JsonProperty("author_name")
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @JsonProperty("author_sex")
    public Long getAuthorSex() {
        return authorSex;
    }

    @JsonProperty("author_sex")
    public void setAuthorSex(Long authorSex) {
        this.authorSex = authorSex;
    }

    @JsonProperty("bbs_id")
    public Long getBbsId() {
        return bbsId;
    }

    @JsonProperty("bbs_id")
    public void setBbsId(Long bbsId) {
        this.bbsId = bbsId;
    }

    @JsonProperty("bbs_image")
    public String getBbsImage() {
        return bbsImage;
    }

    @JsonProperty("bbs_image")
    public void setBbsImage(String bbsImage) {
        this.bbsImage = bbsImage;
    }

    @JsonProperty("bbs_name")
    public String getBbsName() {
        return bbsName;
    }

    @JsonProperty("bbs_name")
    public void setBbsName(String bbsName) {
        this.bbsName = bbsName;
    }

    @JsonProperty("bbs_scheme")
    public String getBbsScheme() {
        return bbsScheme;
    }

    @JsonProperty("bbs_scheme")
    public void setBbsScheme(String bbsScheme) {
        this.bbsScheme = bbsScheme;
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

    @JsonProperty("brand_names")
    public List<String> getBrandNames() {
        return brandNames;
    }

    @JsonProperty("brand_names")
    public void setBrandNames(List<String> brandNames) {
        this.brandNames = brandNames;
    }

    @JsonProperty("carseries_status")
    public Long getCarseriesStatus() {
        return carseriesStatus;
    }

    @JsonProperty("carseries_status")
    public void setCarseriesStatus(Long carseriesStatus) {
        this.carseriesStatus = carseriesStatus;
    }

    @JsonProperty("city_ids")
    public List<Long> getCityIds() {
        return cityIds;
    }

    @JsonProperty("city_ids")
    public void setCityIds(List<Long> cityIds) {
        this.cityIds = cityIds;
    }

    @JsonProperty("clip_play_url")
    public String getClipPlayUrl() {
        return clipPlayUrl;
    }

    @JsonProperty("clip_play_url")
    public void setClipPlayUrl(String clipPlayUrl) {
        this.clipPlayUrl = clipPlayUrl;
    }

    @JsonProperty("community_status")
    public Long getCommunityStatus() {
        return communityStatus;
    }

    @JsonProperty("community_status")
    public void setCommunityStatus(Long communityStatus) {
        this.communityStatus = communityStatus;
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
    public String getCreatedStime() {
        return createdStime;
    }

    @JsonProperty("created_stime")
    public void setCreatedStime(String createdStime) {
        this.createdStime = createdStime;
    }

    @JsonProperty("default_img_url")
    public String getDefaultImgUrl() {
        return defaultImgUrl;
    }

    @JsonProperty("default_img_url")
    public void setDefaultImgUrl(String defaultImgUrl) {
        this.defaultImgUrl = defaultImgUrl;
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

    @JsonProperty("expire_time")
    public String getExpireTime() {
        return expireTime;
    }

    @JsonProperty("expire_time")
    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
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

    @JsonProperty("highlight_section")
    public String getHighlightSection() {
        return highlightSection;
    }

    @JsonProperty("highlight_section")
    public void setHighlightSection(String highlightSection) {
        this.highlightSection = highlightSection;
    }

    @JsonProperty("img_url")
    public String getImgUrl() {
        return imgUrl;
    }

    @JsonProperty("img_url")
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @JsonProperty("img_url_23")
    public String getImgUrl23() {
        return imgUrl23;
    }

    @JsonProperty("img_url_23")
    public void setImgUrl23(String imgUrl23) {
        this.imgUrl23 = imgUrl23;
    }

    @JsonProperty("imgurlheight")
    public Long getImgurlheight() {
        return imgurlheight;
    }

    @JsonProperty("imgurlheight")
    public void setImgurlheight(Long imgurlheight) {
        this.imgurlheight = imgurlheight;
    }

    @JsonProperty("imgurlwidth")
    public Long getImgurlwidth() {
        return imgurlwidth;
    }

    @JsonProperty("imgurlwidth")
    public void setImgurlwidth(Long imgurlwidth) {
        this.imgurlwidth = imgurlwidth;
    }

    @JsonProperty("ip_location")
    public String getIpLocation() {
        return ipLocation;
    }

    @JsonProperty("ip_location")
    public void setIpLocation(String ipLocation) {
        this.ipLocation = ipLocation;
    }

    @JsonProperty("is_del")
    public Long getIsDel() {
        return isDel;
    }

    @JsonProperty("is_del")
    public void setIsDel(Long isDel) {
        this.isDel = isDel;
    }

    @JsonProperty("is_labeled")
    public Long getIsLabeled() {
        return isLabeled;
    }

    @JsonProperty("is_labeled")
    public void setIsLabeled(Long isLabeled) {
        this.isLabeled = isLabeled;
    }

    @JsonProperty("is_off")
    public Long getIsOff() {
        return isOff;
    }

    @JsonProperty("is_off")
    public void setIsOff(Long isOff) {
        this.isOff = isOff;
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

    @JsonProperty("is_recommend_up")
    public Long getIsRecommendUp() {
        return isRecommendUp;
    }

    @JsonProperty("is_recommend_up")
    public void setIsRecommendUp(Long isRecommendUp) {
        this.isRecommendUp = isRecommendUp;
    }

    @JsonProperty("koubei_boughtDate")
    public String getKoubeiBoughtDate() {
        return koubeiBoughtDate;
    }

    @JsonProperty("koubei_boughtDate")
    public void setKoubeiBoughtDate(String koubeiBoughtDate) {
        this.koubeiBoughtDate = koubeiBoughtDate;
    }

    @JsonProperty("koubei_driveKilometer")
    public Double getKoubeiDriveKilometer() {
        return koubeiDriveKilometer;
    }

    @JsonProperty("koubei_driveKilometer")
    public void setKoubeiDriveKilometer(Double koubeiDriveKilometer) {
        this.koubeiDriveKilometer = koubeiDriveKilometer;
    }

    @JsonProperty("koubei_electricity")
    public Long getKoubeiElectricity() {
        return koubeiElectricity;
    }

    @JsonProperty("koubei_electricity")
    public void setKoubeiElectricity(Long koubeiElectricity) {
        this.koubeiElectricity = koubeiElectricity;
    }

    @JsonProperty("koubei_label_names")
    public List<KoubeiLabelName> getKoubeiLabelNames() {
        return koubeiLabelNames;
    }

    @JsonProperty("koubei_label_names")
    public void setKoubeiLabelNames(List<KoubeiLabelName> koubeiLabelNames) {
        this.koubeiLabelNames = koubeiLabelNames;
    }

    @JsonProperty("koubei_oilFeeling")
    public String getKoubeiOilFeeling() {
        return koubeiOilFeeling;
    }

    @JsonProperty("koubei_oilFeeling")
    public void setKoubeiOilFeeling(String koubeiOilFeeling) {
        this.koubeiOilFeeling = koubeiOilFeeling;
    }

    @JsonProperty("koubei_powerDrivenType")
    public Long getKoubeiPowerDrivenType() {
        return koubeiPowerDrivenType;
    }

    @JsonProperty("koubei_powerDrivenType")
    public void setKoubeiPowerDrivenType(Long koubeiPowerDrivenType) {
        this.koubeiPowerDrivenType = koubeiPowerDrivenType;
    }

    @JsonProperty("koubei_routineCruisingRange")
    public Long getKoubeiRoutineCruisingRange() {
        return koubeiRoutineCruisingRange;
    }

    @JsonProperty("koubei_routineCruisingRange")
    public void setKoubeiRoutineCruisingRange(Long koubeiRoutineCruisingRange) {
        this.koubeiRoutineCruisingRange = koubeiRoutineCruisingRange;
    }

    @JsonProperty("koubei_winterCruisingRange")
    public Long getKoubeiWinterCruisingRange() {
        return koubeiWinterCruisingRange;
    }

    @JsonProperty("koubei_winterCruisingRange")
    public void setKoubeiWinterCruisingRange(Long koubeiWinterCruisingRange) {
        this.koubeiWinterCruisingRange = koubeiWinterCruisingRange;
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

    @JsonProperty("modified_stime")
    public String getModifiedStime() {
        return modifiedStime;
    }

    @JsonProperty("modified_stime")
    public void setModifiedStime(String modifiedStime) {
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

    @JsonProperty("new_content_type")
    public Long getNewContentType() {
        return newContentType;
    }

    @JsonProperty("new_content_type")
    public void setNewContentType(Long newContentType) {
        this.newContentType = newContentType;
    }

    @JsonProperty("new_source")
    public Long getNewSource() {
        return newSource;
    }

    @JsonProperty("new_source")
    public void setNewSource(Long newSource) {
        this.newSource = newSource;
    }

    @JsonProperty("off_video")
    public Long getOffVideo() {
        return offVideo;
    }

    @JsonProperty("off_video")
    public void setOffVideo(Long offVideo) {
        this.offVideo = offVideo;
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

    @JsonProperty("positive_negative")
    public Long getPositiveNegative() {
        return positiveNegative;
    }

    @JsonProperty("positive_negative")
    public void setPositiveNegative(Long positiveNegative) {
        this.positiveNegative = positiveNegative;
    }

    @JsonProperty("price")
    public Double getPrice() {
        return price;
    }

    @JsonProperty("price")
    public void setPrice(Double price) {
        this.price = price;
    }

    @JsonProperty("price_type")
    public Long getPriceType() {
        return priceType;
    }

    @JsonProperty("price_type")
    public void setPriceType(Long priceType) {
        this.priceType = priceType;
    }

    @JsonProperty("publish_time")
    public String getPublishTime() {
        return publishTime;
    }

    @JsonProperty("publish_time")
    public void setPublishTime(String publishTime) {
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

    @JsonProperty("qa_author_id")
    public Long getQaAuthorId() {
        return qaAuthorId;
    }

    @JsonProperty("qa_author_id")
    public void setQaAuthorId(Long qaAuthorId) {
        this.qaAuthorId = qaAuthorId;
    }

    @JsonProperty("qa_author_img")
    public String getQaAuthorImg() {
        return qaAuthorImg;
    }

    @JsonProperty("qa_author_img")
    public void setQaAuthorImg(String qaAuthorImg) {
        this.qaAuthorImg = qaAuthorImg;
    }

    @JsonProperty("qa_author_name")
    public String getQaAuthorName() {
        return qaAuthorName;
    }

    @JsonProperty("qa_author_name")
    public void setQaAuthorName(String qaAuthorName) {
        this.qaAuthorName = qaAuthorName;
    }

    @JsonProperty("qa_author_sex")
    public Long getQaAuthorSex() {
        return qaAuthorSex;
    }

    @JsonProperty("qa_author_sex")
    public void setQaAuthorSex(Long qaAuthorSex) {
        this.qaAuthorSex = qaAuthorSex;
    }

    @JsonProperty("query")
    public Object getQuery() {
        return query;
    }

    @JsonProperty("query")
    public void setQuery(Object query) {
        this.query = query;
    }

    @JsonProperty("randombackgroundurl")
    public String getRandombackgroundurl() {
        return randombackgroundurl;
    }

    @JsonProperty("randombackgroundurl")
    public void setRandombackgroundurl(String randombackgroundurl) {
        this.randombackgroundurl = randombackgroundurl;
    }

    @JsonProperty("randomcolor")
    public String getRandomcolor() {
        return randomcolor;
    }

    @JsonProperty("randomcolor")
    public void setRandomcolor(String randomcolor) {
        this.randomcolor = randomcolor;
    }

    @JsonProperty("reply_count")
    public Long getReplyCount() {
        return replyCount;
    }

    @JsonProperty("reply_count")
    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    @JsonProperty("script")
    public Object getScript() {
        return script;
    }

    @JsonProperty("script")
    public void setScript(Object script) {
        this.script = script;
    }

    @JsonProperty("series_ids")
    public List<Long> getSeriesIds() {
        return seriesIds;
    }

    @JsonProperty("series_ids")
    public void setSeriesIds(List<Long> seriesIds) {
        this.seriesIds = seriesIds;
    }

    @JsonProperty("series_names")
    public List<String> getSeriesNames() {
        return seriesNames;
    }

    @JsonProperty("series_names")
    public void setSeriesNames(List<String> seriesNames) {
        this.seriesNames = seriesNames;
    }

    @JsonProperty("shopping_time")
    public String getShoppingTime() {
        return shoppingTime;
    }

    @JsonProperty("shopping_time")
    public void setShoppingTime(String shoppingTime) {
        this.shoppingTime = shoppingTime;
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

    @JsonProperty("spec_names")
    public List<String> getSpecNames() {
        return specNames;
    }

    @JsonProperty("spec_names")
    public void setSpecNames(List<String> specNames) {
        this.specNames = specNames;
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

    @JsonProperty("style_score")
    public Double getStyleScore() {
        return styleScore;
    }

    @JsonProperty("style_score")
    public void setStyleScore(Double styleScore) {
        this.styleScore = styleScore;
    }

    @JsonProperty("sub_title")
    public String getSubTitle() {
        return subTitle;
    }

    @JsonProperty("sub_title")
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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

    @JsonProperty("topic_reply_count")
    public Long getTopicReplyCount() {
        return topicReplyCount;
    }

    @JsonProperty("topic_reply_count")
    public void setTopicReplyCount(Long topicReplyCount) {
        this.topicReplyCount = topicReplyCount;
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

    @JsonProperty("videovertical")
    public Long getVideovertical() {
        return videovertical;
    }

    @JsonProperty("videovertical")
    public void setVideovertical(Long videovertical) {
        this.videovertical = videovertical;
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
        sb.append(AllContentInfo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("appUrl");
        sb.append('=');
        sb.append(((this.appUrl == null)?"<null>":this.appUrl));
        sb.append(',');
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
        sb.append("authorImg");
        sb.append('=');
        sb.append(((this.authorImg == null)?"<null>":this.authorImg));
        sb.append(',');
        sb.append("authorLevel");
        sb.append('=');
        sb.append(((this.authorLevel == null)?"<null>":this.authorLevel));
        sb.append(',');
        sb.append("authorName");
        sb.append('=');
        sb.append(((this.authorName == null)?"<null>":this.authorName));
        sb.append(',');
        sb.append("authorSex");
        sb.append('=');
        sb.append(((this.authorSex == null)?"<null>":this.authorSex));
        sb.append(',');
        sb.append("bbsId");
        sb.append('=');
        sb.append(((this.bbsId == null)?"<null>":this.bbsId));
        sb.append(',');
        sb.append("bbsImage");
        sb.append('=');
        sb.append(((this.bbsImage == null)?"<null>":this.bbsImage));
        sb.append(',');
        sb.append("bbsName");
        sb.append('=');
        sb.append(((this.bbsName == null)?"<null>":this.bbsName));
        sb.append(',');
        sb.append("bbsScheme");
        sb.append('=');
        sb.append(((this.bbsScheme == null)?"<null>":this.bbsScheme));
        sb.append(',');
        sb.append("bizId");
        sb.append('=');
        sb.append(((this.bizId == null)?"<null>":this.bizId));
        sb.append(',');
        sb.append("brandIds");
        sb.append('=');
        sb.append(((this.brandIds == null)?"<null>":this.brandIds));
        sb.append(',');
        sb.append("brandNames");
        sb.append('=');
        sb.append(((this.brandNames == null)?"<null>":this.brandNames));
        sb.append(',');
        sb.append("carseriesStatus");
        sb.append('=');
        sb.append(((this.carseriesStatus == null)?"<null>":this.carseriesStatus));
        sb.append(',');
        sb.append("cityIds");
        sb.append('=');
        sb.append(((this.cityIds == null)?"<null>":this.cityIds));
        sb.append(',');
        sb.append("clipPlayUrl");
        sb.append('=');
        sb.append(((this.clipPlayUrl == null)?"<null>":this.clipPlayUrl));
        sb.append(',');
        sb.append("communityStatus");
        sb.append('=');
        sb.append(((this.communityStatus == null)?"<null>":this.communityStatus));
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
        sb.append("defaultImgUrl");
        sb.append('=');
        sb.append(((this.defaultImgUrl == null)?"<null>":this.defaultImgUrl));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("duration");
        sb.append('=');
        sb.append(((this.duration == null)?"<null>":this.duration));
        sb.append(',');
        sb.append("expireTime");
        sb.append('=');
        sb.append(((this.expireTime == null)?"<null>":this.expireTime));
        sb.append(',');
        sb.append("factoryIds");
        sb.append('=');
        sb.append(((this.factoryIds == null)?"<null>":this.factoryIds));
        sb.append(',');
        sb.append("firstFrame");
        sb.append('=');
        sb.append(((this.firstFrame == null)?"<null>":this.firstFrame));
        sb.append(',');
        sb.append("globalId");
        sb.append('=');
        sb.append(((this.globalId == null)?"<null>":this.globalId));
        sb.append(',');
        sb.append("height");
        sb.append('=');
        sb.append(((this.height == null)?"<null>":this.height));
        sb.append(',');
        sb.append("highlightSection");
        sb.append('=');
        sb.append(((this.highlightSection == null)?"<null>":this.highlightSection));
        sb.append(',');
        sb.append("imgUrl");
        sb.append('=');
        sb.append(((this.imgUrl == null)?"<null>":this.imgUrl));
        sb.append(',');
        sb.append("imgUrl23");
        sb.append('=');
        sb.append(((this.imgUrl23 == null)?"<null>":this.imgUrl23));
        sb.append(',');
        sb.append("imgurlheight");
        sb.append('=');
        sb.append(((this.imgurlheight == null)?"<null>":this.imgurlheight));
        sb.append(',');
        sb.append("imgurlwidth");
        sb.append('=');
        sb.append(((this.imgurlwidth == null)?"<null>":this.imgurlwidth));
        sb.append(',');
        sb.append("ipLocation");
        sb.append('=');
        sb.append(((this.ipLocation == null)?"<null>":this.ipLocation));
        sb.append(',');
        sb.append("isDel");
        sb.append('=');
        sb.append(((this.isDel == null)?"<null>":this.isDel));
        sb.append(',');
        sb.append("isLabeled");
        sb.append('=');
        sb.append(((this.isLabeled == null)?"<null>":this.isLabeled));
        sb.append(',');
        sb.append("isOff");
        sb.append('=');
        sb.append(((this.isOff == null)?"<null>":this.isOff));
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
        sb.append("isRecommendUp");
        sb.append('=');
        sb.append(((this.isRecommendUp == null)?"<null>":this.isRecommendUp));
        sb.append(',');
        sb.append("koubeiBoughtDate");
        sb.append('=');
        sb.append(((this.koubeiBoughtDate == null)?"<null>":this.koubeiBoughtDate));
        sb.append(',');
        sb.append("koubeiDriveKilometer");
        sb.append('=');
        sb.append(((this.koubeiDriveKilometer == null)?"<null>":this.koubeiDriveKilometer));
        sb.append(',');
        sb.append("koubeiElectricity");
        sb.append('=');
        sb.append(((this.koubeiElectricity == null)?"<null>":this.koubeiElectricity));
        sb.append(',');
        sb.append("koubeiLabelNames");
        sb.append('=');
        sb.append(((this.koubeiLabelNames == null)?"<null>":this.koubeiLabelNames));
        sb.append(',');
        sb.append("koubeiOilFeeling");
        sb.append('=');
        sb.append(((this.koubeiOilFeeling == null)?"<null>":this.koubeiOilFeeling));
        sb.append(',');
        sb.append("koubeiPowerDrivenType");
        sb.append('=');
        sb.append(((this.koubeiPowerDrivenType == null)?"<null>":this.koubeiPowerDrivenType));
        sb.append(',');
        sb.append("koubeiRoutineCruisingRange");
        sb.append('=');
        sb.append(((this.koubeiRoutineCruisingRange == null)?"<null>":this.koubeiRoutineCruisingRange));
        sb.append(',');
        sb.append("koubeiWinterCruisingRange");
        sb.append('=');
        sb.append(((this.koubeiWinterCruisingRange == null)?"<null>":this.koubeiWinterCruisingRange));
        sb.append(',');
        sb.append("labelIds");
        sb.append('=');
        sb.append(((this.labelIds == null)?"<null>":this.labelIds));
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
        sb.append("makeScore");
        sb.append('=');
        sb.append(((this.makeScore == null)?"<null>":this.makeScore));
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
        sb.append("newContentType");
        sb.append('=');
        sb.append(((this.newContentType == null)?"<null>":this.newContentType));
        sb.append(',');
        sb.append("newSource");
        sb.append('=');
        sb.append(((this.newSource == null)?"<null>":this.newSource));
        sb.append(',');
        sb.append("offVideo");
        sb.append('=');
        sb.append(((this.offVideo == null)?"<null>":this.offVideo));
        sb.append(',');
        sb.append("pcUrl");
        sb.append('=');
        sb.append(((this.pcUrl == null)?"<null>":this.pcUrl));
        sb.append(',');
        sb.append("playUrl");
        sb.append('=');
        sb.append(((this.playUrl == null)?"<null>":this.playUrl));
        sb.append(',');
        sb.append("positiveNegative");
        sb.append('=');
        sb.append(((this.positiveNegative == null)?"<null>":this.positiveNegative));
        sb.append(',');
        sb.append("price");
        sb.append('=');
        sb.append(((this.price == null)?"<null>":this.price));
        sb.append(',');
        sb.append("priceType");
        sb.append('=');
        sb.append(((this.priceType == null)?"<null>":this.priceType));
        sb.append(',');
        sb.append("publishTime");
        sb.append('=');
        sb.append(((this.publishTime == null)?"<null>":this.publishTime));
        sb.append(',');
        sb.append("pv");
        sb.append('=');
        sb.append(((this.pv == null)?"<null>":this.pv));
        sb.append(',');
        sb.append("qaAuthorId");
        sb.append('=');
        sb.append(((this.qaAuthorId == null)?"<null>":this.qaAuthorId));
        sb.append(',');
        sb.append("qaAuthorImg");
        sb.append('=');
        sb.append(((this.qaAuthorImg == null)?"<null>":this.qaAuthorImg));
        sb.append(',');
        sb.append("qaAuthorName");
        sb.append('=');
        sb.append(((this.qaAuthorName == null)?"<null>":this.qaAuthorName));
        sb.append(',');
        sb.append("qaAuthorSex");
        sb.append('=');
        sb.append(((this.qaAuthorSex == null)?"<null>":this.qaAuthorSex));
        sb.append(',');
        sb.append("query");
        sb.append('=');
        sb.append(((this.query == null)?"<null>":this.query));
        sb.append(',');
        sb.append("randombackgroundurl");
        sb.append('=');
        sb.append(((this.randombackgroundurl == null)?"<null>":this.randombackgroundurl));
        sb.append(',');
        sb.append("randomcolor");
        sb.append('=');
        sb.append(((this.randomcolor == null)?"<null>":this.randomcolor));
        sb.append(',');
        sb.append("replyCount");
        sb.append('=');
        sb.append(((this.replyCount == null)?"<null>":this.replyCount));
        sb.append(',');
        sb.append("script");
        sb.append('=');
        sb.append(((this.script == null)?"<null>":this.script));
        sb.append(',');
        sb.append("seriesIds");
        sb.append('=');
        sb.append(((this.seriesIds == null)?"<null>":this.seriesIds));
        sb.append(',');
        sb.append("seriesNames");
        sb.append('=');
        sb.append(((this.seriesNames == null)?"<null>":this.seriesNames));
        sb.append(',');
        sb.append("shoppingTime");
        sb.append('=');
        sb.append(((this.shoppingTime == null)?"<null>":this.shoppingTime));
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
        sb.append("specNames");
        sb.append('=');
        sb.append(((this.specNames == null)?"<null>":this.specNames));
        sb.append(',');
        sb.append("styleLabelIds");
        sb.append('=');
        sb.append(((this.styleLabelIds == null)?"<null>":this.styleLabelIds));
        sb.append(',');
        sb.append("styleLabelNames");
        sb.append('=');
        sb.append(((this.styleLabelNames == null)?"<null>":this.styleLabelNames));
        sb.append(',');
        sb.append("styleScore");
        sb.append('=');
        sb.append(((this.styleScore == null)?"<null>":this.styleScore));
        sb.append(',');
        sb.append("subTitle");
        sb.append('=');
        sb.append(((this.subTitle == null)?"<null>":this.subTitle));
        sb.append(',');
        sb.append("title");
        sb.append('=');
        sb.append(((this.title == null)?"<null>":this.title));
        sb.append(',');
        sb.append("titleScore");
        sb.append('=');
        sb.append(((this.titleScore == null)?"<null>":this.titleScore));
        sb.append(',');
        sb.append("topicReplyCount");
        sb.append('=');
        sb.append(((this.topicReplyCount == null)?"<null>":this.topicReplyCount));
        sb.append(',');
        sb.append("totalScore");
        sb.append('=');
        sb.append(((this.totalScore == null)?"<null>":this.totalScore));
        sb.append(',');
        sb.append("verifyStatus");
        sb.append('=');
        sb.append(((this.verifyStatus == null)?"<null>":this.verifyStatus));
        sb.append(',');
        sb.append("videovertical");
        sb.append('=');
        sb.append(((this.videovertical == null)?"<null>":this.videovertical));
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
        result = ((result* 31)+((this.labelNames == null)? 0 :this.labelNames.hashCode()));
        result = ((result* 31)+((this.randomcolor == null)? 0 :this.randomcolor.hashCode()));
        result = ((result* 31)+((this.createdStime == null)? 0 :this.createdStime.hashCode()));
        result = ((result* 31)+((this.pv == null)? 0 :this.pv.hashCode()));
        result = ((result* 31)+((this.seriesNames == null)? 0 :this.seriesNames.hashCode()));
        result = ((result* 31)+((this.videovertical == null)? 0 :this.videovertical.hashCode()));
        result = ((result* 31)+((this.contentLevel == null)? 0 :this.contentLevel.hashCode()));
        result = ((result* 31)+((this.likeCount == null)? 0 :this.likeCount.hashCode()));
        result = ((result* 31)+((this.mediaId == null)? 0 :this.mediaId.hashCode()));
        result = ((result* 31)+((this.koubeiPowerDrivenType == null)? 0 :this.koubeiPowerDrivenType.hashCode()));
        result = ((result* 31)+((this.playUrl == null)? 0 :this.playUrl.hashCode()));
        result = ((result* 31)+((this.highlightSection == null)? 0 :this.highlightSection.hashCode()));
        result = ((result* 31)+((this.attributeNames == null)? 0 :this.attributeNames.hashCode()));
        result = ((result* 31)+((this.labelIds == null)? 0 :this.labelIds.hashCode()));
        result = ((result* 31)+((this.attributeIds == null)? 0 :this.attributeIds.hashCode()));
        result = ((result* 31)+((this.multiImages == null)? 0 :this.multiImages.hashCode()));
        result = ((result* 31)+((this.topicReplyCount == null)? 0 :this.topicReplyCount.hashCode()));
        result = ((result* 31)+((this.offVideo == null)? 0 :this.offVideo.hashCode()));
        result = ((result* 31)+((this.price == null)? 0 :this.price.hashCode()));
        result = ((result* 31)+((this.bizId == null)? 0 :this.bizId.hashCode()));
        result = ((result* 31)+((this.isOff == null)? 0 :this.isOff.hashCode()));
        result = ((result* 31)+((this.height == null)? 0 :this.height.hashCode()));
        result = ((result* 31)+((this.publishTime == null)? 0 :this.publishTime.hashCode()));
        result = ((result* 31)+((this.bbsImage == null)? 0 :this.bbsImage.hashCode()));
        result = ((result* 31)+((this.pcUrl == null)? 0 :this.pcUrl.hashCode()));
        result = ((result* 31)+((this.qaAuthorImg == null)? 0 :this.qaAuthorImg.hashCode()));
        result = ((result* 31)+((this.authorSex == null)? 0 :this.authorSex.hashCode()));
        result = ((result* 31)+((this.script == null)? 0 :this.script.hashCode()));
        result = ((result* 31)+((this.koubeiOilFeeling == null)? 0 :this.koubeiOilFeeling.hashCode()));
        result = ((result* 31)+((this.specNames == null)? 0 :this.specNames.hashCode()));
        result = ((result* 31)+((this.size == null)? 0 :this.size.hashCode()));
        result = ((result* 31)+((this.authorName == null)? 0 :this.authorName.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.bbsId == null)? 0 :this.bbsId.hashCode()));
        result = ((result* 31)+((this.isRecommendUp == null)? 0 :this.isRecommendUp.hashCode()));
        result = ((result* 31)+((this.isPrivacy == null)? 0 :this.isPrivacy.hashCode()));
        result = ((result* 31)+((this.randombackgroundurl == null)? 0 :this.randombackgroundurl.hashCode()));
        result = ((result* 31)+((this.modifiedStime == null)? 0 :this.modifiedStime.hashCode()));
        result = ((result* 31)+((this.verifyStatus == null)? 0 :this.verifyStatus.hashCode()));
        result = ((result* 31)+((this.newContentType == null)? 0 :this.newContentType.hashCode()));
        result = ((result* 31)+((this.imgurlheight == null)? 0 :this.imgurlheight.hashCode()));
        result = ((result* 31)+((this.koubeiElectricity == null)? 0 :this.koubeiElectricity.hashCode()));
        result = ((result* 31)+((this.sourceBizId == null)? 0 :this.sourceBizId.hashCode()));
        result = ((result* 31)+((this.priceType == null)? 0 :this.priceType.hashCode()));
        result = ((result* 31)+((this.appUrl == null)? 0 :this.appUrl.hashCode()));
        result = ((result* 31)+((this.authorId == null)? 0 :this.authorId.hashCode()));
        result = ((result* 31)+((this.imgurlwidth == null)? 0 :this.imgurlwidth.hashCode()));
        result = ((result* 31)+((this.styleLabelIds == null)? 0 :this.styleLabelIds.hashCode()));
        result = ((result* 31)+((this.positiveNegative == null)? 0 :this.positiveNegative.hashCode()));
        result = ((result* 31)+((this.width == null)? 0 :this.width.hashCode()));
        result = ((result* 31)+((this.koubeiDriveKilometer == null)? 0 :this.koubeiDriveKilometer.hashCode()));
        result = ((result* 31)+((this.isDel == null)? 0 :this.isDel.hashCode()));
        result = ((result* 31)+((this.isRecommend == null)? 0 :this.isRecommend.hashCode()));
        result = ((result* 31)+((this.titleScore == null)? 0 :this.titleScore.hashCode()));
        result = ((result* 31)+((this.qaAuthorName == null)? 0 :this.qaAuthorName.hashCode()));
        result = ((result* 31)+((this.qaAuthorSex == null)? 0 :this.qaAuthorSex.hashCode()));
        result = ((result* 31)+((this.bbsName == null)? 0 :this.bbsName.hashCode()));
        result = ((result* 31)+((this.source == null)? 0 :this.source.hashCode()));
        result = ((result* 31)+((this.yearIds == null)? 0 :this.yearIds.hashCode()));
        result = ((result* 31)+((this.makeScore == null)? 0 :this.makeScore.hashCode()));
        result = ((result* 31)+((this.koubeiWinterCruisingRange == null)? 0 :this.koubeiWinterCruisingRange.hashCode()));
        result = ((result* 31)+((this.firstFrame == null)? 0 :this.firstFrame.hashCode()));
        result = ((result* 31)+((this.subTitle == null)? 0 :this.subTitle.hashCode()));
        result = ((result* 31)+((this.carseriesStatus == null)? 0 :this.carseriesStatus.hashCode()));
        result = ((result* 31)+((this.cityIds == null)? 0 :this.cityIds.hashCode()));
        result = ((result* 31)+((this.contentType == null)? 0 :this.contentType.hashCode()));
        result = ((result* 31)+((this.communityStatus == null)? 0 :this.communityStatus.hashCode()));
        result = ((result* 31)+((this.bbsScheme == null)? 0 :this.bbsScheme.hashCode()));
        result = ((result* 31)+((this.isLabeled == null)? 0 :this.isLabeled.hashCode()));
        result = ((result* 31)+((this.query == null)? 0 :this.query.hashCode()));
        result = ((result* 31)+((this.globalId == null)? 0 :this.globalId.hashCode()));
        result = ((result* 31)+((this.qaAuthorId == null)? 0 :this.qaAuthorId.hashCode()));
        result = ((result* 31)+((this.shoppingTime == null)? 0 :this.shoppingTime.hashCode()));
        result = ((result* 31)+((this.expireTime == null)? 0 :this.expireTime.hashCode()));
        result = ((result* 31)+((this.specIds == null)? 0 :this.specIds.hashCode()));
        result = ((result* 31)+((this.vv == null)? 0 :this.vv.hashCode()));
        result = ((result* 31)+((this.ipLocation == null)? 0 :this.ipLocation.hashCode()));
        result = ((result* 31)+((this.styleScore == null)? 0 :this.styleScore.hashCode()));
        result = ((result* 31)+((this.description == null)? 0 :this.description.hashCode()));
        result = ((result* 31)+((this.authorImg == null)? 0 :this.authorImg.hashCode()));
        result = ((result* 31)+((this.koubeiBoughtDate == null)? 0 :this.koubeiBoughtDate.hashCode()));
        result = ((result* 31)+((this.title == null)? 0 :this.title.hashCode()));
        result = ((result* 31)+((this.brandIds == null)? 0 :this.brandIds.hashCode()));
        result = ((result* 31)+((this.seriesIds == null)? 0 :this.seriesIds.hashCode()));
        result = ((result* 31)+((this.content == null)? 0 :this.content.hashCode()));
        result = ((result* 31)+((this.factoryIds == null)? 0 :this.factoryIds.hashCode()));
        result = ((result* 31)+((this.duration == null)? 0 :this.duration.hashCode()));
        result = ((result* 31)+((this.authorLevel == null)? 0 :this.authorLevel.hashCode()));
        result = ((result* 31)+((this.isPublic == null)? 0 :this.isPublic.hashCode()));
        result = ((result* 31)+((this.defaultImgUrl == null)? 0 :this.defaultImgUrl.hashCode()));
        result = ((result* 31)+((this.newSource == null)? 0 :this.newSource.hashCode()));
        result = ((result* 31)+((this.clipPlayUrl == null)? 0 :this.clipPlayUrl.hashCode()));
        result = ((result* 31)+((this.imgUrl23 == null)? 0 :this.imgUrl23 .hashCode()));
        result = ((result* 31)+((this.koubeiRoutineCruisingRange == null)? 0 :this.koubeiRoutineCruisingRange.hashCode()));
        result = ((result* 31)+((this.koubeiLabelNames == null)? 0 :this.koubeiLabelNames.hashCode()));
        result = ((result* 31)+((this.totalScore == null)? 0 :this.totalScore.hashCode()));
        result = ((result* 31)+((this.imgUrl == null)? 0 :this.imgUrl.hashCode()));
        result = ((result* 31)+((this.mUrl == null)? 0 :this.mUrl.hashCode()));
        result = ((result* 31)+((this.brandNames == null)? 0 :this.brandNames.hashCode()));
        result = ((result* 31)+((this.replyCount == null)? 0 :this.replyCount.hashCode()));
        result = ((result* 31)+((this.styleLabelNames == null)? 0 :this.styleLabelNames.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AllContentInfo) == false) {
            return false;
        }
        AllContentInfo rhs = ((AllContentInfo) other);
        return ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((this.labelNames == rhs.labelNames)||((this.labelNames!= null)&&this.labelNames.equals(rhs.labelNames)))&&((this.randomcolor == rhs.randomcolor)||((this.randomcolor!= null)&&this.randomcolor.equals(rhs.randomcolor))))&&((this.createdStime == rhs.createdStime)||((this.createdStime!= null)&&this.createdStime.equals(rhs.createdStime))))&&((this.pv == rhs.pv)||((this.pv!= null)&&this.pv.equals(rhs.pv))))&&((this.seriesNames == rhs.seriesNames)||((this.seriesNames!= null)&&this.seriesNames.equals(rhs.seriesNames))))&&((this.videovertical == rhs.videovertical)||((this.videovertical!= null)&&this.videovertical.equals(rhs.videovertical))))&&((this.contentLevel == rhs.contentLevel)||((this.contentLevel!= null)&&this.contentLevel.equals(rhs.contentLevel))))&&((this.likeCount == rhs.likeCount)||((this.likeCount!= null)&&this.likeCount.equals(rhs.likeCount))))&&((this.mediaId == rhs.mediaId)||((this.mediaId!= null)&&this.mediaId.equals(rhs.mediaId))))&&((this.koubeiPowerDrivenType == rhs.koubeiPowerDrivenType)||((this.koubeiPowerDrivenType!= null)&&this.koubeiPowerDrivenType.equals(rhs.koubeiPowerDrivenType))))&&((this.playUrl == rhs.playUrl)||((this.playUrl!= null)&&this.playUrl.equals(rhs.playUrl))))&&((this.highlightSection == rhs.highlightSection)||((this.highlightSection!= null)&&this.highlightSection.equals(rhs.highlightSection))))&&((this.attributeNames == rhs.attributeNames)||((this.attributeNames!= null)&&this.attributeNames.equals(rhs.attributeNames))))&&((this.labelIds == rhs.labelIds)||((this.labelIds!= null)&&this.labelIds.equals(rhs.labelIds))))&&((this.attributeIds == rhs.attributeIds)||((this.attributeIds!= null)&&this.attributeIds.equals(rhs.attributeIds))))&&((this.multiImages == rhs.multiImages)||((this.multiImages!= null)&&this.multiImages.equals(rhs.multiImages))))&&((this.topicReplyCount == rhs.topicReplyCount)||((this.topicReplyCount!= null)&&this.topicReplyCount.equals(rhs.topicReplyCount))))&&((this.offVideo == rhs.offVideo)||((this.offVideo!= null)&&this.offVideo.equals(rhs.offVideo))))&&((this.price == rhs.price)||((this.price!= null)&&this.price.equals(rhs.price))))&&((this.bizId == rhs.bizId)||((this.bizId!= null)&&this.bizId.equals(rhs.bizId))))&&((this.isOff == rhs.isOff)||((this.isOff!= null)&&this.isOff.equals(rhs.isOff))))&&((this.height == rhs.height)||((this.height!= null)&&this.height.equals(rhs.height))))&&((this.publishTime == rhs.publishTime)||((this.publishTime!= null)&&this.publishTime.equals(rhs.publishTime))))&&((this.bbsImage == rhs.bbsImage)||((this.bbsImage!= null)&&this.bbsImage.equals(rhs.bbsImage))))&&((this.pcUrl == rhs.pcUrl)||((this.pcUrl!= null)&&this.pcUrl.equals(rhs.pcUrl))))&&((this.qaAuthorImg == rhs.qaAuthorImg)||((this.qaAuthorImg!= null)&&this.qaAuthorImg.equals(rhs.qaAuthorImg))))&&((this.authorSex == rhs.authorSex)||((this.authorSex!= null)&&this.authorSex.equals(rhs.authorSex))))&&((this.script == rhs.script)||((this.script!= null)&&this.script.equals(rhs.script))))&&((this.koubeiOilFeeling == rhs.koubeiOilFeeling)||((this.koubeiOilFeeling!= null)&&this.koubeiOilFeeling.equals(rhs.koubeiOilFeeling))))&&((this.specNames == rhs.specNames)||((this.specNames!= null)&&this.specNames.equals(rhs.specNames))))&&((this.size == rhs.size)||((this.size!= null)&&this.size.equals(rhs.size))))&&((this.authorName == rhs.authorName)||((this.authorName!= null)&&this.authorName.equals(rhs.authorName))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.bbsId == rhs.bbsId)||((this.bbsId!= null)&&this.bbsId.equals(rhs.bbsId))))&&((this.isRecommendUp == rhs.isRecommendUp)||((this.isRecommendUp!= null)&&this.isRecommendUp.equals(rhs.isRecommendUp))))&&((this.isPrivacy == rhs.isPrivacy)||((this.isPrivacy!= null)&&this.isPrivacy.equals(rhs.isPrivacy))))&&((this.randombackgroundurl == rhs.randombackgroundurl)||((this.randombackgroundurl!= null)&&this.randombackgroundurl.equals(rhs.randombackgroundurl))))&&((this.modifiedStime == rhs.modifiedStime)||((this.modifiedStime!= null)&&this.modifiedStime.equals(rhs.modifiedStime))))&&((this.verifyStatus == rhs.verifyStatus)||((this.verifyStatus!= null)&&this.verifyStatus.equals(rhs.verifyStatus))))&&((this.newContentType == rhs.newContentType)||((this.newContentType!= null)&&this.newContentType.equals(rhs.newContentType))))&&((this.imgurlheight == rhs.imgurlheight)||((this.imgurlheight!= null)&&this.imgurlheight.equals(rhs.imgurlheight))))&&((this.koubeiElectricity == rhs.koubeiElectricity)||((this.koubeiElectricity!= null)&&this.koubeiElectricity.equals(rhs.koubeiElectricity))))&&((this.sourceBizId == rhs.sourceBizId)||((this.sourceBizId!= null)&&this.sourceBizId.equals(rhs.sourceBizId))))&&((this.priceType == rhs.priceType)||((this.priceType!= null)&&this.priceType.equals(rhs.priceType))))&&((this.appUrl == rhs.appUrl)||((this.appUrl!= null)&&this.appUrl.equals(rhs.appUrl))))&&((this.authorId == rhs.authorId)||((this.authorId!= null)&&this.authorId.equals(rhs.authorId))))&&((this.imgurlwidth == rhs.imgurlwidth)||((this.imgurlwidth!= null)&&this.imgurlwidth.equals(rhs.imgurlwidth))))&&((this.styleLabelIds == rhs.styleLabelIds)||((this.styleLabelIds!= null)&&this.styleLabelIds.equals(rhs.styleLabelIds))))&&((this.positiveNegative == rhs.positiveNegative)||((this.positiveNegative!= null)&&this.positiveNegative.equals(rhs.positiveNegative))))&&((this.width == rhs.width)||((this.width!= null)&&this.width.equals(rhs.width))))&&((this.koubeiDriveKilometer == rhs.koubeiDriveKilometer)||((this.koubeiDriveKilometer!= null)&&this.koubeiDriveKilometer.equals(rhs.koubeiDriveKilometer))))&&((this.isDel == rhs.isDel)||((this.isDel!= null)&&this.isDel.equals(rhs.isDel))))&&((this.isRecommend == rhs.isRecommend)||((this.isRecommend!= null)&&this.isRecommend.equals(rhs.isRecommend))))&&((this.titleScore == rhs.titleScore)||((this.titleScore!= null)&&this.titleScore.equals(rhs.titleScore))))&&((this.qaAuthorName == rhs.qaAuthorName)||((this.qaAuthorName!= null)&&this.qaAuthorName.equals(rhs.qaAuthorName))))&&((this.qaAuthorSex == rhs.qaAuthorSex)||((this.qaAuthorSex!= null)&&this.qaAuthorSex.equals(rhs.qaAuthorSex))))&&((this.bbsName == rhs.bbsName)||((this.bbsName!= null)&&this.bbsName.equals(rhs.bbsName))))&&((this.source == rhs.source)||((this.source!= null)&&this.source.equals(rhs.source))))&&((this.yearIds == rhs.yearIds)||((this.yearIds!= null)&&this.yearIds.equals(rhs.yearIds))))&&((this.makeScore == rhs.makeScore)||((this.makeScore!= null)&&this.makeScore.equals(rhs.makeScore))))&&((this.koubeiWinterCruisingRange == rhs.koubeiWinterCruisingRange)||((this.koubeiWinterCruisingRange!= null)&&this.koubeiWinterCruisingRange.equals(rhs.koubeiWinterCruisingRange))))&&((this.firstFrame == rhs.firstFrame)||((this.firstFrame!= null)&&this.firstFrame.equals(rhs.firstFrame))))&&((this.subTitle == rhs.subTitle)||((this.subTitle!= null)&&this.subTitle.equals(rhs.subTitle))))&&((this.carseriesStatus == rhs.carseriesStatus)||((this.carseriesStatus!= null)&&this.carseriesStatus.equals(rhs.carseriesStatus))))&&((this.cityIds == rhs.cityIds)||((this.cityIds!= null)&&this.cityIds.equals(rhs.cityIds))))&&((this.contentType == rhs.contentType)||((this.contentType!= null)&&this.contentType.equals(rhs.contentType))))&&((this.communityStatus == rhs.communityStatus)||((this.communityStatus!= null)&&this.communityStatus.equals(rhs.communityStatus))))&&((this.bbsScheme == rhs.bbsScheme)||((this.bbsScheme!= null)&&this.bbsScheme.equals(rhs.bbsScheme))))&&((this.isLabeled == rhs.isLabeled)||((this.isLabeled!= null)&&this.isLabeled.equals(rhs.isLabeled))))&&((this.query == rhs.query)||((this.query!= null)&&this.query.equals(rhs.query))))&&((this.globalId == rhs.globalId)||((this.globalId!= null)&&this.globalId.equals(rhs.globalId))))&&((this.qaAuthorId == rhs.qaAuthorId)||((this.qaAuthorId!= null)&&this.qaAuthorId.equals(rhs.qaAuthorId))))&&((this.shoppingTime == rhs.shoppingTime)||((this.shoppingTime!= null)&&this.shoppingTime.equals(rhs.shoppingTime))))&&((this.expireTime == rhs.expireTime)||((this.expireTime!= null)&&this.expireTime.equals(rhs.expireTime))))&&((this.specIds == rhs.specIds)||((this.specIds!= null)&&this.specIds.equals(rhs.specIds))))&&((this.vv == rhs.vv)||((this.vv!= null)&&this.vv.equals(rhs.vv))))&&((this.ipLocation == rhs.ipLocation)||((this.ipLocation!= null)&&this.ipLocation.equals(rhs.ipLocation))))&&((this.styleScore == rhs.styleScore)||((this.styleScore!= null)&&this.styleScore.equals(rhs.styleScore))))&&((this.description == rhs.description)||((this.description!= null)&&this.description.equals(rhs.description))))&&((this.authorImg == rhs.authorImg)||((this.authorImg!= null)&&this.authorImg.equals(rhs.authorImg))))&&((this.koubeiBoughtDate == rhs.koubeiBoughtDate)||((this.koubeiBoughtDate!= null)&&this.koubeiBoughtDate.equals(rhs.koubeiBoughtDate))))&&((this.title == rhs.title)||((this.title!= null)&&this.title.equals(rhs.title))))&&((this.brandIds == rhs.brandIds)||((this.brandIds!= null)&&this.brandIds.equals(rhs.brandIds))))&&((this.seriesIds == rhs.seriesIds)||((this.seriesIds!= null)&&this.seriesIds.equals(rhs.seriesIds))))&&((this.content == rhs.content)||((this.content!= null)&&this.content.equals(rhs.content))))&&((this.factoryIds == rhs.factoryIds)||((this.factoryIds!= null)&&this.factoryIds.equals(rhs.factoryIds))))&&((this.duration == rhs.duration)||((this.duration!= null)&&this.duration.equals(rhs.duration))))&&((this.authorLevel == rhs.authorLevel)||((this.authorLevel!= null)&&this.authorLevel.equals(rhs.authorLevel))))&&((this.isPublic == rhs.isPublic)||((this.isPublic!= null)&&this.isPublic.equals(rhs.isPublic))))&&((this.defaultImgUrl == rhs.defaultImgUrl)||((this.defaultImgUrl!= null)&&this.defaultImgUrl.equals(rhs.defaultImgUrl))))&&((this.newSource == rhs.newSource)||((this.newSource!= null)&&this.newSource.equals(rhs.newSource))))&&((this.clipPlayUrl == rhs.clipPlayUrl)||((this.clipPlayUrl!= null)&&this.clipPlayUrl.equals(rhs.clipPlayUrl))))&&((this.imgUrl23 == rhs.imgUrl23)||((this.imgUrl23 != null)&&this.imgUrl23 .equals(rhs.imgUrl23))))&&((this.koubeiRoutineCruisingRange == rhs.koubeiRoutineCruisingRange)||((this.koubeiRoutineCruisingRange!= null)&&this.koubeiRoutineCruisingRange.equals(rhs.koubeiRoutineCruisingRange))))&&((this.koubeiLabelNames == rhs.koubeiLabelNames)||((this.koubeiLabelNames!= null)&&this.koubeiLabelNames.equals(rhs.koubeiLabelNames))))&&((this.totalScore == rhs.totalScore)||((this.totalScore!= null)&&this.totalScore.equals(rhs.totalScore))))&&((this.imgUrl == rhs.imgUrl)||((this.imgUrl!= null)&&this.imgUrl.equals(rhs.imgUrl))))&&((this.mUrl == rhs.mUrl)||((this.mUrl!= null)&&this.mUrl.equals(rhs.mUrl))))&&((this.brandNames == rhs.brandNames)||((this.brandNames!= null)&&this.brandNames.equals(rhs.brandNames))))&&((this.replyCount == rhs.replyCount)||((this.replyCount!= null)&&this.replyCount.equals(rhs.replyCount))))&&((this.styleLabelNames == rhs.styleLabelNames)||((this.styleLabelNames!= null)&&this.styleLabelNames.equals(rhs.styleLabelNames))));
    }

}
