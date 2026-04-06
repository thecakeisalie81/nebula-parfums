package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;
import com.nebulaparfums.nebula_parfums.repository.ICarritoDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoDetalleService implements ICarritoDetalleService{
    @Autowired
    private ICarritoDetalleRepository  carritoDetalleRepository;

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
        if (carritoDetalleRepository.existsById(id)) {
            carritoDetalleRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontro el producto en el carrito");
        }
    }

    @Override
    public void editCarritoDetalle(CarritoDetalle carritoDetalle) {
        this.saveCarritoDetalle(carritoDetalle);
    }
}
