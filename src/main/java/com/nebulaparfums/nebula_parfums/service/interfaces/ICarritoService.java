package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.model.Carrito;


public interface ICarritoService {
    public Carrito getCarritoById(Integer id);
    public void deleteCarritoById(Integer id);
    public void editCarrito(Carrito carrito);
    public void saveCarrito(Carrito carrito);
}
