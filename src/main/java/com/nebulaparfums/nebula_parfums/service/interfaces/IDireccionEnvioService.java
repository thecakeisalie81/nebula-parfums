package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.DireccionDTO;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;

public interface IDireccionEnvioService {
    public DireccionDTO getDireccionEnvioById(Integer id);
    public DireccionDTO saveDireccionEnvio(DireccionDTO direccionEnvio);
    public void deleteDireccionEnvioById(Integer id);
    public DireccionDTO editarDireccionEnvio(Integer id, DireccionDTO direccionEnvio);
}
