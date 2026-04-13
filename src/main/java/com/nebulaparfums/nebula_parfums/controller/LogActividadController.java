package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("log/filtrar")
    public Page<LogActividad> filtrarLogs(Pageable pageable, String accion, LocalDate fechaInicio, LocalDate fechaFin) {
        return iLogActividadService.filtrarLogs(pageable, accion, fechaInicio, fechaFin);
    }

    @PostMapping("/log/logout")
    public void crearLogLogout(Authentication authentication) {
        iLogActividadService.saveLogout(authentication.getName());
    }
}
