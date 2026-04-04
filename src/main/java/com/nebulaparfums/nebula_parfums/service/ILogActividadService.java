package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.LogActividad;

import java.util.List;

public interface ILogActividadService {
    public void saveLogActividad(LogActividad logActividad);
    public void deleteLogActividadById(Integer id);
    public void editLogActividad(LogActividad logActividad);
    public List<LogActividad> getLogsActividad();
}
