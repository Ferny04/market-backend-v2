package com.merida.tecnm.market_backend_v2.persistence.mapper;


import com.merida.tecnm.market_backend_v2.domain.Category;
import com.merida.tecnm.market_backend_v2.persistence.entity.Categoria;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mappings({
            @Mapping(source = "idCategoria", target = "categoryId"),
            @Mapping(source = "descripcion", target = "category"),
            @Mapping(source = "estado", target = "active"),
    })
    Category toCategory(Categoria categoria);

    @InheritInverseConfiguration
    @Mapping(target = "productos", ignore = true)
    Category toCategoria(Category category);
}
