package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductoController {
    @Autowired
    private IProductoService iProductoService;

    @GetMapping("/producto/traer")
    public List<Producto> traerProductos() {
        return iProductoService.getProductos();
    }

    @GetMapping("/producto/resultados")
    public List<Producto> resultadoBusqueda(@RequestParam("nombre") String nombre) {
        return iProductoService.getProductosBusqueda(nombre);
    }

    @GetMapping("/producto/categoria")
    public List<Producto> productosPorCategoria(@RequestParam("categoria") Integer categoria) {
        return iProductoService.getProductosCategoria(categoria);
    }

    @GetMapping("/producto/buscar")
    public Producto buscarProducto(@RequestParam("id") Integer id) {
        return iProductoService.getProductoById(id);
    }

    @PutMapping("/producto/editar")
    public String editarProducto(@RequestBody Producto producto) {
        iProductoService.editProducto(producto);
        return "Producto editado con sucesso";
    }

    @DeleteMapping("/producto/borrar")
    public String borrarProducto(@RequestParam("id") Integer id) {
        iProductoService.deleteProducto(id);
        return "Producto eliminado con sucesso";
    }

    @PostMapping("/producto/crear")
    public String crearProducto(@RequestBody Producto producto) {
        iProductoService.saveProducto(producto);
        return "Producto Creado con sucesso";
    }
}
