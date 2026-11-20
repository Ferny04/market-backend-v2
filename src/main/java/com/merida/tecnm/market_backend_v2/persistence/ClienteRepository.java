package com.merida.tecnm.market_backend_v2.persistence;

import com.merida.tecnm.market_backend_v2.domain.Client;
import com.merida.tecnm.market_backend_v2.domain.repository.ClientRepository;
import com.merida.tecnm.market_backend_v2.persistence.crud.ClienteCrudRepository;
import com.merida.tecnm.market_backend_v2.persistence.entity.Cliente;
import com.merida.tecnm.market_backend_v2.persistence.mapper.ClientMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepository implements ClientRepository {

    @Autowired
    private ClienteCrudRepository clientCrudRepository;

    @Autowired
    private ClientMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Client> getAll() {
        return mapper.toClients((List<Cliente>) clientCrudRepository.findAll());
    }

    @Override
    public Optional<Client> getClient(String clientId) {
        return clientCrudRepository.findById(clientId).map(mapper::toClient);
    }

    @Override
    public Client create(Client client) {
        Cliente cliente = mapper.toCliente(client);
        entityManager.persist(cliente);
        return mapper.toClient(cliente);
    }

    @Override
    public Client save(Client client) {
        return mapper.toClient(clientCrudRepository.save(mapper.toCliente(client)));
    }

    @Override
    public void delete(String clientId) {
        clientCrudRepository.deleteById(clientId);
    }
}
