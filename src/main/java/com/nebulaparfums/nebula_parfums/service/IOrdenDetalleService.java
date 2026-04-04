package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;

import java.util.List;

public interface IOrdenDetalleService {
    public void saveOrdenDetalle(OrdenDetalle ordenDetalle);
    public void deleteOrdenDetalleById(Integer id);
    public void editOrdenDetalle(OrdenDetalle ordenDetalle);
    public OrdenDetalle getOrdenDetalleById(Integer id);
}
