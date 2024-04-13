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
public interface TEduConverter {
    TEduConverter MAPPER = Mappers.getMapper(TEduConverter.class);

    //@Mapping(source = "status", target = "orderStatus")
    TEduDto toDto(TEdu info);

    TEdu toDao(TEduDto info);

    List<TEduDto> toDtoList(List<TEdu> info);

    List<TEdu> toDaoList(List<TEduDto> info);

    /*
        @AfterMapping
        public void doAfter(ShopDTO shopDTO, @MappingTarget ShopVo shopVo){
        List<FruitDTO> fruitDTOS = shopDTO.getFruitDTOS();
            shopVo.setHasFruit(fruitDTOS!=null&&!fruitDTOS.isEmpty());
            }
    */
}

