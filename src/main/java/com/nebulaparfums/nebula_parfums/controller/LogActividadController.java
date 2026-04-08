package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LogActividadController {
    @Autowired
    private ILogActividadService iLogActividadService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("log/traer")
    public List<LogActividad> getLogsActividad() {
        return iLogActividadService.getLogsActividad();
    }

}
