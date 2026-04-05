package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.service.ICarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CarritoController {
    @Autowired
    private ICarritoService iCarritoService;

    @GetMapping("/carrito/buscar")
    public Carrito getCarrito(@RequestParam("id") Integer id) {
        return iCarritoService.getCarritoById(id);
    }

    @PostMapping("/carrito/crear")
    public String createCarrito(@RequestBody Carrito carrito) {
        iCarritoService.saveCarrito(carrito);
        return "Carrito creado";
    }

    @DeleteMapping("/carrito/borrar")
    public String deleteCarrito(@RequestParam("id") Integer id) {
        iCarritoService.deleteCarritoById(id);
        return "Carrito borrado";
    }

    @PutMapping("/carrito/editar")
    public String updateCarrito(@RequestBody Carrito carrito) {
        iCarritoService.editCarrito(carrito);
        return "Carrito editado";
    }


}
