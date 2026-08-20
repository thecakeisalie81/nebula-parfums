package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.DireccionDTO;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;

public interface IDireccionEnvioService {
    DireccionDTO getDireccionEnvioById(Integer id);
    DireccionEnvio saveDireccion (DireccionEnvio direccionEnvio);
    void deleteDireccionEnvioById(Integer id);
    DireccionDTO editarDireccionEnvio(Integer id, DireccionDTO direccionEnvio);
}
