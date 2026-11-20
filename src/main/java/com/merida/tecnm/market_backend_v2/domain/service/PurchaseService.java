package com.merida.tecnm.market_backend_v2.domain.service;

import com.merida.tecnm.market_backend_v2.domain.Purchase;
import com.merida.tecnm.market_backend_v2.domain.PurchaseRequest;
import com.merida.tecnm.market_backend_v2.domain.PurchaseItemRequest;
import com.merida.tecnm.market_backend_v2.domain.repository.PurchaseRepository;
import com.merida.tecnm.market_backend_v2.domain.repository.ClientRepository;
import com.merida.tecnm.market_backend_v2.domain.repository.ProductRepository;
import com.merida.tecnm.market_backend_v2.persistence.mapper.PurchaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseMapper mapper;

    @Transactional(readOnly = true)
    public List<Purchase> getAll(){
        return purchaseRepository.getAll();
    }

    @Transactional(readOnly = true)
    public Optional<Purchase> getPurchase(int purchaseId){
        return purchaseRepository.getPurchase(purchaseId);
    }

    @Transactional(readOnly = true)
    public Optional<List<Purchase>> getByClient(String clientId){
        return purchaseRepository.getByIdClient(clientId);
    }

    @Transactional
    public Purchase save(PurchaseRequest request){
        Purchase purchase = toPurchase(request);
        purchase.setDate(LocalDateTime.now());
        purchase.setState("A");
        return purchaseRepository.save(purchase);
    }

    @Transactional
    public Optional<Purchase> update(int purchaseId, PurchaseRequest request){
        if (getPurchase(purchaseId).isEmpty()) {
            return Optional.empty();
        }
        return purchaseRepository.update(purchaseId, toPurchase(request));
    }

    @Transactional
    public boolean delete(int purchaseId){
        if (getPurchase(purchaseId).isPresent()) {
            purchaseRepository.delete(purchaseId);
            return true;
        }
        return false;
    }

    private Purchase toPurchase(PurchaseRequest request){
        String clientId = request.getIdCliente();
        if (clientId == null || clientId.isBlank() || clientId.length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid client ID");
        }
        if (clientRepository.getClient(clientId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        String paymentMethod = getPaymentMethod(request.getMedioPago());
        if (request.getProductosAdquiridos() == null || request.getProductosAdquiridos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Products are required");
        }

        Set<Integer> productIds = new HashSet<>();
        for (PurchaseItemRequest item : request.getProductosAdquiridos()) {
            if (item == null || item.getIdProducto() == null || item.getIdProducto() <= 0
                    || item.getCantidad() == null || item.getCantidad() <= 0
                    || item.getTotal() == null || !Double.isFinite(item.getTotal()) || item.getTotal() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid purchase item");
            }
            if (!productIds.add(item.getIdProducto())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Repeated product in the purchase");
            }
            if (productRepository.getProduct(item.getIdProducto()).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }
        }
        Purchase purchase = mapper.toPurchaseRequest(request);
        purchase.setPaymentMethod(paymentMethod);
        return purchase;
    }

    private String getPaymentMethod(String paymentMethod){
        if (paymentMethod != null) {
            String value = paymentMethod.strip().toUpperCase(Locale.ROOT);
            if ("TARJETA".equals(value)) {
                return "T";
            }
            if ("EFECTIVO".equals(value)) {
                return "E";
            }
            if (value.matches("[A-Z]")) {
                return value;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment method");
    }
}
