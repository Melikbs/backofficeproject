package com.example.backofficeproject.mapper;

import com.example.backofficeproject.Dto.StockDto;
import com.example.backofficeproject.model.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(source = "produit.codeProduit", target = "codeProduit")
    @Mapping(source = "couleur.codeCouleur", target = "codeCouleur")
    @Mapping(source = "couleur.libelle", target = "libelleCouleur")
    StockDto toDto(Stock stock);

    @Mapping(target = "produit", ignore = true)
    @Mapping(target = "couleur", ignore = true)
    Stock toEntity(StockDto dto);

    List<StockDto> toDtoList(List<Stock> stocks);
}
