package com.example.backofficeproject.mapper;

import com.example.backofficeproject.Dto.CouleurDto;
import com.example.backofficeproject.model.Couleur;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouleurMapper {

    CouleurDto toDto(Couleur couleur);

    Couleur toEntity(CouleurDto dto);

    List<CouleurDto> toDtoList(List<Couleur> couleurs);
}
