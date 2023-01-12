package com.example.es.dal;

/*
 *  ChejiahaoContentsList
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import com.example.es.pojo.*;

import com.bixuebihui.elasticsearch.BaseEsOperator;
import com.bixuebihui.elasticsearch.EsRequest;
import com.bixuebihui.elasticsearch.BaseDao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Generated;


@Generated("com.github.yujiaao:tablegen")
public class ChejiahaoContentsList extends BaseDao<ChejiahaoContents, Long> {
    

    @Override
    public Long getId(ChejiahaoContents info) {
        return info.getInfoId();
    }

    /**
    * Get key name.
    */
    @Override
    public String getKeyName()
    {
        return  F.INFO_ID;
    }


    protected ChejiahaoContentsList(Map<String, String> ds)
    {
        super(new BaseEsOperator<>( new EsRequest(ds.get("host"),
            ds.get("username"),
            ds.get("password"),
            "chejiahao-contents",
            ds.get("version")
            )) {
                @Override
                protected Class<ChejiahaoContents> getType() {
                    return ChejiahaoContents.class;
                }
        });
        this.indexName =  "chejiahao-contents";
    }
    
    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    public static final class F{
        public static final String AD_CITY = "ad_city";
        public static final String ALLOW_DELETE = "allow_delete";
        public static final String AUDIT_NOTES = "audit_notes";
        public static final String AUDIT_RECOMMEND = "audit_recommend";
        public static final String AUDIT_STAGE = "audit_stage";
        public static final String AUDITOR = "auditor";
        public static final String AUTHOR_ID = "author_id";
        public static final String AUTO_WRITE_ARICLE_ID = "auto_write_aricle_id";
        public static final String AUTOHOME_UA = "autohome_ua";
        public static final String CANCEL_RECOMMEND_TIME = "cancel_recommend_time";
        public static final String CLIENT_TYPE = "client_type";
        public static final String CONCEPT_KEY_WORDS = "concept_key_words";
        public static final String CONFIGURE = "configure";
        public static final String CONTENT_TYPE = "content_type";
        public static final String CREATE_TIME = "create_time";
        public static final String DESCRIPTION = "description";
        public static final String DESCRIPTION2 = "description2";
        public static final String EXPIRE_DATE = "expire_date";
        public static final String HAILIANG_REASON = "hailiang_reason";
        public static final String HAILIANG_STATUS = "hailiang_status";
        public static final String HAS_REVIEW = "has_review";
        public static final String HOT_SPOT_TAG = "hot_spot_tag";
        public static final String IDENT = "ident";
        public static final String IMAGE = "image";
        public static final String IMAGE1 = "image1";
        public static final String IMAGE2 = "image2";
        public static final String IMAGE3 = "image3";
        public static final String IMG_CROSS = "img_cross";
        public static final String IMG_VERTICAL = "img_vertical";
        public static final String INFO_ID = "info_id";
        public static final String INFO_LEVEL = "info_level";
        public static final String INFO_TYPE = "info_type";
        public static final String INNOVATE = "innovate";
        public static final String IP = "ip";
        public static final String IP_PROVINCE = "ip_province";
        public static final String IS_ARTIFICIAL = "is_artificial";
        public static final String IS_AUTO_WRITE = "is_auto_write";
        public static final String IS_BUSINESS = "is_business";
        public static final String IS_CONVERT_TEXT = "is_convert_text";
        public static final String IS_DEDUCTION = "is_deduction";
        public static final String IS_DEL = "is_del";
        public static final String IS_EXPOSE = "is_expose";
        public static final String IS_FASTEST_FIRST = "is_fastest_first";
        public static final String IS_FIRST = "is_first";
        public static final String IS_IMPORTANT = "is_important";
        public static final String IS_LOCAL_AUDIT = "is_local_audit";
        public static final String IS_LOW_CAR = "is_low_car";
        public static final String IS_MANUAL_CONTENT_LEVEL = "is_manual_content_level";
        public static final String IS_MANUAL_OR_AI = "is_manual_or_ai";
        public static final String IS_ORIGINAL = "is_original";
        public static final String IS_PRODUCT_IMAGE = "is_product_image";
        public static final String IS_PUBLISH = "is_publish";
        public static final String IS_PUSH = "is_push";
        public static final String IS_TIMING = "is_timing";
        public static final String IS_TOP = "is_top";
        public static final String IS_WAI_TUI = "is_wai_tui";
        public static final String IS_YOU_XUAN = "is_you_xuan";
        public static final String KEY_WORDS = "key_words";
        public static final String LAST_UPDATE_TIME = "last_update_time";
        public static final String LOCAL_AUDIT_TIME = "local_audit_time";
        public static final String LOCAL_RECOMMEND_TIME = "local_recommend_time";
        public static final String LOCAL_RECOMMEND_TYPE = "local_recommend_type";
        public static final String NEW_KEY_WORD_IDS = "new_key_word_ids";
        public static final String NEW_KEY_WORDS = "new_key_words";
        public static final String OPERATE_REMARKS = "operate_remarks";
        public static final String PLUS_OR_MINUS_ONE = "plus_or_minus_one";
        public static final String PLUS_OR_MINUS_TWO = "plus_or_minus_two";
        public static final String PUBLISH_TIME = "publish_time";
        public static final String PUBLISH_TIMING = "publish_timing";
        public static final String PUSH_DESCRIPTION = "push_description";
        public static final String PUSH_TITLE = "push_title";
        public static final String RECOMMEND_TO_MOJI = "recommend_to_moji";
        public static final String RECOMMEND_TO_SERIES = "recommend_to_series";
        public static final String RECOMMEND_TO_XNY = "recommend_to_xny";
        public static final String RECYCLED = "recycled";
        public static final String RECYCLED_ALONG_WITH_AUTHOR = "recycled_along_with_author";
        public static final String SHOW_BIG_IMG = "show_big_img";
        public static final String SHOW_IMAGE_LIST = "show_image_list";
        public static final String SHOW_LEVEL = "show_level";
        public static final String SORT_TYPE = "sort_type";
        public static final String STATUS = "status";
        public static final String STATUS_NOTE = "status_note";
        public static final String STATUS_NOTE_ID = "status_note_id";
        public static final String TITLE = "title";
        public static final String TITLE2 = "title2";
        public static final String TITLE3 = "title3";
        public static final String VERSION_SOURCE = "version_source";
        public static final String WAI_TUI_TIME = "wai_tui_time";
        public static final String WAIT_AUDIT_TIME = "wait_audit_time";
        public static final String WAIT_AUDIT_TIME2 = "wait_audit_time2";
        public static final String WAIT_AUDIT_TIME3 = "wait_audit_time3";
        public static final String YOU_XUAN_TIME = "you_xuan_time";
        
        public static String[] getAllFields() {
            return new String[] { AD_CITY, ALLOW_DELETE, AUDIT_NOTES, AUDIT_RECOMMEND, AUDIT_STAGE, AUDITOR, AUTHOR_ID, AUTO_WRITE_ARICLE_ID, AUTOHOME_UA, CANCEL_RECOMMEND_TIME, CLIENT_TYPE, CONCEPT_KEY_WORDS, CONFIGURE, CONTENT_TYPE, CREATE_TIME, DESCRIPTION, DESCRIPTION2, EXPIRE_DATE, HAILIANG_REASON, HAILIANG_STATUS, HAS_REVIEW, HOT_SPOT_TAG, IDENT, IMAGE, IMAGE1, IMAGE2, IMAGE3, IMG_CROSS, IMG_VERTICAL, INFO_ID, INFO_LEVEL, INFO_TYPE, INNOVATE, IP, IP_PROVINCE, IS_ARTIFICIAL, IS_AUTO_WRITE, IS_BUSINESS, IS_CONVERT_TEXT, IS_DEDUCTION, IS_DEL, IS_EXPOSE, IS_FASTEST_FIRST, IS_FIRST, IS_IMPORTANT, IS_LOCAL_AUDIT, IS_LOW_CAR, IS_MANUAL_CONTENT_LEVEL, IS_MANUAL_OR_AI, IS_ORIGINAL, IS_PRODUCT_IMAGE, IS_PUBLISH, IS_PUSH, IS_TIMING, IS_TOP, IS_WAI_TUI, IS_YOU_XUAN, KEY_WORDS, LAST_UPDATE_TIME, LOCAL_AUDIT_TIME, LOCAL_RECOMMEND_TIME, LOCAL_RECOMMEND_TYPE, NEW_KEY_WORD_IDS, NEW_KEY_WORDS, OPERATE_REMARKS, PLUS_OR_MINUS_ONE, PLUS_OR_MINUS_TWO, PUBLISH_TIME, PUBLISH_TIMING, PUSH_DESCRIPTION, PUSH_TITLE, RECOMMEND_TO_MOJI, RECOMMEND_TO_SERIES, RECOMMEND_TO_XNY, RECYCLED, RECYCLED_ALONG_WITH_AUTHOR, SHOW_BIG_IMG, SHOW_IMAGE_LIST, SHOW_LEVEL, SORT_TYPE, STATUS, STATUS_NOTE, STATUS_NOTE_ID, TITLE, TITLE2, TITLE3, VERSION_SOURCE, WAI_TUI_TIME, WAIT_AUDIT_TIME, WAIT_AUDIT_TIME2, WAIT_AUDIT_TIME3, YOU_XUAN_TIME };
        }
    }

    @Override
    public List<String> getAllFields() {
        return Arrays.asList(F.getAllFields());
    }

}
