package com.merida.tecnm.market_backend_v2.persistence;

import com.merida.tecnm.market_backend_v2.domain.Purchase;
import com.merida.tecnm.market_backend_v2.domain.repository.PurchaseRepository;
import com.merida.tecnm.market_backend_v2.persistence.crud.CompraCrudRepository;
import com.merida.tecnm.market_backend_v2.persistence.entity.Compra;
import com.merida.tecnm.market_backend_v2.persistence.entity.CompraProducto;
import com.merida.tecnm.market_backend_v2.persistence.mapper.PurchaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CompraRepository implements PurchaseRepository {

    @Autowired
    private CompraCrudRepository compraCrudRepository;

    @Autowired
    private PurchaseMapper mapper;

    @Override
    public List<Purchase> getAll() {
        return mapper.toPurchases((List<Compra>) compraCrudRepository.findAll());
    }

    @Override
    public Optional<List<Purchase>> getByIdClient(String clientId) {
        return compraCrudRepository.findByIdCliente(clientId)
                .map(mapper::toPurchases);
    }

    @Override
    public Optional<Purchase> getPurchase(int purchaseId) {
        return compraCrudRepository.findById(purchaseId).map(mapper::toPurchase);
    }

    @Override
    public Purchase save(Purchase purchase) {
        Compra compra = mapper.toCompra(purchase);
        compra.getProductos().forEach(product -> product.setCompra(compra));
        return mapper.toPurchase(compraCrudRepository.save(compra));
    }

    @Override
    public Optional<Purchase> update(int purchaseId, Purchase purchase) {
        return compraCrudRepository.findById(purchaseId).map(compra -> {
            Compra updated = mapper.toCompra(purchase);
            compra.setIdCliente(updated.getIdCliente());
            compra.setMedioPago(updated.getMedioPago());
            compra.setComentario(updated.getComentario());

            Map<Integer, CompraProducto> currentItems = new HashMap<>();
            for (CompraProducto item : compra.getProductos()) {
                currentItems.put(item.getId().getIdProducto(), item);
            }
            for (CompraProducto item : updated.getProductos()) {
                CompraProducto current = currentItems.remove(item.getId().getIdProducto());
                if (current == null) {
                    item.setCompra(compra);
                    compra.getProductos().add(item);
                } else {
                    current.setCantidad(item.getCantidad());
                    current.setTotal(item.getTotal());
                }
            }
            compra.getProductos().removeAll(currentItems.values());
            return mapper.toPurchase(compraCrudRepository.save(compra));
        });
    }

    @Override
    public void delete(int purchaseId) {
        compraCrudRepository.deleteById(purchaseId);
    }
}