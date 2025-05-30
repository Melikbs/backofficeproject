package com.example.backofficeproject.mapper;

import com.example.backofficeproject.Dto.MarqueDto;
import com.example.backofficeproject.model.Marque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MarqueMapper {

    @Mapping(source = "gamme.codeGamme", target = "codeGamme") // Suppression de cette ligne
    MarqueDto toDto(Marque marque);

    @Mapping(target = "gamme", ignore = true) // Et cette ligne
    Marque toEntity(MarqueDto dto);

    List<MarqueDto> toDtoList(List<Marque> marques);
}
