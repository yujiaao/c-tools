package com.bixuebihui.jsongen;

import com.fasterxml.jackson.databind.JsonNode;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Collections;

public class EsMapping2JsonschemaUtilsTest extends TestCase {

    public static final String s= """
            {
            "my-index-name": {
            "mappings": {
            "my-index-type": {
            "properties": {
            "attribute_ids": {
            "type": "long"
            },
            "attribute_names": {
            "type": "keyword"
            },
            "author_id": {
            "type": "long"
            },
            "author_name": {
            "type": "keyword"
            },
            "brand_ids": {
            "type": "long"
            },
            "content_label_ids": {
            "type": "long"
            },
            "content_label_names": {
            "type": "text",
            "fields": {
            "keyword": {
            "type": "keyword",
            "ignore_above": 256
            }
            }
            },
            "cover_image": {
            "type": "text"
            },
            "cover_image_2_3": {
            "type": "text"
            },
            "created_stime": {
            "type": "keyword"
            },
            "duaration": {
            "type": "long"
            },
            "expire_date": {
            "type": "keyword"
            },
            "factory_ids": {
            "type": "long"
            },
            "first_frame": {
            "type": "text"
            },
            "is_del": {
            "type": "long"
            },
            "is_privacy": {
            "type": "integer"
            },
            "is_public": {
            "type": "long"
            },
            "is_recommend": {
            "type": "integer"
            },
            "label_ids": {
            "type": "long"
            },
            "label_names": {
            "type": "keyword"
            },
            "labeled": {
            "type": "integer"
            },
            "make_score": {
            "type": "double"
            },
            "media_id": {
            "type": "text"
            },
            "off_video": {
            "type": "integer"
            },
            "play_url": {
            "type": "text"
            },
            "series_ids": {
            "type": "long"
            },
            "style_label_ids": {
            "type": "long"
            },
            "style_label_names": {
            "type": "text",
            "fields": {
            "keyword": {
            "type": "keyword",
            "ignore_above": 256
            }
            }
            },
            "stytle_score": {
            "type": "double"
            },
            "title": {
            "type": "keyword"
            },
            "title_score": {
            "type": "double"
            },
            "total_score": {
            "type": "double"
            },
            "verify_status": {
            "type": "integer"
            },
            "video_id": {
            "type": "long"
            },
            "video_source": {
            "type": "long"
            }
            }
            }
            }
            }
            }
            """;
    public void testTrans() throws IOException {

        JsonNode trans = EsMapping2JsonschemaUtils.trans(s, Collections.emptySet(), Collections.emptySet());
        System.out.println(trans.toString());
        //  https://www.jsonschema2pojo.org/
    }
}
