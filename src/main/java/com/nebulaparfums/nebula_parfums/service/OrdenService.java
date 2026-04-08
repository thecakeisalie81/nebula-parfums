package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Orden;
import com.nebulaparfums.nebula_parfums.repository.IOrdenRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenService implements IOrdenService {
    @Autowired
    private IOrdenRepository ordenRepository;


    @Override
    public List<Orden> getOrdenes() {
        List<Orden> ordenes = ordenRepository.findAll();
        return ordenes;
    }

    @Override
    public Orden getOrdenById(Integer ordenId) {
        Orden orden = ordenRepository.findById(ordenId).orElseThrow(() -> new ResourceNotFoundException("No se encontro la orden"));
        return orden;
    }

    @Override
    public void saveOrden(Orden orden) {
        ordenRepository.save(orden);
    }

    @Override
    public void deleteOrden(Integer ordenId) {
        if (ordenRepository.existsById(ordenId)) {
            ordenRepository.deleteById(ordenId);
        }else {
            throw new ResourceNotFoundException("No se encontro la orden");
        }
    }

    @Override
    public void editOrden(Orden orden) {
        this.saveOrden(orden);
    }
}
