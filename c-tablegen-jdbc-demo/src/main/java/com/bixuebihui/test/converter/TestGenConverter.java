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
public interface TestGenConverter {

    TestGenConverter MAPPER = Mappers.getMapper(TestGenConverter.class);

    TestGenDto toDto(TestGen info);

    TestGen toDao(TestGenDto info);

    List<TestGenDto> toDtoList(List<TestGen> info);

    List<TestGen> toDaoList(List<TestGenDto> info);

    default TestGenDto toDtoDec(TestGen info, EncryptDecryptService encryptDecryptService) {
        TestGenDto res = toDto(info);
        List<String> listStr = strCollector(info);
        List<String> encStr = encryptDecryptService.decrypt(listStr);
        if (!encStr.isEmpty() && encStr.size() == listStr.size()) {
            strSettersDto(res, encStr, 0);
        }
        return res;
    }

    default TestGen toDaoEnc(TestGenDto info, EncryptDecryptService encryptDecryptService) {
        TestGen res = toDao(info);
        List<String> listStr = strCollectorDto(info);
        List<String> encStr = encryptDecryptService.encrypt(listStr);
        if (!encStr.isEmpty() && encStr.size() == listStr.size()) {
            strSetters(res, encStr, 0);
        }
        return res;
    }

    default List<TestGen> toDaoListEnc(List<TestGenDto> info, EncryptDecryptService encryptDecryptService) {
        List<TestGen> res = toDaoList(info);
        List<String> listStr = res.stream().map(TestGenConverter::strCollector).flatMap(List::stream).collect(Collectors.toList());
        List<String> encStr = encryptDecryptService.encrypt(listStr);
        int index = 0;
        if (!encStr.isEmpty() && encStr.size() == listStr.size()) {
            for (TestGen it : res) {
                index = strSetters(it, encStr, index);
            }
        }
        return res;
    }

    default List<TestGenDto> toDtoListDec(List<TestGen> info, EncryptDecryptService encryptDecryptService) {
        List<TestGenDto> res = toDtoList(info);
        List<String> listStr = res.stream().map(TestGenConverter::strCollectorDto).flatMap(List::stream).collect(Collectors.toList());
        List<String> encStr = encryptDecryptService.decrypt(listStr);
        int index = 0;
        if (!encStr.isEmpty() && encStr.size() == listStr.size()) {
            for (TestGenDto it : res) {
                index = strSettersDto(it, encStr, index);
            }
        }
        return res;
    }

    private static List<String> strCollector(TestGen u) {
        return Arrays.asList(u.getName());
    }

    private static List<String> strCollectorDto(TestGenDto u) {
        return Arrays.asList(u.getName());
    }

    private static int strSettersDto(TestGenDto u, List<String> decStr, int index) {
        u.setName(decStr.get(index++));
        return index;
    }

    private static int strSetters(TestGen u, List<String> encStr, int index) {
        u.setName(encStr.get(index++));
        return index;
    }
}
