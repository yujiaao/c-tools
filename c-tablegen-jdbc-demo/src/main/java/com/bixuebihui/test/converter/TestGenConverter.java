package com.bixuebihui.test.converters;

import com.bixuebihui.test.pojo.*;
import com.bixuebihui.test.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
* @author xwx
*/
@Mapper
public interface TestGenConverter {

    TestGenConverter MAPPER = Mappers.getMapper(TestGenConverter.class);

    //@Mapping(source = "status", target = "orderStatus")
    TestGenDto toDto(TestGen info);
    TestGen toDao(TestGenDto info);

}

