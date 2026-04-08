package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class CarritoDetalleController {
    @Autowired
    private ICarritoDetalleService iCarritoDetalleService;

    @GetMapping("/carritodetalle/buscar")
    public CarritoDetalle getCarrito(@RequestParam("id") Integer id) {
        return iCarritoDetalleService.getCarritoDetalleById(id);
    }

    @PostMapping("/carritodetalle/crear")
    public String createCarritoDetalle(@RequestBody CarritoDetalle carritoDetalle) {
        iCarritoDetalleService.saveCarritoDetalle(carritoDetalle);
        return "Carrito detalle creado com sucesso";
    }

    @PutMapping("/carritodetalle/editar")
    public String editCarritoDetalle(@RequestBody CarritoDetalle carritoDetalle) {
        iCarritoDetalleService.editCarritoDetalle(carritoDetalle);
        return "Carrito detalle editado con sucesso";
    }

    @DeleteMapping("/carritodetalle/borrar")
    public String borrarCarritoDetalle(@RequestBody  CarritoDetalle carritoDetalle) {
        iCarritoDetalleService.editCarritoDetalle(carritoDetalle);
        return "Carrito detalle borrado con sucesso";
    }
}
