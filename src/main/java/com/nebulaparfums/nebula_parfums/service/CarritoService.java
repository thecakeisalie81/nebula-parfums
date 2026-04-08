package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.repository.ICarritoRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService implements ICarritoService {

    @Autowired
    private ICarritoRepository carritoRepository;

    @Override
    public Carrito getCarritoById(Integer id) {
        Carrito carrito = carritoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el carrito"));;
        return carrito;
    }

    @Override
    public void deleteCarritoById(Integer id) {
        if (carritoRepository.existsById(id)) {
            carritoRepository.deleteById(id);
        }
        else {
            throw new ResourceNotFoundException("No se encontro el Carrito");
        }
    }

    @Override
    public void editCarrito(Carrito carrito) {
        this.saveCarrito(carrito);
    }

    @Override
    public void saveCarrito(Carrito carrito) {
        carritoRepository.save(carrito);
    }
}
