package com.nebulaparfums.nebula_parfums.service.interfaces;


import com.nebulaparfums.nebula_parfums.dto.CarritoDetalleDTO;
import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;

public interface ICarritoDetalleService {
    CarritoDetalle getCarritoDetalleById(Integer id);
    CarritoDetalleDTO saveCarritoDetalle(CarritoDetalleDTO carritoDetalle);
    void deleteCarritoDetalleById(Integer id);
    CarritoDetalleDTO editCarritoDetalle(Integer id, CarritoDetalleDTO carritoDetalle);
}
