package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.CarritoDTO;
import com.nebulaparfums.nebula_parfums.model.Carrito;


public interface ICarritoService {
    Carrito getCarritoById(Integer id);
    void deleteCarritoById(Integer id);
    CarritoDTO editCarrito(Integer id, CarritoDTO carrito);
    CarritoDTO saveCarrito(CarritoDTO carrito);
}
