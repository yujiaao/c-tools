package com.bixuebihui.spring;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 多种日期格式反序列化模块支持，
 * 需要在spring 的配置文件中注册此模块，
 * 例如在 application.yml 中添加：
 * ```yaml
 * spring:
 *   jackson:
 *      modules:
 *      - com.bixuebihui.spring.MultiDateModule
 * ```
 * 这样就可以在反序列化时支持多种日期格式。
 */
public class MultiDateModule extends SimpleModule {
    public MultiDateModule() {
        // 为 Date 类型注册多格式反序列化器
        addDeserializer(Date.class, new DateDeserializer());
    }

    private static class DateDeserializer extends StdScalarDeserializer<Date> {
        // 支持的日期格式列表，按优先级排序
        private static final List<SimpleDateFormat> FORMATS = new ArrayList<>();

        static {
            FORMATS.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            FORMATS.add(new SimpleDateFormat("yyyy-MM-dd"));
            FORMATS.add(new SimpleDateFormat("yyyy-M-d")); // 新增，兼容 2025-7-1
            // 可以根据需要添加更多格式
        }

        public DateDeserializer() {
            super(Date.class);
        }

        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateStr = p.getValueAsString();
            for (SimpleDateFormat format : FORMATS) {
                try {
                    return format.parse(dateStr);
                } catch (ParseException e) {
                    // 尝试下一种格式
                }
            }
            throw new IOException("无法解析日期格式: " + dateStr);
        }
    }
}