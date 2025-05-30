package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.ClientDto;

import java.util.List;

public interface ClientService {
    List<ClientDto> getAll();
    void deactivate(Long codeClient);
    List<ClientDto> getBlacklistedClients();
    void reactivateclient(Long codeClient);// ✅ ajoutée
    List<ClientDto> getAcheteurs();

}
