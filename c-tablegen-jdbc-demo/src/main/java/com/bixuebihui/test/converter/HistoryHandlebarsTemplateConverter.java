package com.bixuebihui.test.converters;

import com.bixuebihui.test.pojo.*;
import com.bixuebihui.test.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
* @author xwx
*/
@Mapper
public interface HistoryHandlebarsTemplateConverter {

    HistoryHandlebarsTemplateConverter MAPPER = Mappers.getMapper(HistoryHandlebarsTemplateConverter.class);

    //@Mapping(source = "status", target = "orderStatus")
    HistoryHandlebarsTemplateDto toDto(HistoryHandlebarsTemplate info);
    HistoryHandlebarsTemplate toDao(HistoryHandlebarsTemplateDto info);

}

