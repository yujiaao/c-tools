package com.bixuebihui;

import org.mapstruct.Named;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public interface Base64Converter<DAO, DTO> {


    List<DTO> toDtoList(List<DAO> info);

    List<DAO> toDaoList(List<DTO> info);

    @Named("encodeBase64")
    default String encodeBase64(String value) {
        if (value == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    @Named("decodeBase64")
    default String decodeBase64(String value) {
        if (value == null) {
            return null;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(value);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // 如果不是有效的 Base64 字符串，则返回原值作为兜底
            return value;
        }
    }
}
