package com.example.backofficeproject.mapper;

import com.example.backofficeproject.Dto.GammeDto;
import com.example.backofficeproject.model.Gamme;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface GammeMapper {
 // très important car sinon problème de lazy loading
    GammeDto toDto(Gamme gamme);

    @Mapping(target = "marques", ignore = true)
    Gamme toEntity(GammeDto dto);
    List<GammeDto> toDtoList(List<Gamme> gammes);
}