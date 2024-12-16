package com.bixuebihui.test.converter;

import com.bixuebihui.test.dto.TestGenDto;
import com.bixuebihui.test.pojo.TestGen;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public abstract class TestGenConverterDecorator implements TestGenConverter {
    @Qualifier("delegate")
    protected final TestGenConverter delegate;

    protected TestGenConverterDecorator(TestGenConverter delegate) {
        this.delegate = delegate;
    }


    @Override
    public TestGenDto toDto(TestGen info) {
        return delegate.toDto(info);
    }

    @Override
    public TestGen toDao(TestGenDto info) {
        return delegate.toDao(info);
    }

    @Override
    public List<TestGenDto> toDtoList(List<TestGen> info) {
        return delegate.toDtoList(info);
    }

}
