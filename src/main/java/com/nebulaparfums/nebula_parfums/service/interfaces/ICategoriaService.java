package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.CategoriaDTO;
import com.nebulaparfums.nebula_parfums.model.Categoria;

import java.util.List;

public interface ICategoriaService {
    public CategoriaDTO editCategoria(Integer id, CategoriaDTO categoria);
    public CategoriaDTO saveCategoria(CategoriaDTO categoria);
    public void deleteCategoriaById(Integer id);
    public CategoriaDTO getCategoriaById(Integer id);
    public List<CategoriaDTO> getCategorias();
}
