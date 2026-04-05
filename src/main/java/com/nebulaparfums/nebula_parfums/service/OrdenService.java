package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Orden;
import com.nebulaparfums.nebula_parfums.repository.IOrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenService implements IOrdenService{
    @Autowired
    private IOrdenRepository ordenRepository;


    @Override
    public List<Orden> getOrdenes() {
        List<Orden> ordenes = ordenRepository.findAll();
        return ordenes;
    }

    @Override
    public Orden getOrdenById(Integer ordenId) {
        Orden orden = ordenRepository.findById(ordenId).get();
        return orden;
    }

    @Override
    public void saveOrden(Orden orden) {
        ordenRepository.save(orden);
    }

    @Override
    public void deleteOrden(Integer ordenId) {
        ordenRepository.deleteById(ordenId);
    }

    @Override
    public void editOrden(Orden orden) {
        this.saveOrden(orden);
    }
}
