package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;

public interface IDireccionEnvioService {
    public DireccionEnvio getDireccionEnvioById(Integer id);
    public void saveDireccionEnvio(DireccionEnvio direccionEnvio);
    public void deleteDireccionEnvioById(Integer id);
    public void editDireccionEnvio(DireccionEnvio direccionEnvio);
}
