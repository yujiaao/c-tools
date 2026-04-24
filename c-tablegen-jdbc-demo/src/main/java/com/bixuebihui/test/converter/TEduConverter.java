package com.bixuebihui.test.converter;

import com.bixuebihui.test.pojo.*;
import com.bixuebihui.test.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.bixuebihui.codec.EncryptDecryptService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xwx
 */
@Mapper
public interface TEduConverter {

    TEduConverter MAPPER = Mappers.getMapper(TEduConverter.class);

    TEduDto toDto(TEdu info);

    TEdu toDao(TEduDto info);

    List<TEduDto> toDtoList(List<TEdu> info);

    List<TEdu> toDaoList(List<TEduDto> info);

    default TEduDto toDtoDec(TEdu info, EncryptDecryptService encryptDecryptService) {
        TEduDto res = toDto(info);
        List<String> listStr = strCollector(info);
        List<String> encStr = encryptDecryptService.decrypt(listStr);
        if (!encStr.isEmpty() && encStr.size() == listStr.size()) {
            strSettersDto(res, encStr, 0);
        }
        return res;
    }

    default TEdu toDaoEnc(TEduDto info, EncryptDecryptService encryptDecryptService) {
        TEdu res = toDao(info);
        List<String> listStr = strCollectorDto(info);
        List<String> encStr = encryptDecryptService.encrypt(listStr);
        if (!encStr.isEmpty() && encStr.size() == listStr.size()) {
            strSetters(res, encStr, 0);
        }
        return res;
    }

    default List<TEdu> toDaoListEnc(List<TEduDto> info, EncryptDecryptService encryptDecryptService) {
        List<TEdu> res = toDaoList(info);
        List<String> listStr = res.stream().map(TEduConverter::strCollector).flatMap(List::stream).collect(Collectors.toList());
        List<String> encStr = encryptDecryptService.encrypt(listStr);
        int index = 0;
        if (!encStr.isEmpty() && encStr.size() == listStr.size()) {
            for (TEdu it : res) {
                index = strSetters(it, encStr, index);
            }
        }
        return res;
    }

    default List<TEduDto> toDtoListDec(List<TEdu> info, EncryptDecryptService encryptDecryptService) {
        List<TEduDto> res = toDtoList(info);
        List<String> listStr = res.stream().map(TEduConverter::strCollectorDto).flatMap(List::stream).collect(Collectors.toList());
        List<String> encStr = encryptDecryptService.decrypt(listStr);
        int index = 0;
        if (!encStr.isEmpty() && encStr.size() == listStr.size()) {
            for (TEduDto it : res) {
                index = strSettersDto(it, encStr, index);
            }
        }
        return res;
    }

    private static List<String> strCollector(TEdu u) {
        return Arrays.asList(u.getDegree());
    }

    private static List<String> strCollectorDto(TEduDto u) {
        return Arrays.asList(u.getDegree());
    }

    private static int strSettersDto(TEduDto u, List<String> decStr, int index) {
        u.setDegree(decStr.get(index++));
        return index;
    }

    private static int strSetters(TEdu u, List<String> encStr, int index) {
        u.setDegree(encStr.get(index++));
        return index;
    }
}
