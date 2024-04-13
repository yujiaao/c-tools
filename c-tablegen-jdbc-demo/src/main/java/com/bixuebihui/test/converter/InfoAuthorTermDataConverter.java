package com.bixuebihui.test.converter;

import com.bixuebihui.test.pojo.*;
import com.bixuebihui.test.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* @author xwx
*/
@Mapper
public interface InfoAuthorTermDataConverter {
    InfoAuthorTermDataConverter MAPPER = Mappers.getMapper(InfoAuthorTermDataConverter.class);

    //@Mapping(source = "status", target = "orderStatus")
    InfoAuthorTermDataDto toDto(InfoAuthorTermData info);

    InfoAuthorTermData toDao(InfoAuthorTermDataDto info);

    List<InfoAuthorTermDataDto> toDtoList(List<InfoAuthorTermData> info);

    List<InfoAuthorTermData> toDaoList(List<InfoAuthorTermDataDto> info);

    /*
        @AfterMapping
        public void doAfter(ShopDTO shopDTO, @MappingTarget ShopVo shopVo){
        List<FruitDTO> fruitDTOS = shopDTO.getFruitDTOS();
            shopVo.setHasFruit(fruitDTOS!=null&&!fruitDTOS.isEmpty());
            }
    */
}

