package com.example.backofficeproject.mapper;

import com.example.backofficeproject.Dto.ClientDto;
import com.example.backofficeproject.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDto toDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setCodeClient(client.getCodeClient());
        dto.setUsername(client.getUsername());
        dto.setNom(client.getNom());
        dto.setPrenom(client.getPrenom());
        dto.setEmail(client.getEmail());
        dto.setTel(client.getTel());
        dto.setCin(client.getCin());
        dto.setRue(client.getRue());
        dto.setVille(client.getVille());
        dto.setCodePostal(client.getCodePostal());
        dto.setActif(client.isActif());
        return dto;
    }

    public Client toEntity(ClientDto dto) {
        Client client = new Client();
        client.setCodeClient(dto.getCodeClient());
        client.setUsername(dto.getUsername());
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setTel(dto.getTel());
        client.setCin(dto.getCin());
        client.setRue(dto.getRue());
        client.setVille(dto.getVille());
        client.setCodePostal(dto.getCodePostal());
        client.setActif(dto.isActif());
        return client;
    }
}