package com.bixuebihui.test.converters;

import com.bixuebihui.test.pojo.*;
import com.bixuebihui.test.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
* @author xwx
*/
@Mapper
public interface TEduConverter {

    TEduConverter MAPPER = Mappers.getMapper(TEduConverter.class);

    //@Mapping(source = "status", target = "orderStatus")
    TEduDto toDto(TEdu info);
    TEdu toDao(TEduDto info);

}

