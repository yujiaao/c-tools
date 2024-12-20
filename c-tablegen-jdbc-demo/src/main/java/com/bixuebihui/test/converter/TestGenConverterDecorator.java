package com.bixuebihui.test.converter;

import com.bixuebihui.codec.EncryptDecryptService;
import com.bixuebihui.test.dto.TestGenDto;
import com.bixuebihui.test.pojo.TestGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class TestGenConverterDecorator implements TestGenConverter {
    @Autowired
     protected TestGenConverter delegate;


    @Autowired
    EncryptDecryptService enc;


    @Override
    public TestGenDto toDto(TestGen info) {
        return doAfterSingle(delegate.toDto(info));
    }

    @Override
    public TestGen toDao(TestGenDto info) {
        return doAfterSingle(delegate.toDao(info));
    }

    @Override
    public List<TestGenDto> toDtoList(List<TestGen> info) {
        return doAfterBatchDec(delegate.toDtoList(info));
    }
    @Override
    public List<TestGen> toDaoList(List<TestGenDto> info) {
        return doAfterBatchEnc(delegate.toDaoList(info));
    }


    List<TestGen> doAfterBatchEnc(List<TestGen> dao){
        List<String> all = combineStrings(dao.stream().map(TestGen::getName), dao.stream().map(TestGen::getGender));

        List<String> encrypted = enc.encrypt(all);
        int bound = dao.size();
        IntStream.range(0, bound).forEach(i -> {
            dao.get(i).setName(encrypted.get(i));
            dao.get(i).setGender(encrypted.get(i+bound));
        });
        return dao;
    }

    private List<String> combineStrings(Stream<String> stringStream, Stream<String> stringStream2) {
        List<String> all = new ArrayList<>();
        List<String> collectName = stringStream.toList();
        List<String> collectGender = stringStream2.toList();
        all.addAll(collectName);
        all.addAll(collectGender);
        return all;
    }


    TestGen doAfterSingle(TestGen dao){
        dao.setName(enc.encrypt(dao.getName()));
        dao.setName(enc.encrypt(dao.getGender()));
        return dao;
    }


    TestGenDto doAfterSingle(TestGenDto dto){
        dto.setName(enc.decrypt(dto.getName()));
        dto.setName(enc.decrypt(dto.getGender()));
        return dto;
    }


    List<TestGenDto> doAfterBatchDec(List<TestGenDto> dto){
        List<String> all = combineStrings(dto.stream().map(TestGenDto::getName), dto.stream().map(TestGenDto::getGender));
        List<String> decrypted = enc.decrypt(all);
        int bound = dto.size();
        IntStream.range(0, bound).forEach(i -> {
            dto.get(i).setName(decrypted.get(i));
            dto.get(i).setGender(decrypted.get(i+bound));
        });

        return dto;
    }


}
