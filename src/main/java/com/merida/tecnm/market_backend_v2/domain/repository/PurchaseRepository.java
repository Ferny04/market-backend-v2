package com.merida.tecnm.market_backend_v2.domain.repository;

import com.merida.tecnm.market_backend_v2.domain.Purchase;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository {

    List<Purchase> getAll();
    Optional<List<Purchase>> getByIdClient(String clientId);
    Optional<Purchase> getPurchase(int purchaseId);
    Purchase save(Purchase purchase);
    Optional<Purchase> update(int purchaseId, Purchase purchase);
    void delete(int purchaseId);
}
