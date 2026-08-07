package com.nebulaparfums.nebula_parfums.service.interfaces;


import com.nebulaparfums.nebula_parfums.dto.CarritoDetalleDTO;
import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;

public interface ICarritoDetalleService {
    public CarritoDetalleDTO getCarritoDetalleById(Integer id);
    public CarritoDetalleDTO saveCarritoDetalle(CarritoDetalleDTO carritoDetalle);
    public void
    deleteCarritoDetalleById(Integer id);
    public void editCarritoDetalle(Integer id, CarritoDetalleDTO carritoDetalle);
}
