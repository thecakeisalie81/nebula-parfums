package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.DireccionDTO;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;

public interface IDireccionEnvioService {
    DireccionDTO getDireccionEnvioById(Integer id);
    DireccionDTO saveDireccionEnvio(DireccionDTO direccionEnvio);
    void deleteDireccionEnvioById(Integer id);
    DireccionDTO editarDireccionEnvio(Integer id, DireccionDTO direccionEnvio);
}
