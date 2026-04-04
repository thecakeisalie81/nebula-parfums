package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.repository.ICarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService implements ICarritoService {

    @Autowired
    private ICarritoRepository carritoRepository;

    @Override
    public Carrito getCarritoById(Integer id) {
        Carrito carrito = carritoRepository.findById(id).orElse(null);
        return carrito;
    }

    @Override
    public void deleteCarritoById(Integer id) {
        carritoRepository.deleteById(id);
    }

    @Override
    public void editCarrito(Carrito carrito) {
        carritoRepository.save(carrito);
    }

    @Override
    public void saveCarrito(Carrito carrito) {
        carritoRepository.save(carrito);
    }
}
