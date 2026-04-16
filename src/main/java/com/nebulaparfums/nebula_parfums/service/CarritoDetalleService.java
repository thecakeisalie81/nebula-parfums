package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.CarritoDetalleDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.repository.ICarritoDetalleRepository;
import com.nebulaparfums.nebula_parfums.repository.ICarritoRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoDetalleService;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoDetalleService implements ICarritoDetalleService {
    @Autowired
    private ICarritoDetalleRepository  carritoDetalleRepository;


    @Autowired
    private IProductoService productoService;


    @Override
    public CarritoDetalle getCarritoDetalleById(Integer id) {
        CarritoDetalle carritoDetalle = carritoDetalleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto en el carrito"));;
        return carritoDetalle;
    }

    @Override
    public void saveCarritoDetalle(CarritoDetalle carritoDetalle) {
        carritoDetalleRepository.save(carritoDetalle);
    }

    @Override
    public void deleteCarritoDetalleById(Integer id) {
        carritoDetalleRepository.deleteById(id);
    }

    @Override
    public void editCarritoDetalle(CarritoDetalleDTO request) {

        CarritoDetalle carritoDetalle = getCarritoDetalleById(request.getId_carrito_detalle());
        Producto producto = productoService.getProductoById(request.getId_producto());

        carritoDetalle.setCantidad(request.getCantidad());
        carritoDetalle.setPrecio(producto.getPrecio() * request.getCantidad());

        if (carritoDetalle.getCantidad() > 0){
            this.saveCarritoDetalle(carritoDetalle);
        }else {
            deleteCarritoDetalleById(request.getId_carrito_detalle());
        }

    }
}
