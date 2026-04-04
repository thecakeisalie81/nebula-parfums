package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.repository.ILogActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogActividadService implements ILogActividadService{
    @Autowired
    private ILogActividadRepository logActividadRepository;

    @Override
    public void saveLogActividad(LogActividad logActividad) {
        logActividadRepository.save(logActividad);
    }

    @Override
    public void deleteLogActividadById(Integer id) {
        logActividadRepository.deleteById(id);
    }

    @Override
    public void editLogActividad(LogActividad logActividad) {
        logActividadRepository.save(logActividad);
    }

    @Override
    public List<LogActividad> getLogsActividad() {
        List<LogActividad> logsActividad = logActividadRepository.findAll();
        return logsActividad;
    }
}
