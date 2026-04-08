package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.Categoria;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoriaController {
    @Autowired
    private ICategoriaService iCategoriaService;

    @GetMapping("categoria/traer")
    public List<Categoria> getCategoria() {
        return iCategoriaService.getCategorias();
    }

    @GetMapping("/categoria/buscar")
    public Categoria buscarCategoria(@RequestParam("id") Integer id) {
        return iCategoriaService.getCategoriaById(id);
    }

    @DeleteMapping("/categoria/borrar")
    public String borrarCategoria(@RequestParam("id") Integer id) {
        iCategoriaService.deleteCategoriaById(id);
        return "Categoria borrado con sucesso";
    }

    @PutMapping("/categoria/editar")
    public String editarCategoria(@RequestBody Categoria categoria) {
        iCategoriaService.editCategoria(categoria);
        return "Categoria editado con sucesso";
    }

    @PostMapping("/categoria/crear")
    public String crearCategoria(@RequestBody Categoria categoria) {
        iCategoriaService.saveCategoria(categoria);
        return "Categoria creado con sucesso";
    }
}
