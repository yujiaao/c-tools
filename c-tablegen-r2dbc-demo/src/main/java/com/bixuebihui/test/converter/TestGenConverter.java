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
public interface TestGenConverter extends Base64Converter<TestGen, TestGenDto> {
    TestGenConverter MAPPER = Mappers.getMapper(TestGenConverter.class);

    //@Mapping(source = "optName", target = "optName", qualifiedByName = "decodeBase64")
    TestGenDto toDto(TestGen info);

    TestGen toDao(TestGenDto info);


    /*
        @AfterMapping
        public void doAfter(ShopDTO shopDTO, @MappingTarget ShopVo shopVo){
        List<FruitDTO> fruitDTOS = shopDTO.getFruitDTOS();
            shopVo.setHasFruit(fruitDTOS!=null&&!fruitDTOS.isEmpty());
            }
    */
}
