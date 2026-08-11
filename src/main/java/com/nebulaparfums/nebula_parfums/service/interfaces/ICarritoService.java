package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.CarritoDTO;
import com.nebulaparfums.nebula_parfums.model.Carrito;


public interface ICarritoService {
    public Carrito getCarritoById(Integer id);
    public void deleteCarritoById(Integer id);
    public CarritoDTO editCarrito(Integer id, CarritoDTO carrito);
    public CarritoDTO saveCarrito(CarritoDTO carrito);
}
