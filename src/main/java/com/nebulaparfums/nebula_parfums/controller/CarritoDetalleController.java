package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.CarritoDetalleDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoDetalleService;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class CarritoDetalleController {
    @Autowired
    private ICarritoDetalleService iCarritoDetalleService;


    @Autowired
    private IProductoService iProductoService;

    @Autowired
    private ICarritoService iCarritoService;

    @GetMapping("/carritodetalle/buscar")
    public CarritoDetalle getCarrito(@RequestParam("id") Integer id) {
        return iCarritoDetalleService.getCarritoDetalleById(id);
    }

    @PostMapping("/carritodetalle/crear")
    public String createCarritoDetalle(@RequestBody CarritoDetalleDTO request) {
        Carrito carrito = iCarritoService.getCarritoById(request.getId_carrito());
        Producto producto = iProductoService.getProductoById(request.getId_producto());

        CarritoDetalle detalle = new CarritoDetalle();
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecio(request.getPrecio());
        detalle.setCarrito(carrito);
        detalle.setProducto(producto);

        iCarritoDetalleService.saveCarritoDetalle(detalle);
        return "Carrito detalle creado con sucesso";
    }


    @PutMapping("/carritodetalle/editar")
    public String editCarritoDetalle(@RequestBody CarritoDetalleDTO request) {
        iCarritoDetalleService.editCarritoDetalle(request);
        return "Carrito detalle editado con sucesso";
    }

    @DeleteMapping("/carritodetalle/borrar")
    public String borrarCarritoDetalle(@RequestBody  Integer id) {
        iCarritoDetalleService.deleteCarritoDetalleById(id);
        return "Carrito detalle borrado con sucesso";
    }
}
