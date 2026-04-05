package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Categoria;
import com.nebulaparfums.nebula_parfums.repository.ICategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService implements ICategoriaService{

    @Autowired
    private ICategoriaRepository categoriaRepository;

    @Override
    public void editCategoria(Categoria categoria) {
        this.saveCategoria(categoria);
    }

    @Override
    public void saveCategoria(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    @Override
    public void deleteCategoriaById(Integer id) {
        categoriaRepository.deleteById(id);
    }

    @Override
    public Categoria getCategoriaById(Integer id) {
        Categoria categoria = categoriaRepository.findById(id).get();
        return categoria;
    }

    @Override
    public List<Categoria> getCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();
        return categorias;
    }
}
