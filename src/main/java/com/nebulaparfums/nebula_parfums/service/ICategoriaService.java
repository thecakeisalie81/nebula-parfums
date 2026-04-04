package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Categoria;

import java.util.List;

public interface ICategoriaService {
    public void editCategoria(Categoria categoria);
    public void saveCategoria(Categoria categoria);
    public void deleteCategoriaById(Integer id);
    public Categoria getCategoriaById(Integer id);
    public List<Categoria> getCategorias();
}
