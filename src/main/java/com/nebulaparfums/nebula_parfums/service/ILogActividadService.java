package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.LogActividad;

import java.util.List;

public interface ILogActividadService {
    public void saveLogActividad(LogActividad logActividad);
    public List<LogActividad> getLogsActividad();
}
