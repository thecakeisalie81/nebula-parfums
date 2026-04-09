package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.OrdendetalleDTO;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;

public interface IOrdenDetalleService {
    public void saveOrdenDetalle(OrdenDetalle ordenDetalle);
    public void deleteOrdenDetalleById(Integer id);
    public void editOrdenDetalle(OrdenDetalle ordenDetalle);
    public OrdenDetalle getOrdenDetalleById(Integer id);
    public void createOrdenDetalle(OrdendetalleDTO detalleDTO);
}
