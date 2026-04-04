package com.nebulaparfums.nebula_parfums.service;


import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;

import java.util.List;

public interface ICarritoDetalleService {
    public CarritoDetalle getCarritoDetalleById(Integer id);
    public void saveCarritoDetalle(CarritoDetalle carritoDetalle);
    public void deleteCarritoDetalleById(Integer id);
    public void editCarritoDetalle(CarritoDetalle carritoDetalle);
}
