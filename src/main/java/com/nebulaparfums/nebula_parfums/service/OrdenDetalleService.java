package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
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
        if (ordenDetalleRepository.existsById(id)) {
            ordenDetalleRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontro el producto en la orden");
        }
    }

    @Override
    public void editOrdenDetalle(OrdenDetalle ordenDetalle) {
        this.saveOrdenDetalle(ordenDetalle);
    }

    @Override
    public OrdenDetalle getOrdenDetalleById(Integer id) {
        OrdenDetalle ordenDetalle = ordenDetalleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto en la orden"));;
        return ordenDetalle;
    }
}
