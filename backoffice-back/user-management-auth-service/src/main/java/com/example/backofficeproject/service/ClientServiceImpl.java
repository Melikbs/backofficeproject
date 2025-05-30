package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.ClientDto;
import com.example.backofficeproject.mapper.ClientMapper;
import com.example.backofficeproject.model.Client;
import com.example.backofficeproject.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public List<ClientDto> getAll() {
        return clientRepository.findAll().stream()  // ✅ uniquement actifs
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<ClientDto> getAcheteurs() {
        return clientRepository.findAllAcheteurs()
                .stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deactivate(Long codeClient) {
        Client client = clientRepository.findById(codeClient)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        client.setActif(false);
        clientRepository.save(client);
    }

    @Override
    public List<ClientDto> getBlacklistedClients() {  // ✅ ajoutée
        return clientRepository.findAllByActifFalse()
                .stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void reactivateclient(Long codeClient) {
        Client client = clientRepository.findById(codeClient)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        client.setActif(true); // ✅ Set as active again
        clientRepository.save(client);
    }


}
