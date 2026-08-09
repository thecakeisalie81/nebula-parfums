package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.LogDTO;
import com.nebulaparfums.nebula_parfums.dto.TotalEventosyHoyDTO;
import com.nebulaparfums.nebula_parfums.model.LogActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ILogActividadService {
    public TotalEventosyHoyDTO getTotalEventosyHoyDTO();
    public LogDTO saveLogActividad(LogDTO logActividad);
    public List<LogDTO> getLogsActividad();
    public Page<LogDTO> filtrarLogs(Pageable pageable, String accion, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    public List<LogDTO> filtrarLogsPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    byte[] exportarLogsPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
