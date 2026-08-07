package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.CategoriaDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.Categoria;
import com.nebulaparfums.nebula_parfums.repository.ICategoriaRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICategoriaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para gestionar las categorías.
 * Provee operaciones de consulta, guardado, eliminación, edición y conversión a DTO.
 */
@Service
@AllArgsConstructor
public class CategoriaService implements ICategoriaService {

    private final ICategoriaRepository categoriaRepository;

    /**
     * Guarda una nueva categoria en la db
     * @param categoria CategoriaDTO con los datos que se insertan en la nueva categoria
     * @return CategoriaDTO con los datos recién insertados
     */
    @Override
    public CategoriaDTO saveCategoria(CategoriaDTO categoria) {
        Categoria category = Categoria.builder()
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();

        return Mapper.toDTO(categoriaRepository.save(category));
    }

    /**
     *
     * @param id id de la categoria a editar
     * @param categoria CategoriaDTO que tiene los nuevos datos que se van a actualizar
     * @return CategoriaDTO con los datos actualizados
     */
    @Override
    public CategoriaDTO editCategoria(Integer id, CategoriaDTO categoria) {
        Categoria category = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoria"));

        category.setNombre(categoria.getNombre());
        category.setDescripcion(categoria.getDescripcion());
        return Mapper.toDTO(categoriaRepository.save(category));
    }

    /**
     * Borra una categoria físicamente de la db
     * @param id id de la categoria a eliminar
     */
    @Override
    public void deleteCategoriaById(Integer id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontró la categoria");
        }
    }

    @Override
    public CategoriaDTO getCategoriaById(Integer id) {
        return categoriaRepository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoria"));
    }

    @Override
    public List<CategoriaDTO> getCategorias() {
        return categoriaRepository.findAll().stream().map(Mapper::toDTO).toList();
    }
}
