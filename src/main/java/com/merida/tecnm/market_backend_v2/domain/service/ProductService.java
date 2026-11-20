package com.merida.tecnm.market_backend_v2.domain.service;

import com.merida.tecnm.market_backend_v2.domain.Product;
import com.merida.tecnm.market_backend_v2.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAll(){
        return productRepository.getAll();
    }

    public Optional<Product> getProduct(int productId){
        return productRepository.getProduct(productId);
    }

    public Optional<List<Product>> getByCategory(int categoryId){
        return productRepository.getByCategory(categoryId);
    }

    @Transactional
    public Product save(Product product){
        product.setProductId(null);
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> update(int productId, Product product){
        return productRepository.update(productId, product);
    }

    @Transactional
    public boolean delete(int productId){
        // verificar que existe antes de borrarlo
        if(getProduct(productId).isPresent()) {
            productRepository.delete(productId);
            return true;
        }
        return false;
    }
}
