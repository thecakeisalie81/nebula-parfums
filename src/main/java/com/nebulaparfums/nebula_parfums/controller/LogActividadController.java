package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.service.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.LogActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LogActividadController {
    @Autowired
    private ILogActividadService iLogActividadService;

    @GetMapping("log/traer")
    public List<LogActividad> getLogsActividad() {
        return iLogActividadService.getLogsActividad();
    }

    @PostMapping
    public String crearLogActividad(@RequestBody LogActividad logActividad) {
        iLogActividadService.saveLogActividad(logActividad);
        return "LogActividad creado con sucesso";
    }
}
