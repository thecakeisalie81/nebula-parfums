package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import com.nebulaparfums.nebula_parfums.repository.IDireccionEnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DireccionEnvioService implements IDireccionEnvioService{

    @Autowired
    private IDireccionEnvioRepository direccionEnvioRepository;

    @Override
    public DireccionEnvio getDireccionEnvioById(Integer id) {
        DireccionEnvio direccionEnvio = direccionEnvioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro la direccion de envio"));;
        return direccionEnvio;
    }

    @Override
    public void saveDireccionEnvio(DireccionEnvio direccionEnvio) {
        direccionEnvioRepository.save(direccionEnvio);
    }

    @Override
    public void deleteDireccionEnvioById(Integer id) {
        if (direccionEnvioRepository.existsById(id)) {
            direccionEnvioRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontro la direccion de envio");
        }
    }

    @Override
    public void editDireccionEnvio(DireccionEnvio direccionEnvio) {
        this.saveDireccionEnvio(direccionEnvio);
    }
}
