package com.bixuebihui.test.view.converter;

import com.bixuebihui.test.view.pojo.*;
import com.bixuebihui.test.view.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.bixuebihui.Base64Converter;

import java.util.List;

/**
* @author xwx
*/
@Mapper
public interface MyTestViewConverter extends Base64Converter<MyTestView, MyTestViewDto> {
    MyTestViewConverter MAPPER = Mappers.getMapper(MyTestViewConverter.class);

    //@Mapping(source = "optName", target = "optName", qualifiedByName = "decodeBase64")
    MyTestViewDto toDto(MyTestView info);

    MyTestView toDao(MyTestViewDto info);


    /*
        @AfterMapping
        public void doAfter(ShopDTO shopDTO, @MappingTarget ShopVo shopVo){
        List<FruitDTO> fruitDTOS = shopDTO.getFruitDTOS();
            shopVo.setHasFruit(fruitDTOS!=null&&!fruitDTOS.isEmpty());
            }
    */
}
