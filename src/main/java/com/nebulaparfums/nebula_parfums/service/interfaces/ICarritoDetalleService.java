package com.nebulaparfums.nebula_parfums.service.interfaces;


import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;

public interface ICarritoDetalleService {
    public CarritoDetalle getCarritoDetalleById(Integer id);
    public void saveCarritoDetalle(CarritoDetalle carritoDetalle);
    public void deleteCarritoDetalleById(Integer id);
    public void editCarritoDetalle(CarritoDetalle carritoDetalle);
}
