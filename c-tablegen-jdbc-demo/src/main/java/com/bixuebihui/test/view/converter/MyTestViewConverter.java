package com.bixuebihui.test.view.converter;

import com.bixuebihui.test.view.pojo.*;
import com.bixuebihui.test.view.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* @author xwx
*/
@Mapper
public interface MyTestViewConverter {
    MyTestViewConverter MAPPER = Mappers.getMapper(MyTestViewConverter.class);

    //@Mapping(source = "status", target = "orderStatus")
    MyTestViewDto toDto(MyTestView info);

    MyTestView toDao(MyTestViewDto info);

    List<MyTestViewDto> toDtoList(List<MyTestView> info);

    List<MyTestView> toDaoList(List<MyTestViewDto> info);

    /*
        @AfterMapping
        public void doAfter(ShopDTO shopDTO, @MappingTarget ShopVo shopVo){
        List<FruitDTO> fruitDTOS = shopDTO.getFruitDTOS();
            shopVo.setHasFruit(fruitDTOS!=null&&!fruitDTOS.isEmpty());
            }
    */
}

