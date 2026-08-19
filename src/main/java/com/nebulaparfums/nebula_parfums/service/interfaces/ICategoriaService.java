package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.CategoriaDTO;
import com.nebulaparfums.nebula_parfums.model.Categoria;

import java.util.List;

public interface ICategoriaService {
    CategoriaDTO editCategoria(Integer id, CategoriaDTO categoria);
    CategoriaDTO saveCategoria(CategoriaDTO categoria);
    void deleteCategoriaById(Integer id);
    CategoriaDTO getCategoriaById(Integer id);
    List<CategoriaDTO> getCategorias();
}
