package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import com.nebulaparfums.nebula_parfums.repository.IOrdenDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdenDetalleService implements IOrdenDetalleService{
    @Autowired
    private IOrdenDetalleRepository ordenDetalleRepository;

    @Override
    public void saveOrdenDetalle(OrdenDetalle ordenDetalle) {
        ordenDetalleRepository.save(ordenDetalle);
    }

    @Override
    public void deleteOrdenDetalleById(Integer id) {
        ordenDetalleRepository.deleteById(id);
    }

    @Override
    public void editOrdenDetalle(OrdenDetalle ordenDetalle) {
        ordenDetalleRepository.save(ordenDetalle);
    }

    @Override
    public OrdenDetalle getOrdenDetalleById(Integer id) {
        OrdenDetalle ordenDetalle = ordenDetalleRepository.findById(id).orElse(null);
        return ordenDetalle;
    }
}
