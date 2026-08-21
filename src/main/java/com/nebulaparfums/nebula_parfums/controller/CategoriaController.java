package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.CategoriaDTO;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categoria")
public class CategoriaController {
    private final ICategoriaService iCategoriaService;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaDTO>> getCategoria() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iCategoriaService.getCategorias());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<CategoriaDTO> buscarCategoria(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iCategoriaService.getCategoriaById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','EMPLEADO')")
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarCategoria(@PathVariable Integer id) {
        iCategoriaService.deleteCategoriaById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Categoria eliminada correctamente");
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','EMPLEADO')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<CategoriaDTO> editarCategoria(@PathVariable Integer id, @RequestBody CategoriaDTO categoria) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iCategoriaService.editCategoria(id, categoria));
    }


    @PreAuthorize("hasAnyRole('ADMINISTRADOR','EMPLEADO')")
    @PostMapping("/crear")
    public ResponseEntity<CategoriaDTO> crearCategoria(@RequestBody CategoriaDTO categoria) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iCategoriaService.saveCategoria(categoria));
    }
}
