package com.merida.tecnm.market_backend_v2.persistence;

import com.merida.tecnm.market_backend_v2.domain.Category;
import com.merida.tecnm.market_backend_v2.domain.repository.CategoryRepository;
import com.merida.tecnm.market_backend_v2.persistence.crud.CategoriaCrudRepository;
import com.merida.tecnm.market_backend_v2.persistence.entity.Categoria;
import com.merida.tecnm.market_backend_v2.persistence.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoriaRepository implements CategoryRepository {

    @Autowired
    private CategoriaCrudRepository categoryCrudRepository;

    @Autowired
    private CategoryMapper mapper;

    @Override
    public List<Category> getAll() {
        return mapper.toCategories((List<Categoria>) categoryCrudRepository.findAll());
    }

    @Override
    public Optional<Category> getCategory(int categoryId) {
        return categoryCrudRepository.findById(categoryId).map(mapper::toCategory);
    }

    @Override
    public Category save(Category category) {
        return mapper.toCategory(categoryCrudRepository.save(mapper.toCategoria(category)));
    }

    @Override
    public void delete(int categoryId) {
        categoryCrudRepository.deleteById(categoryId);
    }
}
