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
public interface TestGenConverter {
    TestGenConverter MAPPER = Mappers.getMapper(TestGenConverter.class);

    //@Mapping(source = "status", target = "orderStatus")
    TestGenDto toDto(TestGen info);

    TestGen toDao(TestGenDto info);

    List<TestGenDto> toDtoList(List<TestGen> info);

    List<TestGen> toDaoList(List<TestGenDto> info);

    /*
        @AfterMapping
        public void doAfter(ShopDTO shopDTO, @MappingTarget ShopVo shopVo){
        List<FruitDTO> fruitDTOS = shopDTO.getFruitDTOS();
            shopVo.setHasFruit(fruitDTOS!=null&&!fruitDTOS.isEmpty());
            }
    */
}

