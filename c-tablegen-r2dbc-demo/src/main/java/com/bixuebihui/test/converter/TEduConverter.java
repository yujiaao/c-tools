package com.bixuebihui.test.converter;

import com.bixuebihui.test.pojo.*;
import com.bixuebihui.test.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.bixuebihui.Base64Converter;

import java.util.List;

/**
* @author xwx
*/
@Mapper
public interface TEduConverter extends Base64Converter<TEdu, TEduDto> {
    TEduConverter MAPPER = Mappers.getMapper(TEduConverter.class);

    //@Mapping(source = "optName", target = "optName", qualifiedByName = "decodeBase64")
    TEduDto toDto(TEdu info);

    TEdu toDao(TEduDto info);


    /*
        @AfterMapping
        public void doAfter(ShopDTO shopDTO, @MappingTarget ShopVo shopVo){
        List<FruitDTO> fruitDTOS = shopDTO.getFruitDTOS();
            shopVo.setHasFruit(fruitDTOS!=null&&!fruitDTOS.isEmpty());
            }
    */
}
