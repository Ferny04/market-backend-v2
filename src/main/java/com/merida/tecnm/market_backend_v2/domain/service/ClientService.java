package com.merida.tecnm.market_backend_v2.domain.service;

import com.merida.tecnm.market_backend_v2.domain.Client;
import com.merida.tecnm.market_backend_v2.domain.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAll() {
        return clientRepository.getAll();
    }

    public Optional<Client> getClient(String clientId) {
        return clientRepository.getClient(clientId);
    }

    @Transactional
    public Client save(Client client) {
        if (client.getClientId() == null || client.getClientId().isBlank() || client.getClientId().length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A client ID of up to 20 characters is required");
        }
        if (getClient(client.getClientId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client ID already exists");
        }
        return clientRepository.create(client);
    }

    @Transactional
    public Optional<Client> update(String clientId, Client client) {
        return getClient(clientId).map(current -> {
            client.setClientId(clientId);
            return clientRepository.save(client);
        });
    }

    @Transactional
    public boolean delete(String clientId) {
        if (getClient(clientId).isPresent()) {
            clientRepository.delete(clientId);
            return true;
        }
        return false;
    }
}
