package com.merida.tecnm.market_backend_v2.persistence;

import com.merida.tecnm.market_backend_v2.domain.Product;
import com.merida.tecnm.market_backend_v2.domain.repository.ProductRepository;
import com.merida.tecnm.market_backend_v2.persistence.crud.ProductoCrudRepository;
import com.merida.tecnm.market_backend_v2.persistence.crud.CategoriaCrudRepository;
import com.merida.tecnm.market_backend_v2.persistence.entity.Producto;
import com.merida.tecnm.market_backend_v2.persistence.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductoRepository implements ProductRepository {

    @Autowired
    private ProductoCrudRepository productoCrudRepository;

    @Autowired
    private CategoriaCrudRepository categoriaCrudRepository;

    @Autowired
    private ProductMapper mapper;

    @Override
    public List<Product> getAll(){
        return mapper.toProducts((List<Producto>) productoCrudRepository.findAll());
    }

    @Override
    public Optional<List<Product>> getByCategory(int idCategoria){
        return Optional.of(mapper.toProducts(productoCrudRepository.findByIdCategoriaOrderByNombreAsc(idCategoria)));
    }

    @Override
    public Optional<List<Product>> getScarceProducts(int cantidad){
        return productoCrudRepository.findByCantidadStockLessThanAndEstado(cantidad, true)
                .map(mapper::toProducts);
    }

    @Override
    public Optional<Product> getProduct(int idProducto){
        return productoCrudRepository.findById(idProducto).map(mapper::toProduct);
    }

    @Override
    public Product save(Product product){
        return saveProducto(mapper.toProducto(product));
    }

    @Override
    public Optional<Product> update(int idProducto, Product product){
        return productoCrudRepository.findById(idProducto).map(producto -> {
            mapper.updateProducto(product, producto);
            return saveProducto(producto);
        });
    }

    private Product saveProducto(Producto producto){
        producto.setCategoria(producto.getIdCategoria() == null ? null
                : categoriaCrudRepository.findById(producto.getIdCategoria()).orElse(null));
        return mapper.toProduct(productoCrudRepository.save(producto));
    }

    @Override
    public void delete(int idProducto){
        productoCrudRepository.deleteById(idProducto);
    }
}
