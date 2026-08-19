package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.LogDTO;
import com.nebulaparfums.nebula_parfums.dto.TotalEventosyHoyDTO;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log")
public class LogActividadController {
    private final ILogActividadService iLogActividadService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/logs")
    public ResponseEntity<List<LogDTO>> getLogsActividad() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iLogActividadService.getLogsActividad());
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Page<LogDTO>> filtrarLogs(Pageable pageable, String accion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iLogActividadService.filtrarLogs(pageable, accion, fechaInicio, fechaFin));
    }

    @GetMapping("/totales")
    public ResponseEntity<TotalEventosyHoyDTO> getTotalEventosHoyDTO() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iLogActividadService.getTotalEventosyHoyDTO());
    }

    @PostMapping("/crear")
    public ResponseEntity<LogDTO> crearLog(LogDTO logActividad) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(iLogActividadService.saveLogActividad(logActividad));
    }
}
